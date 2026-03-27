package net.blay09.mods.trashslot.client;

import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.trashslot.InternalMethodsImpl;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.minecraft.world.item.ItemStack;

public class TrashSlotClient {
    public static void initialize(BalmClientRegistrars registrars) {
        ModKeyMappings.initialize();

        TrashSlotGuiHandler.initialize();
        registrars.resourceReloadListeners(registrar ->
                registrar.register("container_layouts", new TrashContainerLayoutReloadListener()));

        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client
                -> TrashSlot.isServerSideInstalled = false);
    }

    public static void receivedTrashSlotContent(ItemStack itemStack) {
        TrashSlot.isServerSideInstalled = true;
        TrashSlotGuiHandler.getTrashSlot().set(itemStack);
    }
}
