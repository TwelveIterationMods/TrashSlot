package net.blay09.mods.trashslot.client;

import net.blay09.mods.trashslot.CommonProxy;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.net.MessageDelete;
import net.blay09.mods.trashslot.net.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    public static TextureAtlasSprite trashSlotIcon;

    private Slot mouseSlot;
    private GuiTrashSlot guiTrashSlot;
    private boolean wasDeleteDown;
    private boolean wasInCreative;

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
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if(entityPlayer != null && wasInCreative && !(gui instanceof GuiContainerCreative)) {
            if(findSlotTrash(entityPlayer.inventoryContainer) == null) {
                patchContainer(entityPlayer, entityPlayer.inventoryContainer);
            }
            wasInCreative = false;
        }
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        trashSlotIcon = event.map.registerSprite(new ResourceLocation("trashslot", "items/trashcan"));
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Pre event) {
        if(event.gui instanceof GuiContainerCreative) {
            unpatchContainer(Minecraft.getMinecraft().thePlayer.inventoryContainer);
            wasInCreative = true;
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if(event.gui instanceof GuiInventory) {
            GuiInventory gui = (GuiInventory) event.gui;
            float x = TrashSlot.trashSlotX;
            float y = TrashSlot.trashSlotY;
            if(TrashSlot.trashSlotRelative) {
                x *= gui.width;
            } else if(x == -1) {
                x = gui.guiLeft + gui.xSize - GuiTrashSlot.UPDOWN_WIDTH;
            } else {
                x += gui.guiLeft + gui.xSize / 2;
            }
            if(TrashSlot.trashSlotRelative) {
                y *= gui.height;
            } else if(y == -1) {
                y = gui.guiTop + gui.ySize - 4;
            } else {
                y += gui.guiTop + gui.ySize / 2;
            }
            guiTrashSlot = new GuiTrashSlot(gui, findSlotTrash(gui.inventorySlots), (int) x, (int) y);
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if(event.gui instanceof GuiInventory) {
            mouseSlot = ((GuiInventory) event.gui).getSlotAtPosition(event.mouseX, event.mouseY);
            guiTrashSlot.update(event.mouseX, event.mouseY);
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

}
