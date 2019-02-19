package net.blay09.mods.trashslot.net;

import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

    public static SimpleChannel instance;

    public static void init() {
        instance = NetworkRegistry.newSimpleChannel(new ResourceLocation(TrashSlot.MOD_ID, "network"), () -> "1.0", it -> {
            TrashSlot.isServerSideInstalled = it.equals("1.0");
            return true;
        }, it -> true);

        instance.registerMessage(0, MessageDeleteFromSlot.class, MessageDeleteFromSlot::encode, MessageDeleteFromSlot::decode, MessageDeleteFromSlot::handle);
        instance.registerMessage(1, MessageTrashSlotClick.class, MessageTrashSlotClick::encode, MessageTrashSlotClick::decode, MessageTrashSlotClick::handle);
        instance.registerMessage(2, MessageTrashSlotContent.class, MessageTrashSlotContent::encode, MessageTrashSlotContent::decode, MessageTrashSlotContent::handle);
    }

}
