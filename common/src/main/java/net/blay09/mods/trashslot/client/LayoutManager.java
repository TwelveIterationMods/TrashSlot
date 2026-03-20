package net.blay09.mods.trashslot.client;

import com.google.common.collect.Maps;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.Map;

public class LayoutManager {

    private static final Map<String, TrashContainerLayout> layoutMap = Maps.newHashMap();

    public static TrashContainerLayout getLayout(AbstractContainerScreen<?> gui) {
        TrashContainerLayout layout = layoutMap.get(gui.getClass().getName());
        if (layout == null) {
            return SimpleGuiContainerLayout.DEFAULT;
        }

        return layout;
    }

    public static void registerLayout(Class<? extends AbstractContainerScreen<?>> clazz, TrashContainerLayout layout) {
        layoutMap.put(clazz.getName(), layout);
    }

}
