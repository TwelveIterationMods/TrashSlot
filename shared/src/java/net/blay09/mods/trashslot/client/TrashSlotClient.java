package net.blay09.mods.trashslot.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.event.client.ConnectedToServerEvent;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.blay09.mods.trashslot.client.gui.layout.ChestContainerLayout;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;

public class TrashSlotClient {
    public static void initialize() {
        ModKeyMappings.initialize(BalmClient.getKeyMappings());
        ModTextures.initialize(BalmClient.getTextures());

        TrashSlotAPI.registerLayout(InventoryScreen.class, SimpleGuiContainerLayout.DEFAULT_ENABLED);
        TrashSlotAPI.registerLayout(CraftingScreen.class, SimpleGuiContainerLayout.DEFAULT_ENABLED);
        TrashSlotAPI.registerLayout(ContainerScreen.class, new ChestContainerLayout());

        TrashSlotGuiHandler.initialize();

        Balm.getEvents().onEvent(ConnectedToServerEvent.class, it -> TrashSlot.isServerSideInstalled = false);

        BalmClient.initialize(TrashSlot.MOD_ID);
    }

    public static void receivedTrashSlotContent(ItemStack itemStack) {
        TrashSlot.isServerSideInstalled = true;
        TrashSlotGuiHandler.getTrashSlot().set(itemStack);
    }
}
