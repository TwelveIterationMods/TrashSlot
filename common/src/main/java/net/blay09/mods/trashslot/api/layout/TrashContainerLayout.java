package net.blay09.mods.trashslot.api.layout;

import net.blay09.mods.trashslot.TrashSlotContainerContextImpl;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TrashContainerLayout {
    List<Rect2i> getAllBounds(TrashSlotContainerContext context);

    Optional<Rect2i> getBounds(TrashSlotContainerContext context, Identifier identifier);

    Optional<Snap> getSnap(TrashSlotContainerContext context, Identifier identifier);

    Map<Identifier, Snap> getSnaps(TrashSlotContainerContext context);

    Optional<Snap> getDefaultSnap(TrashSlotContainerContext context);

    TrashSlotAvailability getAvailability();

    default TrashSlotContainerContext createContext(AbstractContainerScreen<?> screen) {
        return new TrashSlotContainerContextImpl(this, screen);
    }

    default boolean isEnabledByDefault() {
        return getAvailability() == TrashSlotAvailability.DEFAULT;
    }
}
