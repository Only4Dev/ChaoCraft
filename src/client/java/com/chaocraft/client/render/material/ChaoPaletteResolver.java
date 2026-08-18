package com.chaocraft.client.render.material;

import com.chaocraft.client.render.family.ChaoAdultFamily;
import com.chaocraft.visual.ChaoAppearanceState;
import com.chaocraft.visual.ChaoMorphResolver;
import com.chaocraft.visual.ChaoMorphWeights;
import com.chaocraft.visual.ChaoVisualType;

/**
 * Translation of the Chao Viewer's Palettes.cs for the visual families that
 * are currently integrated in ChaoCraft.
 */
public final class ChaoPaletteResolver {
    private static final ChaoColor WHITE = ChaoColor.WHITE;
    private static final ChaoColor CLEAR = ChaoColor.TRANSPARENT_WHITE;

    private ChaoPaletteResolver() {
    }

    public static ChaoPaletteState resolve(ChaoAppearanceState state, ChaoMorphWeights weights) {
        ChaoPaletteState palette;
        if (state.type() == ChaoVisualType.CHAOS) {
            palette = resolveChaos(state);
        } else if (state.type() == ChaoVisualType.CHILD) {
            palette = resolveChild(state, weights);
        } else {
            palette = ChaoAdultPaletteResolver.resolve(ChaoAdultFamily.resolve(state), state, weights);
        }

        // ChaoMorphController.Update() overrides BodyCover after palette resolution
        // whenever the real SA2 color field is not Normal.
        ChaoColor bodyCover = ChaoBodyColorResolver.resolve(state.colorType(), palette.bodyCover());
        return withBodyCover(palette, bodyCover);
    }

    private static ChaoPaletteState resolveChaos(ChaoAppearanceState state) {
        ChaoColor emotion = state.alignment() >= 50.0F ? ChaoColor.rgb(255, 255, 0) : ChaoColor.WHITE;
        return new ChaoPaletteState(
                WHITE, WHITE, WHITE, WHITE, CLEAR, CLEAR,
                WHITE, WHITE, WHITE, CLEAR,
                WHITE, WHITE, emotion
        );
    }

    private static ChaoPaletteState resolveNeutralNormal(ChaoAppearanceState state, ChaoMorphWeights weights) {
        float young = 1.0F - state.age();
        float normal = weights.normal() / 100.0F;
        float swim = weights.swim() / 100.0F;
        float fly = weights.fly() / 100.0F;
        float run = weights.run() / 100.0F;
        float power = weights.power() / 100.0F;

        ChaoColor bodyCover = mix(
                ChaoColor.rgb(123, 255, 255),
                ChaoColor.rgb(0, 255, 255),
                ChaoColor.rgb(0, 255, 255),
                ChaoColor.rgb(0, 255, 255),
                ChaoColor.rgb(0, 255, 255),
                ChaoColor.rgb(0, 255, 255),
                young, normal, swim, fly, run, power
        );
        ChaoColor emotionBall = mix(
                ChaoColor.rgb(255, 208, 135),
                ChaoColor.rgb(255, 208, 0),
                ChaoColor.rgb(255, 208, 0),
                ChaoColor.rgb(255, 208, 0),
                ChaoColor.rgb(255, 208, 0),
                ChaoColor.rgb(255, 208, 0),
                young, normal, swim, fly, run, power
        );

        return new ChaoPaletteState(
                WHITE,
                ChaoColor.rgb(255, 255, 0),
                ChaoColor.rgb(123, 255, 255),
                CLEAR,
                CLEAR,
                WHITE,
                ChaoColor.rgb(255, 120, 229),
                ChaoColor.rgb(255, 210, 246),
                bodyCover,
                WHITE,
                emotionBall
        );
    }

