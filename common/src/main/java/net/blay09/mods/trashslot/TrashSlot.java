package net.blay09.mods.trashslot;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.balm.api.event.PlayerOpenMenuEvent;
import net.blay09.mods.balm.api.event.PlayerRespawnEvent;
import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.blay09.mods.trashslot.config.TrashSlotConfig;
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
        ModNetworking.initialize(Balm.getNetworking());

        Balm.getEvents().onEvent(PlayerLoginEvent.class, event -> {
            TrashHelper.setTrashItem(event.getPlayer(), ItemStack.EMPTY);
            Balm.getNetworking().sendTo(event.getPlayer(), new MessageTrashSlotContent(ItemStack.EMPTY));
        });

        Balm.getEvents().onEvent(PlayerRespawnEvent.class, event -> Balm.getNetworking().sendTo(event.getNewPlayer(), new MessageTrashSlotContent(ItemStack.EMPTY)));

        Balm.getEvents().onEvent(PlayerOpenMenuEvent.class, event -> {
            ItemStack trashItem = TrashHelper.getTrashItem(event.getPlayer());
            Balm.getNetworking().sendTo(event.getPlayer(), new MessageTrashSlotContent(trashItem));
        });
    }
}
