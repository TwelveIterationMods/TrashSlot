package net.blay09.mods.trashslot.client;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.blay09.mods.trashslot.CommonProxy;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.net.MessageDelete;
import net.blay09.mods.trashslot.net.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    private Slot mouseSlot;
    private IIcon trashSlotIcon;
    private GuiTrashSlot guiTrashSlot;
    private boolean wasDeleteDown;

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(event.entity == Minecraft.getMinecraft().thePlayer) {
            if (findSlotTrash(Minecraft.getMinecraft().thePlayer.inventoryContainer) == null) {
                patchContainer(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.inventoryContainer);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        if(entityPlayer != null) {
            if (TrashSlot.enableDeleteKey && Minecraft.getMinecraft().currentScreen != null && entityPlayer.openContainer == entityPlayer.inventoryContainer) {
                if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
                    if (!wasDeleteDown) {
                        if (mouseSlot != null && mouseSlot.getHasStack() && mouseSlot.inventory == entityPlayer.inventory && mouseSlot.getSlotIndex() < entityPlayer.inventory.getSizeInventory() - 4) {
                            NetworkHandler.instance.sendToServer(new MessageDelete(mouseSlot.slotNumber, (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))));
                        }
                    }
                    wasDeleteDown = true;
                } else {
                    wasDeleteDown = false;
                }
            }
        }
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent event) {
        if(event.map.getTextureType() == 1) {
            trashSlotIcon = event.map.registerIcon("trashslot:trashcan");
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent event) {
        if(event.gui instanceof GuiInventory) {
            guiTrashSlot = new GuiTrashSlot((GuiInventory) event.gui, event.gui.width / 2 + 57, event.gui.height / 2 + 79);
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if(event.gui instanceof GuiInventory) {
            mouseSlot = ((GuiInventory) event.gui).getSlotAtPosition(event.mouseX, event.mouseY);
            guiTrashSlot.drawBackground(event.mouseX, event.mouseY);
        }
    }

    @Override
    public boolean canDropStack(int mouseX, int mouseY, boolean result) {
        if(Minecraft.getMinecraft().currentScreen instanceof GuiInventory) {
            return result && !guiTrashSlot.isInside(mouseX, mouseY);
        }
        return result;
    }

    @Override
    public IIcon getSlotBackgroundIcon() {
        return TrashSlot.drawSlotBackground ? trashSlotIcon : null;
    }
}
