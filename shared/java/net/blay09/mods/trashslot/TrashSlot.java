package net.blay09.mods.trashslot;

import net.blay09.mods.balm.event.BalmEvents;
import net.blay09.mods.balm.network.BalmNetworking;
import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.blay09.mods.trashslot.client.gui.layout.ChestContainerLayout;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.blay09.mods.trashslot.config.TrashSlotConfig;
import net.blay09.mods.trashslot.network.TrashSlotContentMessage;
import net.blay09.mods.trashslot.network.ModNetworking;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;

public class TrashSlot {

    public static final String MOD_ID = "trashslot";
    public static boolean isServerSideInstalled;

    public static void initialize() {
        TrashSlotAPI.__setupAPI(new InternalMethodsImpl());

        ModNetworking.initialize();
        TrashSlotConfig.initialize();

        TrashSlotAPI.registerLayout(InventoryScreen.class, SimpleGuiContainerLayout.DEFAULT_ENABLED);
        TrashSlotAPI.registerLayout(CraftingScreen.class, SimpleGuiContainerLayout.DEFAULT_ENABLED);
        TrashSlotAPI.registerLayout(ContainerScreen.class, new ChestContainerLayout());

        BalmEvents.onPlayerLogin(player -> {
            TrashSlotContentMessage message = new TrashSlotContentMessage(ItemStack.EMPTY);
            BalmNetworking.sendTo(player, message);
        });

        BalmEvents.onPlayerRespawn((oldPlayer, newPlayer) -> {
            TrashSlotContentMessage message = new TrashSlotContentMessage(ItemStack.EMPTY);
            BalmNetworking.sendTo(newPlayer, message);
        });

        BalmEvents.onPlayerOpenMenu((player, menu) -> {
            ItemStack itemStack = TrashHelper.getTrashItem(player);
            TrashSlotContentMessage message = new TrashSlotContentMessage(itemStack);
            BalmNetworking.sendTo(player, message);
        });
    }

}
