package com.chaocraft.client.dev;

import com.chaocraft.client.render.ChaoRenderer;
import com.chaocraft.client.render.debug.ChaoRenderMetrics;
import com.chaocraft.client.perf.ChaoPerformanceProfiler;
import com.chaocraft.entity.ChaoEntity;
import com.chaocraft.entity.ModEntities;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoAppearanceState.EvolutionChannel;
import com.chaocraft.visual.ChaoColorType;
import com.chaocraft.visual.ChaoReflectionType;
import com.chaocraft.visual.ChaoAnimalType;
import com.chaocraft.visual.ChaoAnimalParts;
import com.chaocraft.visual.ChaoAnimalParts.Slot;
import com.chaocraft.client.render.animal.ChaoAnimalPartCatalog;
import com.chaocraft.visual.ChaoVisualType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleConsumer;
import java.util.function.UnaryOperator;

/**
 * In-game Chao Viewer parity lab.
 *
 * <p>The lab owns one temporary client-only Chao and never edits Chao entities
 * already living in the world. The preview still goes through the exact production
 * ChaoRenderer. Only explicit Summon/Matrix actions cross to the authoritative
 * server, which keeps this debug UI isolated from gameplay state.</p>
 */
public final class ChaoVisualLabScreen extends Screen {
    private static final int OUTER_MARGIN = 4;
    private static final int COLUMN_GAP = 4;
    private static final int WIDGET_HEIGHT = 16;
    private static final int GAP = 2;
    private static final int MIN_CONTROL_WIDTH = 150;
    private static final int MAX_CONTROL_WIDTH = 280;
    private static final int MIN_PREVIEW_WIDTH = 138;
    private static final int PREVIEW_SETTLE_TICKS = 3;
    private static final int PREVIEW_FULL_BRIGHT = 0x00F000F0;
    private static final float PREVIEW_SCALE_MIN = 0.65F;
    private static final float PREVIEW_SCALE_MAX = 1.65F;
    private static final float PREVIEW_SCALE_STEP = 0.15F;

    private ChaoAppearanceState draft = ChaoAppearanceState.DEFAULT;
    private ChaoVisualType lastAdultType = ChaoVisualType.NORMAL;
    private int presetIndex = 0;
    private LabTab activeTab = LabTab.BODY;

    private LabSlider swimSlider;
    private LabSlider flySlider;
    private LabSlider runSlider;
    private LabSlider powerSlider;

    private ChaoEntity previewEntity;
    private boolean previewDirty;
    private int previewSettleTicks;
    private float previewYaw;
    private boolean autoRotate = true;
    private boolean previewDragging;
    private float previewScale = 1.0F;
    private double lastPreviewMouseX;
    private ButtonWidget autoRotateButton;
    private String status = "";
    private long statusUntil;

    public ChaoVisualLabScreen() {
        super(Text.literal("ChaoCraft Visual Lab"));
    }

