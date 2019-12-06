package net.blay09.mods.trashslot.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.client.TrashSlotGui;
import net.blay09.mods.trashslot.client.gui.GuiTrashSlot;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

@JeiPlugin
public class JEIAddon implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("waystones", "waystones");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(ContainerScreen.class, new IGuiContainerHandler<ContainerScreen>() {
            @Override
            public List<Rectangle2d> getGuiExtraAreas(ContainerScreen containerScreen) {
                GuiTrashSlot slot = TrashSlot.trashSlotGui.map(TrashSlotGui::getGuiTrashSlot).orElse(null);
                return slot != null && slot.isVisible() ? Collections.singletonList(slot.getRectangle()) : Collections.emptyList();
            }
        });
    }

}
