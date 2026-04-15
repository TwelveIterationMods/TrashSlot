package net.blay09.mods.trashslot.api;

import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;

import java.lang.reflect.InvocationTargetException;

public class TrashSlotAPI {

    private static final InternalMethods internalMethods;

    static {
        try {
            internalMethods = (InternalMethods) Class.forName("net.blay09.mods.trashslot.InternalMethodsImpl").getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static TrashContainerLayout getLayout(MenuType<?> menuType) {
        final var menuTypeId = BuiltInRegistries.MENU.getKey(menuType);
        if (menuTypeId != null) {
            return internalMethods.getLayout(menuTypeId);
        } else {
            return internalMethods.getDefaultLayout();
        }
    }

    public static TrashContainerLayout getLayout(Identifier identifier) {
        return internalMethods.getLayout(identifier);
    }

    public static TrashContainerLayout getDefaultLayout() {
        return internalMethods.getDefaultLayout();
    }

    /**
     * Registers a layout. Must be called after every client resource reload. Use the new event instead.
     * @deprecated Use {@link net.blay09.mods.trashslot.api.event.RegisterTrashSlotContainerLayoutsEvent#EVENT} instead.
     */
    @Deprecated
    public static void registerLayout(MenuType<?> menuType, TrashContainerLayout layout) {
        final var menuTypeId = BuiltInRegistries.MENU.getKey(menuType);
        if (menuTypeId != null) {
            registerLayout(menuTypeId, layout);
        } else {
            throw new IllegalArgumentException("Menu type is not registered, could not look up identifier");
        }
    }

    /**
     * Registers a layout. Must be called after every client resource reload. Use the new event instead.
     * @deprecated Use {@link net.blay09.mods.trashslot.api.event.RegisterTrashSlotContainerLayoutsEvent#EVENT} instead.
     */
    @Deprecated
    public static void registerLayout(Identifier identifier, TrashContainerLayout layout) {
        internalMethods.registerLayout(identifier, layout);
    }

}
