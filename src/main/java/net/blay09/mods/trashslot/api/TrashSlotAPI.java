package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;

public class TrashSlotAPI {

    private static InternalMethods internalMethods;

    public static void __setupAPI(InternalMethods impl) {
        internalMethods = impl;
    }

    public static ISimpleGuiContainerLayout registerSimpleLayout(Class<? extends ContainerScreen<?>> clazz) {
        return internalMethods.registerSimpleLayout(clazz);
    }

    public static void registerLayout(Class<? extends ContainerScreen<?>> clazz, IGuiContainerLayout layout) {
        internalMethods.registerLayout(clazz, layout);
    }

}
