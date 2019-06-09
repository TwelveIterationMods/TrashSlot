package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;

public interface InternalMethods {
    ISimpleGuiContainerLayout registerSimpleLayout(Class<? extends ContainerScreen<?>> clazz);

    void registerLayout(Class<? extends ContainerScreen<?>> clazz, IGuiContainerLayout layout);
}
