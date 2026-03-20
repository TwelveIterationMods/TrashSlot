package net.blay09.mods.trashslot;

import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.blay09.mods.trashslot.api.layout.TrashSlotContainerContext;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record TrashSlotContainerContextImpl(
        TrashContainerLayout layout,
        AbstractContainerScreen<?> screen
) implements TrashSlotContainerContext {
    @Override
    public Optional<Rect2i> rect(Identifier identifier) {
        return layout.getBounds(this, identifier);
    }
}
