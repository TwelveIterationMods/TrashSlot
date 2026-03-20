package net.blay09.mods.trashslot.api;

import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayoutBuilder;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class TrashSlotAPI {

    private static InternalMethods internalMethods;

    public static void __setupAPI(InternalMethods impl) {
        internalMethods = impl;
    }

    public static TrashContainerLayoutBuilder registerSimpleLayout(Class<? extends AbstractContainerScreen<?>> clazz) {
        return internalMethods.registerSimpleLayout(clazz);
    }

    public static void registerLayout(Class<? extends AbstractContainerScreen<?>> clazz, TrashContainerLayout layout) {
        internalMethods.registerLayout(clazz, layout);
    }

}