    @Override
    protected void init() {
        if (client == null) {
            return;
        }

        ensurePreviewEntity();
        syncPreview();

        LabLayout layout = layout();
        int x = layout.controlsX();
        int y = 28;
        int full = layout.controlsWidth();
        int half = (full - GAP) / 2;

        int tabWidth = (full - GAP * 3) / 4;
        addTabButton(LabTab.BODY, x, y, tabWidth);
        addTabButton(LabTab.FACE, x + tabWidth + GAP, y, tabWidth);
        addTabButton(LabTab.PARTS, x + (tabWidth + GAP) * 2, y, tabWidth);
        addTabButton(LabTab.TEST, x + (tabWidth + GAP) * 3, y, full - (tabWidth + GAP) * 3);
        y += WIDGET_HEIGHT + GAP + 3;

        switch (activeTab) {
            case BODY -> initBodyTab(x, y, full, half);
            case FACE -> initFaceTab(x, y, full, half);
            case PARTS -> initPartsTab(x, y, full, half);
            case TEST -> initTestTab(x, y, full, half);
        }

        // Preview controls live inside the preview column and never mutate an
        // existing world Chao. Summon is the explicit server-authoritative bridge.
        int px = layout.previewX() + 6;
        int pWidth = Math.max(40, layout.previewWidth() - 12);
        int pHalf = (pWidth - GAP) / 2;
        autoRotateButton = addDrawableChild(ButtonWidget.builder(autoRotateLabel(), button -> toggleAutoRotate())
                .dimensions(px, 27, pHalf, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Summon Chao"), button -> summonDraft())
                .dimensions(px + pHalf + GAP, 27, pWidth - pHalf - GAP, WIDGET_HEIGHT).build());

        int zoomWidth = Math.max(30, (pWidth - GAP) / 2);
        addDrawableChild(ButtonWidget.builder(Text.literal("Zoom -"), button -> adjustPreviewZoom(-PREVIEW_SCALE_STEP))
                .dimensions(px, 27 + WIDGET_HEIGHT + GAP, zoomWidth, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Zoom +"), button -> adjustPreviewZoom(PREVIEW_SCALE_STEP))
                .dimensions(px + zoomWidth + GAP, 27 + WIDGET_HEIGHT + GAP, pWidth - zoomWidth - GAP, WIDGET_HEIGHT).build());
    }

    private void initBodyTab(int x, int y, int full, int half) {
        addDrawableChild(ButtonWidget.builder(stageLabel(), button -> toggleStage())
                .dimensions(x, y, half, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Type: " + adultTypeLabel()), button -> cycleAdultType())
                .dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP;

        addDrawableChild(ButtonWidget.builder(Text.literal("Color: " + colorLabel()), button -> cycleColor())
                .dimensions(x, y, half, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Tone: " + (draft.monotone() ? "Monotone" : "Two-Tone")), button -> toggleTone())
                .dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP;
        addDrawableChild(ButtonWidget.builder(Text.literal("Reflection: " + reflectionLabel()), button -> cycleReflection())
                .dimensions(x, y, full, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP;

        addSlider(new LabSlider(x, y, full, "Alignment", -100.0D, 100.0D, draft.alignment(), 0,
                value -> update(state -> state.withAlignment((float) value), false)));
        y += WIDGET_HEIGHT + GAP;
        addSlider(new LabSlider(x, y, full, "Age", 0.0D, 1.0D, draft.age(), 2,
                value -> update(state -> state.withAge((float) value), false)));
        y += WIDGET_HEIGHT + GAP + 2;

        swimSlider = addSlider(new LabSlider(x, y, half, "Swim", 0.0D, 100.0D, draft.swim(), 0,
                value -> updateEvolution(EvolutionChannel.SWIM, (float) value)));
        flySlider = addSlider(new LabSlider(x + half + GAP, y, half, "Fly", 0.0D, 100.0D, draft.fly(), 0,
                value -> updateEvolution(EvolutionChannel.FLY, (float) value)));
        y += WIDGET_HEIGHT + GAP;
        runSlider = addSlider(new LabSlider(x, y, half, "Run", 0.0D, 100.0D, draft.run(), 0,
                value -> updateEvolution(EvolutionChannel.RUN, (float) value)));
        powerSlider = addSlider(new LabSlider(x + half + GAP, y, half, "Power", 0.0D, 100.0D, draft.power(), 0,
                value -> updateEvolution(EvolutionChannel.POWER, (float) value)));
    }

    private void initFaceTab(int x, int y, int full, int half) {
        addSlider(new LabSlider(x, y, half, "Eyes", 0.0D, 12.0D, draft.eyes(), 0,
                value -> update(state -> state.withEyes((int) Math.round(value)), false)));
        addSlider(new LabSlider(x + half + GAP, y, half, "Eyelid", 0.0D, 2.0D, draft.eyelid(), 0,
                value -> update(state -> state.withEyelid((int) Math.round(value)), false)));
        y += WIDGET_HEIGHT + GAP;

        addSlider(new LabSlider(x, y, half, "Mouth", 0.0D, 12.0D, draft.mouth(), 0,
                value -> update(state -> state.withMouth((int) Math.round(value)), false)));
        addDrawableChild(ButtonWidget.builder(Text.literal("Face: " + faceModeLabel()), button -> toggleAutoFace())
                .dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP;

        addDrawableChild(ButtonWidget.builder(Text.literal("Emotion: " + emotionMode().label), button -> cycleEmotion())
                .dimensions(x, y, half, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Halo: " + (draft.tiltedHalo() ? "Tilted" : "Default")), button -> toggleHalo())
                .dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP + 3;

        addDrawableChild(ButtonWidget.builder(Text.literal("Auto Face + Ball"), button -> autoFaceAndBall())
                .dimensions(x, y, half, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset Face"), button -> update(ChaoAppearanceState::resetFace, true))
                .dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
    }

    private void initPartsTab(int x, int y, int full, int half) {
        Slot[] slots = Slot.values();
        for (int i = 0; i < slots.length; i += 2) {
            Slot left = slots[i];
            Slot right = slots[i + 1];
            addDrawableChild(ButtonWidget.builder(animalPartLabel(left), button -> cycleAnimalPart(left))
                    .dimensions(x, y, half, WIDGET_HEIGHT).build());
            addDrawableChild(ButtonWidget.builder(animalPartLabel(right), button -> cycleAnimalPart(right))
                    .dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
            y += WIDGET_HEIGHT + GAP;
        }
        y += 2;
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear Animal Parts"), button -> update(ChaoAppearanceState::clearAnimalParts, true))
                .dimensions(x, y, full, WIDGET_HEIGHT).build());
    }

    private void initTestTab(int x, int y, int full, int half) {
        addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> changePreset(-1))
                .dimensions(x, y, 24, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Preset: " + VisualPreset.values()[presetIndex].label), button -> applyPreset())
                .dimensions(x + 26, y, full - 52, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> changePreset(1))
                .dimensions(x + full - 24, y, 24, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP;

        int third = (full - GAP * 2) / 3;
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), button -> reset())
                .dimensions(x, y, third, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Random"), button -> randomize())
                .dimensions(x + third + GAP, y, third, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Auto"), button -> autoFaceAndBall())
                .dimensions(x + (third + GAP) * 2, y, full - (third + GAP) * 2, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP;

        addDrawableChild(ButtonWidget.builder(Text.literal("Copy State"), button -> copyState())
                .dimensions(x, y, half, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Paste State"), button -> pasteState())
                .dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP + 3;

        addDrawableChild(ButtonWidget.builder(Text.literal("Base Matrix (15)"), button -> spawnMatrix())
                .dimensions(x, y, half, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Chaos Matrix (3)"), button -> spawnChaosMatrix())
                .dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP;

        addDrawableChild(ButtonWidget.builder(Text.literal("Adult Extremes (75)"), button -> spawnAdultExtremes())
                .dimensions(x, y, half, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Child Extremes (15)"), button -> spawnChildExtremes())
                .dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP;

        addDrawableChild(ButtonWidget.builder(Text.literal("Color Matrix (28)"), button -> spawnColorMatrix())
                .dimensions(x, y, half, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reflection Matrix (17)"), button -> spawnReflectionMatrix())
                .dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP;
        addDrawableChild(ButtonWidget.builder(Text.literal("Animal Matrix (34)"), button -> spawnAnimalMatrix())
                .dimensions(x, y, half, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear Matrix"), button -> clearMatrix())
                .dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
        y += WIDGET_HEIGHT + GAP;

        addDrawableChild(ButtonWidget.builder(Text.literal(ChaoPerformanceProfiler.isRunning()
                        ? "Stop Perf Log" : "Start Perf Log"), button -> {
                    ChaoPerformanceProfiler.toggle(client);
                    rebuild();
                }).dimensions(x, y, half, WIDGET_HEIGHT).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Perf Snapshot"), button -> {
                    ChaoPerformanceProfiler.snapshotNow(client);
                    flash("Performance snapshot written");
                }).dimensions(x + half + GAP, y, half, WIDGET_HEIGHT).build());
    }

    private void addTabButton(LabTab tab, int x, int y, int width) {
        String prefix = activeTab == tab ? "[" : "";
        String suffix = activeTab == tab ? "]" : "";
        addDrawableChild(ButtonWidget.builder(Text.literal(prefix + tab.label + suffix), button -> {
            activeTab = tab;
            rebuild();
        }).dimensions(x, y, width, WIDGET_HEIGHT).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        LabLayout layout = layout();
        context.fill(layout.controlsX() - 2, 3,
                layout.controlsX() + layout.controlsWidth() + 2, height - 3, 0xB0101010);
        context.fill(layout.previewX() - 2, 3,
                layout.previewX() + layout.previewWidth() + 2, height - 3, 0xB0101010);

        renderPreview(context, mouseX, mouseY, layout.previewX(), 5,
                layout.previewWidth(), Math.max(80, height - 10));
        super.render(context, mouseX, mouseY, delta);

        int center = layout.controlsX() + layout.controlsWidth() / 2;
        context.drawCenteredTextWithShadow(textRenderer, title, center, 6, 0xFFFFFF);
        String draftText = "Virtual Chao  " + (draft.type().isChild() ? "Child" : adultTypeLabel())
                + "  N=" + Math.round(draft.normal());
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(draftText), center, 17, 0xC8FFC8);

        if (!status.isEmpty() && System.currentTimeMillis() < statusUntil) {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(status), center,
                    Math.max(4, height - 13), 0xFFFFA0);
        }
    }

    private void renderPreview(DrawContext context, int mouseX, int mouseY, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, 0xA0181818);
        context.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0x8026282C);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Production Renderer Preview"),
                x + width / 2, y + 6, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal((autoRotate
                        ? "Auto-rotate 360°" : "Manual: drag preview to rotate")
                        + "  |  Zoom " + Math.round(previewScale * 100.0F) + "%"),
                x + width / 2, y + 17, 0xAAAAAA);

        ensurePreviewEntity();
        if (previewEntity == null) {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("Preview unavailable"),
                    x + width / 2, y + height / 2, 0xAAAAAA);
            return;
        }

        int infoSpace = 34;
        int centerX = x + width / 2;
        // Chao meshes pivot near their feet. Lift the origin so the visual mass
        // sits near the center of the preview instead of hugging the bottom edge.
        int previewLift = MathHelper.clamp(height / 12, 18, 42);
        int baseY = y + height - infoSpace - previewLift;
        int baseScale = MathHelper.clamp(Math.min(width, Math.max(60, height - infoSpace)) / 3, 34, 86);
        int maxScale = Math.max(40, Math.min(width / 2, (height - infoSpace) / 2));
        int entityScale = MathHelper.clamp(Math.round(baseScale * previewScale), 24, maxScale);

        // Render the isolated preview Chao with the exact production renderer.
        // renderGuiPreview() composes Minecraft's GUI model-view transform with
        // this local screen transform, so the persistent VBO is inside the GUI
        // projection instead of being clipped behind it. Yaw is presentation-only
        // and therefore does not rebuild/cache-bust geometry.
        var renderer = client.getEntityRenderDispatcher().getRenderer(previewEntity);
        if (renderer instanceof ChaoRenderer chaoRenderer) {
            context.getMatrices().push();
            context.getMatrices().translate(centerX, baseY, 100.0F);
            context.getMatrices().scale(entityScale, -entityScale, entityScale);
            chaoRenderer.renderGuiPreview(previewEntity, context.getMatrices(), PREVIEW_FULL_BRIGHT,
                    previewYaw, -8.0F);
            context.getMatrices().pop();
        } else {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("Preview renderer unavailable"),
                    centerX, y + height / 2, 0xFF7777);
        }

        String state = (draft.type().isChild() ? "Child" : adultTypeLabel())
                + " A=" + Math.round(draft.alignment())
                + " " + colorLabel() + (draft.monotone() ? "/Mono" : "/2T")
                + " Refl=" + reflectionLabel()
                + " S/F/R/P=" + Math.round(draft.swim()) + "/" + Math.round(draft.fly())
                + "/" + Math.round(draft.run()) + "/" + Math.round(draft.power());
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(state), centerX,
                y + height - 25, 0xC8FFC8);

        ChaoRenderMetrics.Snapshot metrics = ChaoRenderMetrics.snapshot();
        double cachedMiB = metrics.cachedEstimatedBytes() / (1024.0D * 1024.0D);
        String perf = "VBO " + metrics.buildsPerSecond() + "/s q" + metrics.deferredPerSecond()
                + "  cache " + metrics.cachedEntities() + "e/" + metrics.sharedEntries() + "shared "
                + String.format(Locale.ROOT, "%.1fMB", cachedMiB)
                + (ChaoPerformanceProfiler.isRunning() ? "  LOG" : "");
        int perfColor = metrics.buildsPerSecond() == 0 ? 0xA0FFA0
                : metrics.buildsPerSecond() <= 20 ? 0xFFFFA0 : 0xFF7777;
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(perf), centerX,
                y + height - 13, perfColor);
    }

    /**
     * Screen.width/height are already Minecraft GUI-scaled coordinates. Instead
     * of assuming one GUI scale, split whatever logical space exists into two
     * bounded columns. At 1080p GUI scale 4 (~480 logical px) both columns still
     * fit; larger resolutions simply grant the preview more room.
     */
    private LabLayout layout() {
        int available = Math.max(2, width - OUTER_MARGIN * 2 - COLUMN_GAP);
        int controls = MathHelper.clamp((int) (available * 0.50F), MIN_CONTROL_WIDTH, MAX_CONTROL_WIDTH);
        int preview = available - controls;
        if (preview < MIN_PREVIEW_WIDTH) {
            controls = Math.max(120, available - MIN_PREVIEW_WIDTH);
            preview = Math.max(1, available - controls);
        }
        int controlsX = OUTER_MARGIN;
        int previewX = controlsX + controls + COLUMN_GAP;
        return new LabLayout(controlsX, controls, previewX, preview);
    }

    private void ensurePreviewEntity() {
        if (client == null || client.world == null) {
            releasePreviewEntity();
            return;
        }
        if (previewEntity != null && previewEntity.getWorld() == client.world) {
            return;
        }
        releasePreviewEntity();
        previewEntity = ModEntities.CHAO.create(client.world);
        if (previewEntity != null) {
            previewEntity.setCustomNameVisible(false);
            previewEntity.setNoGravity(true);
        }
    }

    private void syncPreview() {
        ensurePreviewEntity();
        if (previewEntity != null) {
            previewEntity.setAppearanceState(draft);
        }
        previewDirty = false;
        previewSettleTicks = 0;
    }

    /**
     * Slider mouse events can arrive hundreds of times per second. Debounce
     * geometry edits: while the user is actively dragging, only the inexpensive
     * draft/UI changes. The production VBO updates after a short pause or on
     * mouse release, eliminating sustained native-allocation churn.
     */
    private void queuePreviewSync() {
        previewDirty = true;
        previewSettleTicks = PREVIEW_SETTLE_TICKS;
    }

    @Override
    public void tick() {
        super.tick();
        if (autoRotate) {
            previewYaw = (previewYaw + 2.0F) % 360.0F;
        }
        if (previewDirty && --previewSettleTicks <= 0) {
            syncPreview();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (!autoRotate && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isInsidePreviewBody(mouseX, mouseY)) {
            previewDragging = true;
            lastPreviewMouseX = mouseX;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (previewDragging && !autoRotate && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            double movement = mouseX - lastPreviewMouseX;
            previewYaw = (float) ((previewYaw + movement * 1.35D) % 360.0D);
            if (previewYaw < 0.0F) {
                previewYaw += 360.0F;
            }
            lastPreviewMouseX = mouseX;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        previewDragging = false;
        boolean handled = super.mouseReleased(mouseX, mouseY, button);
        if (previewDirty) {
            syncPreview();
        }
        return handled;
    }

    private boolean isInsidePreviewBody(double mouseX, double mouseY) {
        LabLayout layout = layout();
        return mouseX >= layout.previewX() && mouseX < layout.previewX() + layout.previewWidth()
                && mouseY >= 46 && mouseY < height - 38;
    }

    private void releasePreviewEntity() {
        if (previewEntity == null) {
            return;
        }
        ChaoRenderer.releaseEntity(previewEntity.getUuid());
        previewEntity.discard();
        previewEntity = null;
    }

    @Override
    public void removed() {
        releasePreviewEntity();
        super.removed();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_F8) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private <T extends LabSlider> T addSlider(T slider) {
        return addDrawableChild(slider);
    }

    private void update(UnaryOperator<ChaoAppearanceState> operation, boolean rebuild) {
        ChaoAppearanceState next = operation.apply(draft);
        if (next.equals(draft)) {
            return;
        }
        draft = next;
        if (!draft.type().isChild()) {
            lastAdultType = draft.type();
        }
        // The Visual Lab owns only this isolated client-side draft. Geometry is
        // still debounced so violent slider movement cannot churn native VBOs.
        queuePreviewSync();
        if (rebuild) {
            rebuild();
        }
    }

    private void updateEvolution(EvolutionChannel channel, float value) {
        update(state -> state.withEvolution(channel, value), false);
        syncEvolutionSliders();
    }

    private void syncEvolutionSliders() {
        if (swimSlider != null) swimSlider.setMappedValueSilently(draft.swim());
        if (flySlider != null) flySlider.setMappedValueSilently(draft.fly());
        if (runSlider != null) runSlider.setMappedValueSilently(draft.run());
        if (powerSlider != null) powerSlider.setMappedValueSilently(draft.power());
    }

    private void toggleStage() {
        update(state -> state.type().isChild()
                ? state.withType(lastAdultType)
                : state.withType(ChaoVisualType.CHILD), true);
    }

    private void cycleAdultType() {
        ChaoVisualType[] adult = {ChaoVisualType.NORMAL, ChaoVisualType.SWIM, ChaoVisualType.FLY, ChaoVisualType.RUN, ChaoVisualType.POWER, ChaoVisualType.CHAOS};
        int index = 0;
        for (int i = 0; i < adult.length; i++) {
            if (adult[i] == lastAdultType) {
                index = i;
                break;
            }
        }
        lastAdultType = adult[(index + 1) % adult.length];
        if (!draft.type().isChild()) {
            update(state -> state.withType(lastAdultType), true);
        } else {
            rebuild();
        }
    }

    private void cycleColor() {
        ChaoColorType[] colors = ChaoColorType.values();
        ChaoColorType next = colors[(draft.colorType().ordinal() + 1) % colors.length];
        update(state -> state.withColorType(next), true);
    }

    private void toggleTone() {
        update(state -> state.withMonotone(!state.monotone()), true);
    }

    private void cycleReflection() {
        ChaoReflectionType[] values = ChaoReflectionType.values();
        ChaoReflectionType next = values[(draft.reflectionType().ordinal() + 1) % values.length];
        update(state -> state.withReflectionType(next), true);
    }

    private void cycleAnimalPart(Slot slot) {
        boolean adult = !draft.type().isChild();
        java.util.List<ChaoAnimalType> available = ChaoAnimalPartCatalog.available(adult, slot);
        ChaoAnimalType current = draft.animalParts().get(slot);
        int index = available.indexOf(current);
        ChaoAnimalType next = available.get(Math.floorMod(index + 1, available.size()));
        update(state -> state.withAnimalPart(slot, next), true);
    }

    private void changePreset(int direction) {
        presetIndex = Math.floorMod(presetIndex + direction, VisualPreset.values().length);
        applyPreset();
    }

    private void applyPreset() {
        VisualPreset preset = VisualPreset.values()[presetIndex];
        update(state -> preset.apply(state), true);
    }

    private void toggleAutoFace() {
        update(state -> state.customEyes() ? state.withAutoEyes() : state.withEyes(state.resolvedEyes()), true);
    }

    private void cycleEmotion() {
        EmotionMode[] values = EmotionMode.values();
        EmotionMode current = emotionMode();
        int index = current == EmotionMode.CUSTOM ? -1 : current.ordinal();
        EmotionMode next = values[Math.floorMod(index + 1, 5)];
        update(state -> next.apply(state), true);
    }

    private EmotionMode emotionMode() {
        if (!draft.customEmotionBall()) return EmotionMode.AUTO;
        if (draft.neutralBall() && !draft.heroBall() && !draft.darkBall()) return EmotionMode.NEUTRAL;
        if (!draft.neutralBall() && draft.heroBall() && !draft.darkBall()) return EmotionMode.HERO;
        if (!draft.neutralBall() && !draft.heroBall() && draft.darkBall()) return EmotionMode.DARK;
        if (!draft.neutralBall() && !draft.heroBall() && !draft.darkBall()) return EmotionMode.NONE;
        return EmotionMode.CUSTOM;
    }

    private void toggleHalo() {
        update(state -> state.withTiltedHalo(!state.tiltedHalo()), true);
    }

    private void reset() {
        update(state -> ChaoAppearanceState.DEFAULT, true);
    }

    private void autoFaceAndBall() {
        update(state -> state.withAutoEyes().withAutoEmotionBall().withStandardMouthMode(), true);
    }

    private void randomize() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        ChaoVisualType type = ChaoVisualType.values()[random.nextInt(ChaoVisualType.values().length)];
        float alignment = random.nextFloat() * 200.0F - 100.0F;
        float age = random.nextFloat();

        float swim = random.nextFloat();
        float fly = random.nextFloat();
        float run = random.nextFloat();
        float power = random.nextFloat();
        float sum = swim + fly + run + power;
        float total = random.nextFloat() * 100.0F;
        if (sum > 0.0F) {
            swim = swim / sum * total;
            fly = fly / sum * total;
            run = run / sum * total;
            power = power / sum * total;
        }

        ChaoAppearanceState randomized = new ChaoAppearanceState(
                type, age, alignment, swim, fly, run, power,
                ChaoColorType.values()[random.nextInt(ChaoColorType.values().length)], random.nextBoolean(),
                true, random.nextInt(13), random.nextInt(3),
                random.nextInt(13), false, 0, 0,
                false, true, false, false,
                random.nextBoolean()
        );
        update(state -> randomized, true);
    }

    private void copyState() {
        if (client == null) {
            return;
        }
        client.keyboard.setClipboard(serialize(draft));
        flash("Visual state copied");
    }

    private void pasteState() {
        if (client == null) {
            return;
        }
        ChaoAppearanceState parsed = parse(client.keyboard.getClipboard());
        if (parsed == null) {
            flash("Clipboard is not a ChaoCraft visual state");
            return;
        }
        update(state -> parsed, true);
        flash("Visual state pasted");
    }

    private Text autoRotateLabel() {
        return Text.literal("Auto Rotate: " + (autoRotate ? "ON" : "OFF"));
    }

    private void toggleAutoRotate() {
        autoRotate = !autoRotate;
        previewDragging = false;
        if (autoRotateButton != null) {
            autoRotateButton.setMessage(autoRotateLabel());
        }
    }

    private void adjustPreviewZoom(float delta) {
        float next = MathHelper.clamp(previewScale + delta, PREVIEW_SCALE_MIN, PREVIEW_SCALE_MAX);
        if (Math.abs(next - previewScale) < 0.0001F) {
            flash(delta > 0.0F ? "Preview zoom already at maximum" : "Preview zoom already at minimum");
            return;
        }
        previewScale = next;
        flash("Preview zoom " + Math.round(previewScale * 100.0F) + "%");
    }

    private void summonDraft() {
        // Ensure a drag that has not yet settled is reflected in the summoned
        // state. This does not create another preview VBO if nothing changed.
        if (previewDirty) {
            syncPreview();
        }
        ChaoVisualLabClient.summonDraft(draft);
        flash("Summoning current virtual Chao");
    }

    private void spawnMatrix() {
        ChaoVisualLabClient.spawnAdultMatrix();
        flash("Spawning 15-family adult matrix in front of you");
    }

    private void spawnChaosMatrix() {
        ChaoVisualLabClient.spawnChaosMatrix();
        flash("Spawning Neutral/Hero/Dark Chaos Chao");
    }

    private void spawnColorMatrix() {
        ChaoVisualLabClient.spawnColorMatrix(draft);
        flash("Spawning current family in 14 colors x Two-Tone/Monotone");
    }

    private void spawnReflectionMatrix() {
        ChaoVisualLabClient.spawnReflectionMatrix(draft);
        flash("Spawning all 17 Viewer reflection modes");
    }

    private void spawnAnimalMatrix() {
        ChaoVisualLabClient.spawnAnimalMatrix(draft);
        flash("Spawning all 34 animal sets on current Chao base");
    }

    private void spawnAdultExtremes() {
        ChaoVisualLabClient.spawnAdultExtremes();
        flash("Spawning 75 canonical adult family/evolution endpoints");
    }

    private void spawnChildExtremes() {
        ChaoVisualLabClient.spawnChildExtremes();
        flash("Spawning 15 canonical Child alignment/evolution endpoints");
    }

    private void clearMatrix() {
        ChaoVisualLabClient.clearAdultMatrix();
        flash("Clearing Visual Lab matrix");
    }

    private void rebuild() {
        clearChildren();
        init();
    }

    private void flash(String message) {
        status = message;
        statusUntil = System.currentTimeMillis() + 2400L;
    }

    private Text stageLabel() {
        return Text.literal("Stage: " + (draft.type().isChild() ? "Child" : "Adult"));
    }

    private String adultTypeLabel() {
        return switch (lastAdultType) {
            case NORMAL -> "Normal";
            case SWIM -> "Swim";
            case FLY -> "Fly";
            case RUN -> "Run";
            case POWER -> "Power";
            case CHAOS -> "Chaos";
            case CHILD -> "Normal";
        };
    }

    private String colorLabel() {
        return switch (draft.colorType()) {
            case NORMAL -> "Normal";
            case WHITE -> "White";
            case GREY -> "Grey";
            case BLACK -> "Black";
            case BROWN -> "Brown";
            case RED -> "Red";
            case ORANGE -> "Orange";
            case YELLOW -> "Yellow";
            case GREEN -> "Green";
            case LIME_GREEN -> "Lime Green";
            case SKY_BLUE -> "Sky Blue";
            case BLUE -> "Blue";
            case PURPLE -> "Purple";
            case PINK -> "Pink";
        };
    }

    private String reflectionLabel() {
        return draft.reflectionType().name().replace('_', ' ');
    }

    private Text animalPartLabel(Slot slot) {
        ChaoAnimalType animal = draft.animalParts().get(slot);
        String slotName = slot.name().charAt(0) + slot.name().substring(1).toLowerCase(Locale.ROOT);
        return Text.literal(slotName + ": " + animal.displayName());
    }

    private String faceModeLabel() {
        return draft.customEyes() ? "Custom" : "Auto";
    }

    private static String serialize(ChaoAppearanceState state) {
        return String.join("|",
                "CCVL3", state.type().name(), Float.toString(state.age()), Float.toString(state.alignment()),
                Float.toString(state.swim()), Float.toString(state.fly()), Float.toString(state.run()), Float.toString(state.power()),
                state.colorType().name(), Boolean.toString(state.monotone()), state.reflectionType().name(),
                state.animalParts().arms().name(), state.animalParts().legs().name(), state.animalParts().tail().name(),
                state.animalParts().wings().name(), state.animalParts().face().name(), state.animalParts().horns().name(),
                state.animalParts().ears().name(), state.animalParts().forehead().name(),
                Boolean.toString(state.customEyes()), Integer.toString(state.eyes()), Integer.toString(state.eyelid()),
                Integer.toString(state.mouth()), Boolean.toString(state.customMouth()), Integer.toString(state.mouthMid()),
                Integer.toString(state.mouthSide()), Boolean.toString(state.customEmotionBall()),
                Boolean.toString(state.neutralBall()), Boolean.toString(state.heroBall()), Boolean.toString(state.darkBall()),
                Boolean.toString(state.tiltedHalo()));
    }

    private static ChaoAppearanceState parse(String encoded) {
        if (encoded == null) return null;
        String[] parts = encoded.trim().split("\\|", -1);
        try {
            if (parts.length == 31 && "CCVL3".equals(parts[0])) {
                return new ChaoAppearanceState(
                        ChaoVisualType.valueOf(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]),
                        Float.parseFloat(parts[4]), Float.parseFloat(parts[5]), Float.parseFloat(parts[6]), Float.parseFloat(parts[7]),
                        ChaoColorType.valueOf(parts[8]), Boolean.parseBoolean(parts[9]), ChaoReflectionType.valueOf(parts[10]),
                        new ChaoAnimalParts(ChaoAnimalType.valueOf(parts[11]), ChaoAnimalType.valueOf(parts[12]),
                                ChaoAnimalType.valueOf(parts[13]), ChaoAnimalType.valueOf(parts[14]), ChaoAnimalType.valueOf(parts[15]),
                                ChaoAnimalType.valueOf(parts[16]), ChaoAnimalType.valueOf(parts[17]), ChaoAnimalType.valueOf(parts[18])),
                        Boolean.parseBoolean(parts[19]), Integer.parseInt(parts[20]), Integer.parseInt(parts[21]),
                        Integer.parseInt(parts[22]), Boolean.parseBoolean(parts[23]), Integer.parseInt(parts[24]), Integer.parseInt(parts[25]),
                        Boolean.parseBoolean(parts[26]), Boolean.parseBoolean(parts[27]), Boolean.parseBoolean(parts[28]),
                        Boolean.parseBoolean(parts[29]), Boolean.parseBoolean(parts[30]));
            }
            if (parts.length == 22 && "CCVL2".equals(parts[0])) {
                return new ChaoAppearanceState(
                        ChaoVisualType.valueOf(parts[1]),
                        Float.parseFloat(parts[2]), Float.parseFloat(parts[3]),
                        Float.parseFloat(parts[4]), Float.parseFloat(parts[5]),
                        Float.parseFloat(parts[6]), Float.parseFloat(parts[7]),
                        ChaoColorType.valueOf(parts[8]), Boolean.parseBoolean(parts[9]),
                        Boolean.parseBoolean(parts[10]), Integer.parseInt(parts[11]), Integer.parseInt(parts[12]),
                        Integer.parseInt(parts[13]), Boolean.parseBoolean(parts[14]),
                        Integer.parseInt(parts[15]), Integer.parseInt(parts[16]),
                        Boolean.parseBoolean(parts[17]), Boolean.parseBoolean(parts[18]),
                        Boolean.parseBoolean(parts[19]), Boolean.parseBoolean(parts[20]),
                        Boolean.parseBoolean(parts[21])
                );
            }
            // Backward compatibility with CP07.7 clipboard states.
            if (parts.length == 20 && "CCVL1".equals(parts[0])) {
                return new ChaoAppearanceState(
                        ChaoVisualType.valueOf(parts[1]),
                        Float.parseFloat(parts[2]), Float.parseFloat(parts[3]),
                        Float.parseFloat(parts[4]), Float.parseFloat(parts[5]),
                        Float.parseFloat(parts[6]), Float.parseFloat(parts[7]),
                        ChaoColorType.NORMAL, false,
                        Boolean.parseBoolean(parts[8]), Integer.parseInt(parts[9]), Integer.parseInt(parts[10]),
                        Integer.parseInt(parts[11]), Boolean.parseBoolean(parts[12]),
                        Integer.parseInt(parts[13]), Integer.parseInt(parts[14]),
                        Boolean.parseBoolean(parts[15]), Boolean.parseBoolean(parts[16]),
                        Boolean.parseBoolean(parts[17]), Boolean.parseBoolean(parts[18]),
                        Boolean.parseBoolean(parts[19])
                );
            }
            return null;
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private enum EmotionMode {
        AUTO("Auto"),
        NEUTRAL("Neutral"),
        HERO("Hero"),
        DARK("Dark"),
        NONE("None"),
        CUSTOM("Custom");

        final String label;

        EmotionMode(String label) {
            this.label = label;
        }

        ChaoAppearanceState apply(ChaoAppearanceState state) {
            return switch (this) {
                case AUTO -> state.withAutoEmotionBall();
                case NEUTRAL -> state.withCustomEmotionBalls(true, false, false);
                case HERO -> state.withCustomEmotionBalls(false, true, false);
                case DARK -> state.withCustomEmotionBalls(false, false, true);
                case NONE -> state.withCustomEmotionBalls(false, false, false);
                case CUSTOM -> state;
            };
        }
    }

    private enum VisualPreset {
        CHILD("Child", ChaoVisualType.CHILD, 0.0F),
        NN("Neutral Normal", ChaoVisualType.NORMAL, 0.0F),
        NS("Neutral Swim", ChaoVisualType.SWIM, 0.0F),
        NF("Neutral Fly", ChaoVisualType.FLY, 0.0F),
        NR("Neutral Run", ChaoVisualType.RUN, 0.0F),
        NP("Neutral Power", ChaoVisualType.POWER, 0.0F),
        HN("Hero Normal", ChaoVisualType.NORMAL, 100.0F),
        HS("Hero Swim", ChaoVisualType.SWIM, 100.0F),
        HF("Hero Fly", ChaoVisualType.FLY, 100.0F),
        HR("Hero Run", ChaoVisualType.RUN, 100.0F),
        HP("Hero Power", ChaoVisualType.POWER, 100.0F),
        DN("Dark Normal", ChaoVisualType.NORMAL, -100.0F),
        DS("Dark Swim", ChaoVisualType.SWIM, -100.0F),
        DF("Dark Fly", ChaoVisualType.FLY, -100.0F),
        DR("Dark Run", ChaoVisualType.RUN, -100.0F),
        DP("Dark Power", ChaoVisualType.POWER, -100.0F),
        NC("Neutral Chaos", ChaoVisualType.CHAOS, 0.0F),
        HC("Hero Chaos", ChaoVisualType.CHAOS, 100.0F),
        DC("Dark Chaos", ChaoVisualType.CHAOS, -100.0F);

        final String label;
        final ChaoVisualType type;
        final float alignment;

        VisualPreset(String label, ChaoVisualType type, float alignment) {
            this.label = label;
            this.type = type;
            this.alignment = alignment;
        }

        ChaoAppearanceState apply(ChaoAppearanceState state) {
            ChaoAppearanceState result = state
                    .withType(type)
                    .withAlignment(alignment)
                    .withAge(type.isChild() ? 0.0F : 1.0F);
            return new ChaoAppearanceState(
                    result.type(), result.age(), result.alignment(),
                    0.0F, 0.0F, 0.0F, 0.0F,
                    ChaoColorType.NORMAL, false,
                    false, 0, 0,
                    0, false, 0, 0,
                    false, true, false, false,
                    false
            );
        }

        static int closestIndex(ChaoAppearanceState state) {
            if (state.type().isChild()) return CHILD.ordinal();
            float snappedAlignment = state.alignment() >= 50.0F ? 100.0F : state.alignment() <= -50.0F ? -100.0F : 0.0F;
            VisualPreset[] values = values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].type == state.type() && values[i].alignment == snappedAlignment) return i;
            }
            return NN.ordinal();
        }
    }

    private enum LabTab {
        BODY("Body"),
        FACE("Face"),
        PARTS("Parts"),
        TEST("Test");

        final String label;

        LabTab(String label) {
            this.label = label;
        }
    }

    private record LabLayout(int controlsX, int controlsWidth, int previewX, int previewWidth) {
    }

    private static final class LabSlider extends SliderWidget {
        private final String label;
        private final double min;
        private final double max;
        private final int decimals;
        private final DoubleConsumer consumer;
        private boolean silent;
        private double lastApplied = Double.NaN;

        LabSlider(int x, int y, int width, String label, double min, double max, double initial, int decimals,
                DoubleConsumer consumer) {
            super(x, y, width, WIDGET_HEIGHT, Text.empty(), normalize(initial, min, max));
            this.label = label;
            this.min = min;
            this.max = max;
            this.decimals = decimals;
            this.consumer = consumer;
            this.lastApplied = quantize(initial);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            double mapped = quantizedMappedValue();
            String number = decimals == 0
                    ? Integer.toString((int) Math.round(mapped))
                    : String.format(Locale.ROOT, "%." + decimals + "f", mapped);
            setMessage(Text.literal(label + ": " + number));
        }

        @Override
        protected void applyValue() {
            if (silent) {
                return;
            }
            double mapped = quantizedMappedValue();
            // Mouse events may arrive far faster than game ticks. Quantization and
            // duplicate suppression prevent visually identical states from causing
            // render/network churn.
            if (Double.compare(mapped, lastApplied) == 0) {
                return;
            }
            lastApplied = mapped;
            value = normalize(mapped, min, max);
            updateMessage();
            consumer.accept(mapped);
        }

        void setMappedValueSilently(double mapped) {
            silent = true;
            double quantized = quantize(mapped);
            value = normalize(quantized, min, max);
            lastApplied = quantized;
            updateMessage();
            silent = false;
        }

        private double quantizedMappedValue() {
            return quantize(min + value * (max - min));
        }

        private double quantize(double mapped) {
            double clamped = MathHelper.clamp(mapped, min, max);
            double scale = decimals <= 0 ? 1.0D : Math.pow(10.0D, decimals);
            return Math.round(clamped * scale) / scale;
        }

        private static double normalize(double mapped, double min, double max) {
            if (max <= min) return 0.0D;
            return MathHelper.clamp((mapped - min) / (max - min), 0.0D, 1.0D);
        }
    }
}
