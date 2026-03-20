package net.blay09.mods.trashslot.api.layout;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum TrashSlotAvailability implements StringRepresentable {
    NEVER,
    OPTIONAL,
    DEFAULT;

    public static final StringRepresentable.EnumCodec<TrashSlotAvailability> CODEC = StringRepresentable.fromEnum(TrashSlotAvailability::values);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return getSerializedName();
    }
}
