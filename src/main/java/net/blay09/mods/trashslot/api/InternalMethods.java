package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.inventory.GuiContainer;

public interface InternalMethods {
    ISimpleGuiContainerLayout registerSimpleLayout(Class<? extends GuiContainer> clazz);

    void registerLayout(Class<? extends GuiContainer> clazz, IGuiContainerLayout layout);
}
