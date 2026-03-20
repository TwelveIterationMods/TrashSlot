package net.blay09.mods.trashslot.client;

import com.google.common.collect.Maps;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.blay09.mods.trashslot.api.layout.TrashSlotAvailability;
import net.blay09.mods.trashslot.client.gui.layout.TrashContainerLayoutImpl;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static net.blay09.mods.trashslot.TrashSlot.id;

public class TrashContainerLayoutManager {

    public static final Identifier DEFAULT_LAYOUT = id("default");
    public static final Identifier INVENTORY_LAYOUT = Identifier.withDefaultNamespace("inventory");
    private static final Map<Identifier, TrashContainerLayout> layoutMap = Maps.newHashMap();

    private static final TrashContainerLayout FALLBACK = new TrashContainerLayoutImpl(TrashSlotAvailability.NEVER, Collections.emptyMap(), Collections.emptyMap(), id("never"));

    public static TrashContainerLayout getLayout(AbstractContainerScreen<?> screen) {
        if (screen instanceof InventoryScreen) {
            return getLayout(INVENTORY_LAYOUT);
        }

        try {
            final var menuTypeId = BuiltInRegistries.MENU.getKey(screen.getMenu().getType());
            return getLayout(menuTypeId);
        } catch (Exception e) {
            return getLayout(DEFAULT_LAYOUT);
        }
    }

    public static TrashContainerLayout getLayout(Identifier identifier) {
        final var layout = layoutMap.get(identifier);
        if (layout == null) {
            return getDefaultLayout();
        }

        return layout;
    }

    public static TrashContainerLayout getDefaultLayout() {
        return layoutMap.getOrDefault(DEFAULT_LAYOUT, FALLBACK);
    }

    public static void registerLayout(Identifier identifier, TrashContainerLayout layout) {
        layoutMap.put(identifier, layout);
    }

    public static void replaceLayouts(Map<Identifier, TrashContainerLayout> layouts) {
        layoutMap.clear();
        layoutMap.putAll(new HashMap<>(layouts));
    }

}