    /** Hero Normal palette group (HN) from the Viewer. */
    private static ChaoPaletteState resolveHeroNormal(ChaoAppearanceState state, ChaoMorphWeights weights) {
        float young = 1.0F - state.age();
        float normal = weights.normal() / 100.0F;
        float swim = weights.swim() / 100.0F;
        float fly = weights.fly() / 100.0F;
        float run = weights.run() / 100.0F;
        float power = weights.power() / 100.0F;

        ChaoColor body = mix(
                ChaoColor.rgb(0, 170, 255), ChaoColor.rgb(0, 179, 255), ChaoColor.rgb(0, 255, 255),
                ChaoColor.rgb(114, 91, 197), ChaoColor.rgb(0, 100, 255), ChaoColor.rgb(13, 9, 255),
                young, normal, swim, fly, run, power
        );
        ChaoColor belly = mix(
                ChaoColor.rgb(255, 255, 0), ChaoColor.rgb(255, 255, 0), ChaoColor.rgb(129, 255, 216),
                ChaoColor.rgb(252, 193, 166), ChaoColor.rgb(100, 150, 255), ChaoColor.rgb(239, 200, 24),
                young, normal, swim, fly, run, power
        );
        ChaoColor extra = mix(
                ChaoColor.rgba(0, 170, 255, 0), ChaoColor.rgb(137, 231, 255), ChaoColor.rgb(8, 251, 186),
                ChaoColor.rgb(198, 208, 239), ChaoColor.rgb(0, 255, 255), ChaoColor.rgb(196, 98, 255),
                young, normal, swim, fly, run, power
        );
        ChaoColor wings = mix(
                ChaoColor.rgb(255, 255, 0), ChaoColor.rgb(255, 255, 0), ChaoColor.rgb(215, 215, 0),
                ChaoColor.rgb(196, 147, 39), ChaoColor.rgb(155, 230, 201), ChaoColor.rgb(246, 157, 33),
                young, normal, swim, fly, run, power
        );
        ChaoColor wingsBase = mix(
                WHITE, WHITE, WHITE, WHITE, ChaoColor.rgb(139, 208, 234), ChaoColor.rgb(255, 215, 0),
                young, normal, swim, fly, run, power
        );
        ChaoColor emotionBall = mix(
                ChaoColor.rgb(135, 255, 255), ChaoColor.rgb(85, 184, 255), ChaoColor.rgb(255, 255, 160),
                ChaoColor.rgb(255, 135, 111), ChaoColor.rgb(0, 255, 255), ChaoColor.rgb(255, 0, 255),
                young, normal, swim, fly, run, power
        );

        return new ChaoPaletteState(
                WHITE, body, belly, extra, CLEAR, WHITE, wings, wingsBase, WHITE, WHITE, emotionBall
        );
    }

    /** Dark Normal palette group (DN) from the Viewer. */
    private static ChaoPaletteState resolveDarkNormal(ChaoAppearanceState state, ChaoMorphWeights weights) {
        float young = 1.0F - state.age();
        float normal = weights.normal() / 100.0F;
        float swim = weights.swim() / 100.0F;
        float fly = weights.fly() / 100.0F;
        float run = weights.run() / 100.0F;
        float power = weights.power() / 100.0F;

        ChaoColor bodyCover = mix(
                ChaoColor.rgb(255, 0, 0), ChaoColor.rgb(255, 0, 128), ChaoColor.rgb(255, 132, 79),
                ChaoColor.rgb(255, 169, 255), ChaoColor.rgb(255, 0, 255), ChaoColor.rgb(255, 90, 0),
                young, normal, swim, fly, run, power
        );
        ChaoColor wingsCover = mix(
                ChaoColor.rgb(155, 120, 255), ChaoColor.rgb(128, 0, 255), ChaoColor.rgb(255, 0, 255),
                ChaoColor.rgb(255, 255, 0), ChaoColor.rgb(0, 255, 255), ChaoColor.rgb(255, 255, 0),
                young, normal, swim, fly, run, power
        );
        ChaoColor emotionBall = mix(
                ChaoColor.rgb(135, 135, 255), ChaoColor.rgb(85, 0, 208), ChaoColor.rgb(134, 0, 255),
                ChaoColor.rgb(255, 134, 255), ChaoColor.rgb(255, 255, 0), ChaoColor.rgb(255, 183, 0),
                young, normal, swim, fly, run, power
        );

        return new ChaoPaletteState(
                ChaoColor.rgb(49, 52, 49),
                WHITE,
                ChaoColor.rgb(186, 217, 255),
                ChaoColor.rgb(62, 129, 230),
                CLEAR,
                ChaoColor.rgb(49, 52, 49),
                ChaoColor.rgb(49, 48, 49),
                WHITE,
                bodyCover,
                wingsCover,
                emotionBall
        );
    }

