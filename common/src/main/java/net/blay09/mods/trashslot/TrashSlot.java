package net.blay09.mods.trashslot;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.event.callback.ServerPlayerCallback;
import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.blay09.mods.trashslot.network.MessageTrashSlotContent;
import net.blay09.mods.trashslot.network.ModNetworking;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrashSlot {

    public static final Logger logger = LogManager.getLogger();

    public static final String MOD_ID = "trashslot";
    public static boolean isServerSideInstalled;

    public static void initialize() {
        TrashSlotAPI.__setupAPI(new InternalMethodsImpl());

        TrashSlotConfig.initialize();
        ModNetworking.initialize(Balm.networking());

        ServerPlayerCallback.Login.EVENT.register(player -> {
            TrashHelper.setTrashItem(player, ItemStack.EMPTY);
            Balm.networking().sendTo(player, new MessageTrashSlotContent(ItemStack.EMPTY));
        });

        ServerPlayerCallback.Respawn.EVENT.register((oldPlayer, newPlayer)
                -> Balm.networking().sendTo(newPlayer, new MessageTrashSlotContent(ItemStack.EMPTY)));

        ServerPlayerCallback.OpenMenu.EVENT.register((player, menu) -> {
            ItemStack trashItem = TrashHelper.getTrashItem(player);
            Balm.networking().sendTo(player, new MessageTrashSlotContent(trashItem));
        });
    }
}
