package net.blay09.mods.trashslot.api;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.awt.*;
import java.util.List;

public interface IGuiContainerLayout {
    List<Rectangle> getCollisionAreas(AbstractContainerScreen<?> screen);

    List<Snap> getSnaps(AbstractContainerScreen<?> screen, SlotRenderStyle renderStyle);

    SlotRenderStyle getSlotRenderStyle(AbstractContainerScreen<?> screen, int slotX, int slotY);

    int getDefaultSlotX(AbstractContainerScreen<?> screen);

    int getDefaultSlotY(AbstractContainerScreen<?> screen);

    boolean isEnabledByDefault();

    int getSlotOffsetX(AbstractContainerScreen<?> screen, SlotRenderStyle renderStyle);

    int getSlotOffsetY(AbstractContainerScreen<?> screen, SlotRenderStyle renderStyle);

    default String getContainerId(AbstractContainerScreen<?> screen) {
        return screen.getClass().getName().replace('.', '/');
    }
}
