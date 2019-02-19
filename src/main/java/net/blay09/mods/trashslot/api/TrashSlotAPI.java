package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.inventory.GuiContainer;

public class TrashSlotAPI {

    private static InternalMethods internalMethods;

    public static void __setupAPI(InternalMethods impl) {
        internalMethods = impl;
    }

    public static ISimpleGuiContainerLayout registerSimpleLayout(Class<? extends GuiContainer> clazz) {
        return internalMethods.registerSimpleLayout(clazz);
    }

    public static void registerLayout(Class<? extends GuiContainer> clazz, IGuiContainerLayout layout) {
        internalMethods.registerLayout(clazz, layout);
    }

}