    private static ChaoPaletteState resolveChild(ChaoAppearanceState state, ChaoMorphWeights weights) {
        float age = state.age();
        float young = 1.0F - age;
        float normal = state.normal() * age / 100.0F;
        float swim = state.swim() * age / 100.0F;
        float fly = state.fly() * age / 100.0F;
        float run = state.run() * age / 100.0F;
        float power = state.power() * age / 100.0F;

        ChaoMorphResolver.AlignmentWeights alignment = ChaoMorphResolver.resolveAlignment(state.alignment());
        Group neutralGroup = !state.monotone() && state.colorType() != com.chaocraft.visual.ChaoColorType.NORMAL ? CNC : CN;
        Group darkGroup = !state.monotone() && state.colorType() != com.chaocraft.visual.ChaoColorType.NORMAL ? CDC : CD;
        ChaoPaletteState neutral = withEmotionBall(
                childGroup(neutralGroup, young, normal, swim, fly, run, power),
                mix(
                        ChaoColor.rgb(255, 255, 90), ChaoColor.rgb(255, 255, 0), ChaoColor.rgb(0, 184, 255),
                        ChaoColor.rgb(255, 255, 0), ChaoColor.rgb(0, 255, 187), ChaoColor.rgb(255, 165, 57),
                        young, normal, swim, fly, run, power
                )
        );
        if (alignment.hero() > 0.0F) {
            ChaoPaletteState hero = withEmotionBall(
                    childGroup(CH, young, normal, swim, fly, run, power),
                    mix(
                            ChaoColor.rgb(0, 135, 255), ChaoColor.rgb(0, 135, 255), ChaoColor.rgb(131, 255, 0),
                            ChaoColor.rgb(178, 135, 255), ChaoColor.rgb(255, 159, 162), ChaoColor.rgb(255, 255, 0),
                            young, normal, swim, fly, run, power
                    )
            );
            return lerp(neutral, hero, alignment.hero() / 100.0F);
        }
        if (alignment.dark() > 0.0F) {
            ChaoPaletteState dark = withEmotionBall(
                    childGroup(darkGroup, young, normal, swim, fly, run, power),
                    mix(
                            ChaoColor.rgb(255, 134, 135), ChaoColor.rgb(255, 134, 135), ChaoColor.rgb(0, 183, 206),
                            ChaoColor.rgb(255, 135, 255), ChaoColor.rgb(0, 0, 255), ChaoColor.rgb(255, 255, 0),
                            young, normal, swim, fly, run, power
                    )
            );
            return lerp(neutral, dark, alignment.dark() / 100.0F);
        }
        return neutral;
    }

    private static ChaoPaletteState childGroup(Group group, float young, float normal, float swim, float fly, float run, float power) {
        return new ChaoPaletteState(
                mixChannel(group, Channel.BASE, young, normal, swim, fly, run, power),
                mixChannel(group, Channel.BODY, young, normal, swim, fly, run, power),
                mixChannel(group, Channel.BELLY, young, normal, swim, fly, run, power),
                mixChannel(group, Channel.EXTRA, young, normal, swim, fly, run, power),
                mixChannel(group, Channel.EXTRA2, young, normal, swim, fly, run, power),
                mixChannel(group, Channel.HORNS, young, normal, swim, fly, run, power),
                mixChannel(group, Channel.WINGS, young, normal, swim, fly, run, power),
                mixChannel(group, Channel.WINGS_BASE, young, normal, swim, fly, run, power),
                WHITE,
                WHITE,
                ChaoColor.WHITE
        );
    }

    private static ChaoColor mixChannel(Group group, Channel channel, float young, float normal, float swim, float fly, float run, float power) {
        return mix(
                group.young.get(channel), group.normal.get(channel), group.swim.get(channel),
                group.fly.get(channel), group.run.get(channel), group.power.get(channel),
                young, normal, swim, fly, run, power
        );
    }

    private static ChaoColor mix(ChaoColor youngColor, ChaoColor normalColor, ChaoColor swimColor,
            ChaoColor flyColor, ChaoColor runColor, ChaoColor powerColor,
            float young, float normal, float swim, float fly, float run, float power) {
        return youngColor.scale(young)
                .add(normalColor.scale(normal))
                .add(swimColor.scale(swim))
                .add(flyColor.scale(fly))
                .add(runColor.scale(run))
                .add(powerColor.scale(power));
    }

