package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;

import java.awt.*;
import java.util.List;

public interface IGuiContainerLayout {
    List<Rectangle> getCollisionAreas(ContainerScreen<?> gui);

    List<Snap> getSnaps(ContainerScreen<?> gui, SlotRenderStyle renderStyle);

    SlotRenderStyle getSlotRenderStyle(ContainerScreen<?> gui, int slotX, int slotY);

    int getDefaultSlotX(ContainerScreen<?> gui);

    int getDefaultSlotY(ContainerScreen<?> gui);

    boolean isEnabledByDefault();

    int getSlotOffsetX(ContainerScreen<?> gui, SlotRenderStyle renderStyle);

    int getSlotOffsetY(ContainerScreen<?> gui, SlotRenderStyle renderStyle);

    default String getContainerId(ContainerScreen<?> gui) {
        return gui.getClass().getName().replace('.', '/');
    }
}
