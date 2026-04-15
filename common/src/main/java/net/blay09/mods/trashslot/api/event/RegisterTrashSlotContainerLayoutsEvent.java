package net.blay09.mods.trashslot.api.event;

import net.blay09.mods.balm.Balmstrap;
import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record RegisterTrashSlotContainerLayoutsEvent(BiConsumer<Identifier, TrashContainerLayout> registrar) {
    public static final BidirectionalEventMapper<Consumer<RegisterTrashSlotContainerLayoutsEvent>> EVENT = Balmstrap.createBoundCustomEvent(RegisterTrashSlotContainerLayoutsEvent.class);

    public void registerLayout(MenuType<?> menuType, TrashContainerLayout layout) {
        final var menuTypeId = BuiltInRegistries.MENU.getKey(menuType);
        if (menuTypeId != null) {
            registerLayout(menuTypeId, layout);
        } else {
            throw new IllegalArgumentException("Menu type is not registered, could not look up identifier");
        }
    }

    public void registerLayout(Identifier identifier, TrashContainerLayout layout) {
        registrar.accept(identifier, layout);
    }
}
