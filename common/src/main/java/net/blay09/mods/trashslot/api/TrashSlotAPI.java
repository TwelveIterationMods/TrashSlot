package net.blay09.mods.trashslot.api;

import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;

public class TrashSlotAPI {

    private static InternalMethods internalMethods;

    public static void __setupAPI(InternalMethods impl) {
        internalMethods = impl;
    }

    public static TrashContainerLayout getLayout(MenuType<?> menuType) {
        final var menuTypeId = BuiltInRegistries.MENU.getKey(menuType);
        return internalMethods.getLayout(menuTypeId);
    }

    public static TrashContainerLayout getLayout(Identifier identifier) {
        return internalMethods.getLayout(identifier);
    }

    public static void registerLayout(MenuType<?> menuType, TrashContainerLayout layout) {
        final var menuTypeId = BuiltInRegistries.MENU.getKey(menuType);
        registerLayout(menuTypeId, layout);
    }

    public static void registerLayout(Identifier identifier, TrashContainerLayout layout) {
        internalMethods.registerLayout(identifier, layout);
    }

}
