package net.blay09.mods.trashslot.fabric.compat;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import net.blay09.mods.trashslot.client.TrashSlotGuiHandler;
import net.blay09.mods.trashslot.client.gui.TrashSlotComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;

import java.util.Collections;

@Environment(EnvType.CLIENT)
public class RoughlyEnoughItemsIntegration implements REIClientPlugin {

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(AbstractContainerScreen.class, (ExclusionZonesProvider<AbstractContainerScreen<?>>) abstractContainerScreen -> {
            TrashSlotComponent widget = TrashSlotGuiHandler.getTrashSlotComponent();
            Rect2i rect = widget != null ? widget.getRectangle() : null;
            return widget != null && widget.isVisible() ? Collections.singletonList(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight())) : Collections.emptyList();
        });
    }
}