    private static ChaoPaletteState lerp(ChaoPaletteState from, ChaoPaletteState to, float amount) {
        return new ChaoPaletteState(
                from.base().lerp(to.base(), amount),
                from.body().lerp(to.body(), amount),
                from.belly().lerp(to.belly(), amount),
                from.extra().lerp(to.extra(), amount),
                from.extra2().lerp(to.extra2(), amount),
                from.horns().lerp(to.horns(), amount),
                from.wings().lerp(to.wings(), amount),
                from.wingsBase().lerp(to.wingsBase(), amount),
                from.bodyCover().lerp(to.bodyCover(), amount),
                from.wingsCover().lerp(to.wingsCover(), amount),
                from.emotionBall().lerp(to.emotionBall(), amount)
        );
    }

    private static ChaoPaletteState withBodyCover(ChaoPaletteState state, ChaoColor bodyCover) {
        return new ChaoPaletteState(
                state.base(), state.body(), state.belly(), state.extra(), state.extra2(), state.extra3(),
                state.horns(), state.wings(), state.wingsBase(), state.wingsExtra(),
                bodyCover, state.wingsCover(), state.emotionBall()
        );
    }

    private static ChaoPaletteState withEmotionBall(ChaoPaletteState state, ChaoColor emotionBall) {
        return new ChaoPaletteState(
                state.base(), state.body(), state.belly(), state.extra(), state.extra2(),
                state.horns(), state.wings(), state.wingsBase(), state.bodyCover(), state.wingsCover(),
                emotionBall
        );
    }

    private enum Channel { BASE, BODY, BELLY, EXTRA, EXTRA2, HORNS, WINGS, WINGS_BASE }

    private record Palette(ChaoColor base, ChaoColor body, ChaoColor belly, ChaoColor extra,
            ChaoColor extra2, ChaoColor horns, ChaoColor wings, ChaoColor wingsBase) {
        ChaoColor get(Channel channel) {
            return switch (channel) {
                case BASE -> base;
                case BODY -> body;
                case BELLY -> belly;
                case EXTRA -> extra;
                case EXTRA2 -> extra2;
                case HORNS -> horns;
                case WINGS -> wings;
                case WINGS_BASE -> wingsBase;
            };
        }
    }

    private record Group(Palette young, Palette normal, Palette swim, Palette fly, Palette run, Palette power) {
    }

    private static Palette p(int[] base, int[] body, int[] belly, int[] extra, int[] extra2, int[] horns, int[] wings, int[] wingsBase) {
        return new Palette(c(base), c(body), c(belly), c(extra), c(extra2), c(horns), c(wings), c(wingsBase));
    }

