package net.blay09.mods.trashslot.net;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.blay09.mods.trashslot.TrashSlot;

public class NetworkHandler {

    public static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(TrashSlot.MOD_ID);

    public static void init() {
        instance.registerMessage(HandlerDelete.class, MessageDelete.class, 1, Side.SERVER);
    }

}
