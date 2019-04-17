package net.blay09.mods.trashslot;

import net.blay09.mods.trashslot.client.SlotTrash;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.blay09.mods.trashslot.net.MessageTrashSlotContent;
import net.blay09.mods.trashslot.net.NetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class CommonProxy {

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerOpenContainer(PlayerContainerEvent.Open event) {
        EntityPlayer player = event.getEntityPlayer();
        if(player instanceof EntityPlayerMP) {
            ItemStack trashItem = TrashHelper.getTrashItem(player);
            NetworkHandler.instance.sendTo(new MessageTrashSlotContent(trashItem), (EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof EntityPlayerMP) {
            NetworkHandler.instance.sendTo(new MessageTrashSlotContent(ItemStack.EMPTY), (EntityPlayerMP) event.getEntity());
        }
    }

    public SlotTrash getTrashSlot() {
        return new SlotTrash();
    }

    @Nullable
    public DeletionProvider getDeletionProvider() {
        return null;
    }

    public void reloadDeletionProvider() {

    }
}