    private static ChaoColor c(int[] rgba) {
        return ChaoColor.rgba(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    private static int[] v(int r, int g, int b, int a) { return new int[]{r, g, b, a}; }

    private static final Group CN = new Group(
            p(v(132,242,255,255), v(254,253,84,255), v(132,242,255,255), v(132,242,255,0), v(255,255,255,0), v(132,242,255,255), v(255,120,229,255), v(255,210,246,255)),
            p(v(132,242,255,255), v(162,241,26,255), v(0,209,255,255), v(0,209,255,0), v(255,255,255,0), v(132,242,255,255), v(255,120,229,255), v(255,210,246,255)),
            p(v(253,253,179,255), v(254,253,84,255), v(123,243,174,255), v(123,243,174,0), v(255,255,255,0), v(123,243,174,255), v(0,139,255,255), v(0,255,255,255)),
            p(v(254,167,254,255), v(255,148,0,255), v(124,159,255,255), v(124,159,255,0), v(255,255,255,0), v(124,159,255,255), v(255,148,0,255), v(255,255,0,255)),
            p(v(0,255,255,255), v(0,255,0,255), v(0,255,255,255), v(0,255,255,0), v(255,255,255,0), v(0,255,255,255), v(255,82,0,255), v(255,220,0,255)),
            p(v(253,110,110,255), v(255,137,0,255), v(131,109,111,255), v(131,109,111,0), v(255,255,255,0), v(253,110,110,255), v(111,0,77,255), v(0,255,192,255))
    );

    private static final Group CH = new Group(
            p(v(255,255,255,255), v(0,179,255,255), v(255,255,255,255), v(255,255,255,0), v(0,255,255,255), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255)),
            p(v(255,255,255,255), v(0,137,255,255), v(255,255,93,255), v(255,255,93,0), v(0,213,255,255), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255)),
            p(v(255,255,255,255), v(255,255,92,255), v(93,255,181,255), v(93,255,181,0), v(201,255,181,255), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255)),
            p(v(255,255,255,255), v(206,68,255,255), v(93,181,255,255), v(93,181,255,0), v(255,197,255,255), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255)),
            p(v(255,255,255,255), v(128,255,155,255), v(93,181,255,255), v(93,181,255,0), v(128,255,155,0), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255)),
            p(v(255,255,255,255), v(255,0,0,255), v(255,234,156,255), v(255,234,156,0), v(255,234,156,255), v(255,255,255,255), v(255,105,132,255), v(255,217,132,255))
    );

    private static final Group CD = new Group(
            p(v(54,10,11,255), v(255,0,0,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(54,10,11,255), v(100,0,0,255), v(255,0,0,255)),
            p(v(54,10,11,255), v(255,0,0,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(54,10,11,255), v(100,0,0,255), v(255,0,0,255)),
            p(v(59,68,36,255), v(77,190,159,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(34,44,19,255), v(255,144,0,255), v(255,236,0,255)),
            p(v(45,18,49,255), v(234,47,216,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(45,18,49,255), v(0,82,86,255), v(0,193,204,255)),
            p(v(0,50,38,255), v(0,226,170,255), v(0,0,0,0), v(0,0,0,255), v(255,255,255,0), v(0,50,38,255), v(55,0,232,255), v(55,0,232,255)),
            p(v(48,28,2,255), v(255,144,0,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(48,28,2,255), v(255,144,0,255), v(255,236,0,255))
    );
    /** Colored two-tone Child neutral group (CNC) from Palettes.cs. */
    private static final Group CNC = new Group(
            p(v(132,242,255,255), v(254,253,84,255), v(132,242,255,255), v(132,242,255,0), v(255,255,255,0), v(132,242,255,255), v(255,120,229,255), v(255,210,246,255)),
            p(v(255,255,255,255), v(255,255,255,255), v(132,242,255,255), v(132,242,255,0), v(255,255,255,0), v(132,242,255,255), v(255,120,229,255), v(255,210,246,255)),
            p(v(255,255,255,255), v(255,255,255,255), v(132,242,255,255), v(132,242,255,0), v(255,255,255,0), v(132,242,255,255), v(0,139,255,255), v(0,255,255,255)),
            p(v(255,255,255,255), v(255,255,255,255), v(132,242,255,255), v(132,242,255,0), v(255,255,255,0), v(132,242,255,255), v(255,148,0,255), v(255,255,0,255)),
            p(v(255,255,255,255), v(255,255,255,255), v(132,242,255,255), v(132,242,255,0), v(255,255,255,0), v(132,242,255,255), v(255,82,0,255), v(255,220,0,255)),
            p(v(255,255,255,255), v(255,255,255,255), v(132,242,255,255), v(132,242,255,0), v(255,255,255,0), v(132,242,255,255), v(111,0,77,255), v(0,255,192,255))
    );

    /** Colored two-tone Child dark group (CDC) from Palettes.cs. */
    private static final Group CDC = new Group(
            p(v(49,52,49,255), v(255,255,255,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(49,52,49,255), v(100,0,0,255), v(255,0,0,255)),
            p(v(49,52,49,255), v(255,255,255,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(49,52,49,255), v(100,0,0,255), v(255,0,0,255)),
            p(v(49,52,49,255), v(255,255,255,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(49,52,49,255), v(255,144,0,255), v(255,236,0,255)),
            p(v(49,52,49,255), v(255,255,255,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(49,52,49,255), v(0,82,86,255), v(0,193,204,255)),
            p(v(49,52,49,255), v(255,255,255,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(49,52,49,255), v(55,0,232,255), v(55,0,232,255)),
            p(v(49,52,49,255), v(255,255,255,255), v(255,0,0,0), v(255,0,0,255), v(255,255,255,0), v(49,52,49,255), v(255,144,0,255), v(255,236,0,255))
    );

}
