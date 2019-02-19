package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.inventory.GuiContainer;

import java.awt.*;
import java.util.List;

public interface IGuiContainerLayout {
    List<Rectangle> getCollisionAreas(GuiContainer gui);

    List<Snap> getSnaps(GuiContainer gui, SlotRenderStyle renderStyle);

    SlotRenderStyle getSlotRenderStyle(GuiContainer gui, int slotX, int slotY);

    int getDefaultSlotX(GuiContainer gui);

    int getDefaultSlotY(GuiContainer gui);

    boolean isEnabledByDefault();

    int getSlotOffsetX(GuiContainer gui, SlotRenderStyle renderStyle);

    int getSlotOffsetY(GuiContainer gui, SlotRenderStyle renderStyle);

    default String getContainerId(GuiContainer gui) {
        return gui.getClass().getName().replace('.', '/');
    }
}
