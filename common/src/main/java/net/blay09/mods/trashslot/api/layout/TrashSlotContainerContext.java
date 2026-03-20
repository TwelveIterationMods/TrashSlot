package net.blay09.mods.trashslot.api.layout;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public interface TrashSlotContainerContext {
    Optional<Rect2i> rect(Identifier identifier);

    AbstractContainerScreen<?> screen();

    TrashContainerLayout layout();
}
