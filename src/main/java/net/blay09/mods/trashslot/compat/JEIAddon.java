package net.blay09.mods.trashslot.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.client.TrashSlotGui;
import net.blay09.mods.trashslot.client.gui.GuiTrashSlot;
import net.minecraft.client.gui.inventory.GuiContainer;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.List;

@JEIPlugin
public class JEIAddon implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler() {
            @Override
            public Class<GuiContainer> getGuiContainerClass() {
                return GuiContainer.class;
            }

            @Nullable
            @Override
            public List<Rectangle> getGuiExtraAreas(GuiContainer guiContainer) {
                GuiTrashSlot slot = TrashSlot.trashSlotGui.map(TrashSlotGui::getGuiTrashSlot).orElse(null);
                return slot != null && slot.isVisible() ? Collections.singletonList(slot.getRectangle()) : null;
            }
        });
    }
}
