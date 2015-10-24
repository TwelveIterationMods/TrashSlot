package net.blay09.mods.trashslot;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.blay09.mods.trashslot.net.MessageHello;
import net.blay09.mods.trashslot.net.NetworkHandler;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.IIcon;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

import java.util.HashSet;
import java.util.List;

public class CommonProxy {

    private final HashSet<String> modInstalled = new HashSet<>();

    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        NetworkHandler.instance.sendTo(new MessageHello(NetworkHandler.PROTOCOL_VERSION), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if(modInstalled.contains(event.player.getCommandSenderName())) {
            patchContainer(event.player, event.player.inventoryContainer);
        }
    }

    @SubscribeEvent
    public void onOpenContainer(PlayerOpenContainerEvent event) {
        if(event.entityPlayer.openContainer instanceof GuiContainerCreative.ContainerCreative) {
            unpatchContainer(event.entityPlayer.inventoryContainer);
        } else if(event.entityPlayer.openContainer == event.entityPlayer.inventoryContainer && modInstalled.contains(event.entityPlayer.getCommandSenderName())) {
            if(findSlotTrash(event.entityPlayer.inventoryContainer) == null) {
                patchContainer(event.entityPlayer, event.entityPlayer.inventoryContainer);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected SlotTrash patchContainer(EntityPlayer entityPlayer, Container container) {
        SlotTrash slot = new SlotTrash(entityPlayer, 152, 165);
        slot.slotNumber = container.inventorySlots.size();
        container.inventorySlots.add(slot);
        container.inventoryItemStacks.add(null);
        return slot;
    }

    protected SlotTrash unpatchContainer(Container container) {
        for(int i = container.inventorySlots.size() - 1; i >= 0; i--) {
            if(container.inventorySlots.get(i).getClass() == SlotTrash.class) {
                return (SlotTrash) container.inventorySlots.remove(i);
            }
        }
        return null;
    }

    public Slot findSlotTrash(Container container) {
        List slots = container.inventorySlots;
        for(int i = slots.size() - 1; i >= 0; i--) {
            if(slots.get(i).getClass() == SlotTrash.class) {
                return (Slot)slots.get(i);
            }
        }
        return null;
    }

    public boolean canDropStack(int mouseX, int mouseY, boolean result) {
        return result;
    }

    public IIcon getSlotBackgroundIcon() {
        return null;
    }

    public void receivedHello(EntityPlayer entityPlayer) {
        modInstalled.add(entityPlayer.getCommandSenderName());
        if(findSlotTrash(entityPlayer.inventoryContainer) == null) {
            patchContainer(entityPlayer, entityPlayer.inventoryContainer);
        }
    }
}
