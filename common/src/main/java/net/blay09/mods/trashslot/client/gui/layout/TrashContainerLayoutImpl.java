package net.blay09.mods.trashslot.client.gui.layout;

import net.blay09.mods.trashslot.api.layout.*;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TrashContainerLayoutImpl implements TrashContainerLayout {

    private final TrashSlotAvailability availability;
    private final Identifier defaultSnap;
    private final Map<Identifier, ScreenBoundsProvider> bounds;
    private final Map<Identifier, Snap> snaps;

    public TrashContainerLayoutImpl(TrashSlotAvailability availability, Map<Identifier, ScreenBoundsProvider> bounds, Map<Identifier, Snap> snaps, Identifier defaultSnap) {
        this.availability = availability;
        this.defaultSnap = defaultSnap;
        this.bounds = bounds;
        this.snaps = snaps;
    }

    @Override
    public List<Rect2i> getAllBounds(TrashSlotContainerContext context) {
        return bounds.values().stream().map(it -> it.get(context)).toList();
    }

    @Override
    public Optional<Rect2i> getBounds(TrashSlotContainerContext context, Identifier identifier) {
        return Optional.ofNullable(bounds.get(identifier)).map(it -> it.get(context));
    }

    @Override
    public Optional<Snap> getSnap(TrashSlotContainerContext context, Identifier identifier) {
        return Optional.ofNullable(snaps.get(identifier));
    }

    @Override
    public Map<Identifier, Snap> getSnaps(TrashSlotContainerContext context) {
        return snaps;
    }

    @Override
    public Optional<Snap> getDefaultSnap(TrashSlotContainerContext context) {
        return getSnap(context, defaultSnap);
    }

    @Override
    public TrashSlotAvailability getAvailability() {
        return availability;
    }

}
