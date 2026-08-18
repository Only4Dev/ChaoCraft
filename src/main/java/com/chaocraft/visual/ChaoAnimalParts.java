package com.chaocraft.visual;

/** Immutable per-slot animal-part selection mirroring ChaoMorphController. */
public record ChaoAnimalParts(
        ChaoAnimalType arms,
        ChaoAnimalType legs,
        ChaoAnimalType tail,
        ChaoAnimalType wings,
        ChaoAnimalType face,
        ChaoAnimalType horns,
        ChaoAnimalType ears,
        ChaoAnimalType forehead
) {
    public static final ChaoAnimalParts NONE = new ChaoAnimalParts(
            ChaoAnimalType.NONE, ChaoAnimalType.NONE, ChaoAnimalType.NONE, ChaoAnimalType.NONE,
            ChaoAnimalType.NONE, ChaoAnimalType.NONE, ChaoAnimalType.NONE, ChaoAnimalType.NONE
    );

    public ChaoAnimalParts {
        arms = safe(arms);
        legs = safe(legs);
        tail = safe(tail);
        wings = safe(wings);
        face = safe(face);
        horns = safe(horns);
        ears = safe(ears);
        forehead = safe(forehead);
    }

    public ChaoAnimalType get(Slot slot) {
        return switch (slot) {
            case ARMS -> arms;
            case LEGS -> legs;
            case TAIL -> tail;
            case WINGS -> wings;
            case FACE -> face;
            case HORNS -> horns;
            case EARS -> ears;
            case FOREHEAD -> forehead;
        };
    }

    public ChaoAnimalParts with(Slot slot, ChaoAnimalType animal) {
        ChaoAnimalType value = safe(animal);
        return switch (slot) {
            case ARMS -> new ChaoAnimalParts(value, legs, tail, wings, face, horns, ears, forehead);
            case LEGS -> new ChaoAnimalParts(arms, value, tail, wings, face, horns, ears, forehead);
            case TAIL -> new ChaoAnimalParts(arms, legs, value, wings, face, horns, ears, forehead);
            case WINGS -> new ChaoAnimalParts(arms, legs, tail, value, face, horns, ears, forehead);
            case FACE -> new ChaoAnimalParts(arms, legs, tail, wings, value, horns, ears, forehead);
            case HORNS -> new ChaoAnimalParts(arms, legs, tail, wings, face, value, ears, forehead);
            case EARS -> new ChaoAnimalParts(arms, legs, tail, wings, face, horns, value, forehead);
            case FOREHEAD -> new ChaoAnimalParts(arms, legs, tail, wings, face, horns, ears, value);
        };
    }

    public boolean isEmpty() {
        return this.equals(NONE);
    }

    private static ChaoAnimalType safe(ChaoAnimalType value) {
        return value == null ? ChaoAnimalType.NONE : value;
    }

    public enum Slot {
        ARMS,
        LEGS,
        TAIL,
        WINGS,
        FACE,
        HORNS,
        EARS,
        FOREHEAD
    }
}
