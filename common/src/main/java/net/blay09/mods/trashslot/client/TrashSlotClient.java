package net.blay09.mods.trashslot.client;

import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.trashslot.InternalMethodsImpl;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.blay09.mods.trashslot.client.gui.layout.ChestContainerLayout;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;

public class TrashSlotClient {
    public static void initialize(BalmClientRegistrars registrars) {
        TrashSlotAPI.__setupAPI(new InternalMethodsImpl());

        ModKeyMappings.initialize();

        TrashSlotAPI.registerLayout(InventoryScreen.class, SimpleGuiContainerLayout.DEFAULT_ENABLED);
        TrashSlotAPI.registerLayout(CraftingScreen.class, SimpleGuiContainerLayout.DEFAULT_ENABLED);
        TrashSlotAPI.registerLayout(ContainerScreen.class, new ChestContainerLayout());

        TrashSlotGuiHandler.initialize();

        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client
                -> TrashSlot.isServerSideInstalled = false);
    }

    public static void receivedTrashSlotContent(ItemStack itemStack) {
        TrashSlot.isServerSideInstalled = true;
        TrashSlotGuiHandler.getTrashSlot().set(itemStack);
    }
}
