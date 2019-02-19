package net.blay09.mods.trashslot;

import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.api.ISimpleGuiContainerLayout;
import net.blay09.mods.trashslot.api.InternalMethods;
import net.blay09.mods.trashslot.client.LayoutManager;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.inventory.GuiContainer;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public ISimpleGuiContainerLayout registerSimpleLayout(Class<? extends GuiContainer> clazz) {
        SimpleGuiContainerLayout layout = new SimpleGuiContainerLayout();
        registerLayout(clazz, layout);
        return layout;
    }

    @Override
    public void registerLayout(Class<? extends GuiContainer> clazz, IGuiContainerLayout layout) {
        LayoutManager.registerLayout(clazz, layout);
    }
}
