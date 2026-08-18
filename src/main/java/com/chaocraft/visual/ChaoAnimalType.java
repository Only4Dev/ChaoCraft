package com.chaocraft.visual;

/** Exact Animal enum order used by Chao Viewer / SA2 + DX part sets. */
public enum ChaoAnimalType {
    NONE,
    BEAR,
    BUNNY,
    CHEETA,
    CONDOR,
    DRAGON,
    GORILLA,
    OTTER,
    PARROT,
    PEACOCK,
    PENGUIN,
    PHOENIX,
    RACCOON,
    SEAL,
    SHEEP,
    SKUNK,
    TIGER,
    UNICORN,
    WARTHOG,
    BAT,
    OLD_SEAL,
    OLD_PENGUIN,
    OLD_OTTER,
    OLD_PEACOCK,
    OLD_PARROT,
    SWALLOW,
    OLD_RABBIT,
    DEER,
    KANGAROO,
    OLD_GORILLA,
    LION,
    ELEPHANT,
    OLD_SKUNK,
    MOLE,
    KOALA;

    public static ChaoAnimalType fromOrdinal(int ordinal) {
        ChaoAnimalType[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : NONE;
    }

    public String displayName() {
        return switch (this) {
            case NONE -> "None";
            case OLD_SEAL -> "Old Seal";
            case OLD_PENGUIN -> "Old Penguin";
            case OLD_OTTER -> "Old Otter";
            case OLD_PEACOCK -> "Old Peacock";
            case OLD_PARROT -> "Old Parrot";
            case OLD_RABBIT -> "Old Rabbit";
            case OLD_GORILLA -> "Old Gorilla";
            case OLD_SKUNK -> "Old Skunk";
            default -> {
                String value = name().toLowerCase(java.util.Locale.ROOT).replace('_', ' ');
                yield Character.toUpperCase(value.charAt(0)) + value.substring(1);
            }
        };
    }
}
