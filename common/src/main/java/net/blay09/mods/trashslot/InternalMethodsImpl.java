package net.blay09.mods.trashslot;

import net.blay09.mods.trashslot.api.InternalMethods;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayoutBuilder;
import net.blay09.mods.trashslot.client.LayoutManager;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public TrashContainerLayoutBuilder registerSimpleLayout(Class<? extends AbstractContainerScreen<?>> clazz) {
        SimpleGuiContainerLayout layout = new SimpleGuiContainerLayout();
        registerLayout(clazz, layout);
        return layout;
    }

    @Override
    public void registerLayout(Class<? extends AbstractContainerScreen<?>> clazz, TrashContainerLayout layout) {
        LayoutManager.registerLayout(clazz, layout);
    }
}
