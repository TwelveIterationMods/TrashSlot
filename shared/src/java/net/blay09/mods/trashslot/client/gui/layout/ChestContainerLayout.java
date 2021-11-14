package net.blay09.mods.trashslot.client.gui.layout;

import net.blay09.mods.trashslot.api.SlotRenderStyle;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class ChestContainerLayout extends SimpleGuiContainerLayout {
    public ChestContainerLayout() {
        enableDefaultSnaps();
        enableDefaultCollision();
        setEnabledByDefault();
    }

    @Override
    public int getSlotOffsetY(AbstractContainerScreen<?> screen, SlotRenderStyle renderStyle) {
        return switch (renderStyle) {
            case ATTACH_BOTTOM_CENTER, ATTACH_BOTTOM_LEFT, ATTACH_BOTTOM_RIGHT, ATTACH_LEFT_BOTTOM, ATTACH_RIGHT_BOTTOM -> -1;
            default -> 0;
        };
    }

    @Override
    public String getContainerId(AbstractContainerScreen<?> screen) {
        if (screen.getMenu().slots.size() > 63) {
            return super.getContainerId(screen) + "_large";
        }
        return super.getContainerId(screen);
    }
}
