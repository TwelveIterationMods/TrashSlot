package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class TrashSlotAPI {

    private static InternalMethods internalMethods;

    public static void __setupAPI(InternalMethods impl) {
        internalMethods = impl;
    }

    public static ISimpleGuiContainerLayout registerSimpleLayout(Class<? extends AbstractContainerScreen<?>> clazz) {
        return internalMethods.registerSimpleLayout(clazz);
    }

    public static void registerLayout(Class<? extends AbstractContainerScreen<?>> clazz, IGuiContainerLayout layout) {
        internalMethods.registerLayout(clazz, layout);
    }

}
