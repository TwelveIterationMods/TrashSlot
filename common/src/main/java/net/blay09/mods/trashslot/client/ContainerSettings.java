package net.blay09.mods.trashslot.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class ContainerSettings {

    public static final MapCodec<ContainerSettings> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("snap").forGetter(settings -> Optional.ofNullable(settings.snap)),
            Codec.INT.fieldOf("slotX").forGetter(ContainerSettings::getSlotX),
            Codec.INT.fieldOf("slotY").forGetter(ContainerSettings::getSlotY),
            Codec.BOOL.fieldOf("enabled").forGetter(ContainerSettings::isEnabled),
            Codec.BOOL.fieldOf("locked").forGetter(ContainerSettings::isLocked)
    ).apply(instance, (snap, slotX, slotY, enabled, locked) -> new ContainerSettings(snap.orElse(null), slotX, slotY, enabled, locked)));
    public static final Codec<ContainerSettings> CODEC = MAP_CODEC.codec();

    private @Nullable Identifier snap;
    private int slotX;
    private int slotY;
    private boolean enabled;
    private boolean locked;

    public ContainerSettings(@Nullable Identifier snap, int slotX, int slotY, boolean enabled, boolean locked) {
        this.snap = snap;
        this.slotX = slotX;
        this.slotY = slotY;
        this.enabled = enabled;
        this.locked = locked;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean isLocked) {
        this.locked = isLocked;
    }

    public int getSlotX() {
        return slotX;
    }

    public void setSlotX(int slotX) {
        this.slotX = slotX;
    }

    public int getSlotY() {
        return slotY;
    }

    public void setSlotY(int slotY) {
        this.slotY = slotY;
    }

    @Nullable
    public Identifier getSnap() {
        return snap;
    }

    public void setSnap(@Nullable Identifier snap) {
        this.snap = snap;
    }
}
