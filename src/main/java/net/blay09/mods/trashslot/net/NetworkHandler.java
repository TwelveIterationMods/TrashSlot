package net.blay09.mods.trashslot.net;

import net.blay09.mods.trashslot.TrashSlot;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    public static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(TrashSlot.MOD_ID);

    public static void init() {
        instance.registerMessage(HandlerDelete.class, MessageDelete.class, 1, Side.SERVER);
    }

}
