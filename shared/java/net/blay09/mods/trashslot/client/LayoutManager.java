package net.blay09.mods.trashslot.client;

import com.google.common.collect.Maps;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.Map;

public class LayoutManager {

    private static final Map<String, IGuiContainerLayout> layoutMap = Maps.newHashMap();

    public static IGuiContainerLayout getLayout(AbstractContainerScreen<?> gui) {
        IGuiContainerLayout layout = layoutMap.get(gui.getClass().getName());
        if (layout == null) {
            return SimpleGuiContainerLayout.DEFAULT;
        }

        return layout;
    }

    public static void registerLayout(Class<? extends AbstractContainerScreen<?>> clazz, IGuiContainerLayout layout) {
        layoutMap.put(clazz.getName(), layout);
    }

}
