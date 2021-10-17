package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;

import java.util.List;

public interface IGuiContainerLayout {
    List<Rect2i> getCollisionAreas(AbstractContainerScreen<?> gui);

    List<Snap> getSnaps(AbstractContainerScreen<?> gui, SlotRenderStyle renderStyle);

    SlotRenderStyle getSlotRenderStyle(AbstractContainerScreen<?> gui, int slotX, int slotY);

    int getDefaultSlotX(AbstractContainerScreen<?> gui);

    int getDefaultSlotY(AbstractContainerScreen<?> gui);

    boolean isEnabledByDefault();

    int getSlotOffsetX(AbstractContainerScreen<?> gui, SlotRenderStyle renderStyle);

    int getSlotOffsetY(AbstractContainerScreen<?> gui, SlotRenderStyle renderStyle);

    default String getContainerId(AbstractContainerScreen<?> gui) {
        return gui.getClass().getName().replace('.', '/');
    }
}
