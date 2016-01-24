package net.blay09.mods.trashslot.client;

import net.blay09.mods.trashslot.CommonProxy;
import net.blay09.mods.trashslot.SlotTrash;
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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    public static TextureAtlasSprite trashSlotIcon;
    private static final int HELLO_TIMEOUT = 20 * 10;

    private int helloTimeout;
    private boolean isEnabled;
    private Slot mouseSlot;
    private GuiTrashSlot guiTrashSlot;
    private boolean wasDeleteDown;
    private boolean wasInCreative;
    private boolean neiLoaded;

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        MinecraftForge.EVENT_BUS.register(this);
        neiLoaded = Loader.isModLoaded("NotEnoughItems");
    }

    @Override
    public void addScheduledTask(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }

    @SubscribeEvent
    public void connectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        helloTimeout = HELLO_TIMEOUT;
        isEnabled = false;
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (isEnabled && event.entity == Minecraft.getMinecraft().thePlayer) {
            if (findSlotTrash(Minecraft.getMinecraft().thePlayer.inventoryContainer) == null) {
                patchContainer(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.inventoryContainer);
            }
        }
    }

    @SubscribeEvent
    public void onOpenContainer(PlayerOpenContainerEvent event) {
        if (event.entityPlayer.openContainer instanceof GuiContainerCreative.ContainerCreative) {
            unpatchContainer(event.entityPlayer.inventoryContainer);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        if (entityPlayer != null) {
            if (helloTimeout > 0) {
                helloTimeout--;
                if (helloTimeout <= 0) {
                    isEnabled = false;
                    unpatchContainer(entityPlayer.inventoryContainer);
                    entityPlayer.addChatMessage(new ChatComponentText("This server does not have TrashSlot installed. It will be disabled."));
                }
            }
            if (!isEnabled) {
                return;
            }
            if (TrashSlot.enableDeleteKey && Minecraft.getMinecraft().currentScreen != null && entityPlayer.openContainer == entityPlayer.inventoryContainer) {
                if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
                    if (!wasDeleteDown) {
                        if (mouseSlot != null && mouseSlot.getHasStack() && ((mouseSlot.inventory == entityPlayer.inventory && mouseSlot.getSlotIndex() < entityPlayer.inventory.getSizeInventory()) || mouseSlot instanceof SlotTrash)) {
                            NetworkHandler.instance.sendToServer(new MessageDelete(mouseSlot.slotNumber, (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))));
                        }
                    }
                    wasDeleteDown = true;
                } else {
                    wasDeleteDown = false;
                }
            }
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (wasInCreative && !(gui instanceof GuiContainerCreative)) {
                if (findSlotTrash(entityPlayer.inventoryContainer) == null) {
                    patchContainer(entityPlayer, entityPlayer.inventoryContainer);
                    if (gui instanceof GuiInventory) {
                        Slot trashSlot = findSlotTrash(((GuiInventory) gui).inventorySlots);
                        if (trashSlot != null) {
                            guiTrashSlot = new GuiTrashSlot((GuiInventory) gui, trashSlot);
                        }
                    }
                }
                wasInCreative = false;
            }
        }
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        trashSlotIcon = event.map.registerSprite(new ResourceLocation("trashslot", "items/trashcan"));
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Pre event) {
        if (isEnabled && event.gui instanceof GuiContainerCreative) {
            unpatchContainer(Minecraft.getMinecraft().thePlayer.inventoryContainer);
            wasInCreative = true;
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (isEnabled && event.gui instanceof GuiInventory) {
            GuiInventory gui = (GuiInventory) event.gui;
            Slot trashSlot = findSlotTrash(gui.inventorySlots);
            if (trashSlot != null) {
                guiTrashSlot = new GuiTrashSlot(gui, trashSlot);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (event.gui instanceof GuiInventory) {
            mouseSlot = ((GuiInventory) event.gui).getSlotAtPosition(event.mouseX, event.mouseY);
            if (neiLoaded) {
                ((GuiInventory) event.gui).guiLeft = event.gui.width / 2 - ((GuiInventory) event.gui).xSize / 2;
                ((GuiInventory) event.gui).guiTop = event.gui.height / 2 - ((GuiInventory) event.gui).ySize / 2;
            }
            if (guiTrashSlot != null) {
                guiTrashSlot.update(event.mouseX, event.mouseY);
                guiTrashSlot.drawBackground(event.mouseX, event.mouseY);
            }
        }
    }

    @Override
    public void receivedHello(EntityPlayer entityPlayer) {
        super.receivedHello(entityPlayer);
        helloTimeout = 0;
        isEnabled = true;
    }

}
