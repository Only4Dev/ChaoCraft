package com.chaocraft.client.render.family;

import com.chaocraft.ChaoCraft;
import net.minecraft.util.Identifier;

/** Material-slot mapping copied from the Viewer's three Chaos Chao objects. */
public final class ChaoChaosMaterialProfiles {
    private static final Identifier N_HEAD = tex("al_ncha04.png");
    private static final Identifier N_INNER = tex("al_ncha01.png");
    private static final Identifier N_ARMS = tex("al_ncha02.png");
    private static final Identifier N_BELLY = tex("al_ncha03.png");
    private static final Identifier N_SPIKE = tex("al_ncha00.png");
    private static final Identifier H_HORNS = tex("al_hcha00.png");
    private static final Identifier H_BELLY = tex("al_hcha01.png");
    private static final Identifier H_BODY = tex("al_hcha02.png");
    private static final Identifier D_BODY = tex("al_dcha00.png");
    private static final Identifier D_HORNS = tex("al_dcha01.png");
    private static final Identifier D_BELLY = tex("al_dcha02.png");

    private ChaoChaosMaterialProfiles() {}

    public static Spec resolve(ChaoChaosFamily family, String segmentName, int submesh) {
        String segment = partName(segmentName);
        return switch (family) {
            case NEUTRAL -> neutral(segment, submesh);
            case HERO -> hero(segment, submesh);
            case DARK -> dark(segment, submesh);
        };
    }

    private static Spec neutral(String segment, int i) {
        return switch (segment) {
            case "arms" -> body(N_ARMS, true);
            case "belly", "legs" -> body(N_BELLY, true);
            case "tail" -> hidden();
            case "head" -> switch (i) {
                case 0 -> body(N_HEAD, true);
                case 1 -> body(N_ARMS, true);
                case 2 -> body(N_BELLY, true);
                // NCHA InnerHead is deliberately not BodyCover-tinted by ChangeNeutralChaos().
                case 3 -> body(N_INNER, false);
                case 4 -> body(N_SPIKE, true);
                case 5 -> eye();
                case 6 -> eyelid();
                case 7 -> mouthMid();
                case 8 -> mouthSide();
                default -> hidden();
            };
            default -> hidden();
        };
    }

    private static Spec hero(String segment, int i) {
        return switch (segment) {
            case "arms" -> body(H_HORNS, true);
            case "legs" -> body(H_BODY, true);
            case "tail" -> hidden();
            case "belly" -> i == 0 ? body(H_BELLY, true) : body(H_BODY, true);
            case "head" -> switch (i) {
                case 0 -> body(H_BODY, true);
                case 1 -> body(H_BELLY, true);
                case 2 -> eye();
                case 3 -> eyelid();
                case 4 -> body(H_HORNS, true);
                case 5 -> mouthMid();
                case 6 -> mouthSide();
                default -> hidden();
            };
            default -> hidden();
        };
    }

    private static Spec dark(String segment, int i) {
        return switch (segment) {
            case "arms" -> body(D_HORNS, true);
            case "legs", "tail" -> body(D_BODY, true);
            case "belly" -> i == 0 ? body(D_BODY, true) : body(D_BELLY, true);
            case "head" -> switch (i) {
                case 0 -> body(D_HORNS, true);
                case 1 -> body(D_BELLY, true);
                case 2 -> body(D_BODY, true);
                case 3 -> eye();
                case 4 -> eyelid();
                case 5 -> mouthMid();
                case 6 -> mouthSide();
                default -> hidden();
            };
            default -> hidden();
        };
    }


    private static String partName(String name) {
        String lower = name.toLowerCase(java.util.Locale.ROOT);
        if (lower.contains("arms")) return "arms";
        if (lower.contains("belly")) return "belly";
        if (lower.contains("head")) return "head";
        if (lower.contains("legs")) return "legs";
        if (lower.contains("tail")) return "tail";
        return lower;
    }

    private static Identifier tex(String name) {
        return ChaoCraft.id("textures/entity/chao/chaos/" + name);
    }

    private static Spec body(Identifier texture, boolean coverTint) { return new Spec(Kind.BODY, texture, coverTint); }
    private static Spec eye() { return new Spec(Kind.EYE, null, false); }
    private static Spec eyelid() { return new Spec(Kind.EYELID, null, false); }
    private static Spec mouthMid() { return new Spec(Kind.MOUTH_MID, null, false); }
    private static Spec mouthSide() { return new Spec(Kind.MOUTH_SIDE, null, false); }
    private static Spec hidden() { return new Spec(Kind.HIDDEN, null, false); }

    public enum Kind { BODY, EYE, EYELID, MOUTH_MID, MOUTH_SIDE, HIDDEN }
    public record Spec(Kind kind, Identifier texture, boolean coverTint) {}
}
