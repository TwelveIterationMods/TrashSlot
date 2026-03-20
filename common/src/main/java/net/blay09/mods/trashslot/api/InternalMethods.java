package net.blay09.mods.trashslot.api;

import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayoutBuilder;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public interface InternalMethods {
    TrashContainerLayoutBuilder registerSimpleLayout(Class<? extends AbstractContainerScreen<?>> clazz);

    void registerLayout(Class<? extends AbstractContainerScreen<?>> clazz, TrashContainerLayout layout);
}
