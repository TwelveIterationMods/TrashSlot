package net.blay09.mods.trashslot.client;

import com.google.common.collect.Lists;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.TrashSlotConfig;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.blay09.mods.trashslot.client.gui.GuiHelper;
import net.blay09.mods.trashslot.client.gui.GuiTrashSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.config.GuiUtils;

public class TrashSlotGui {

    private final SlotTrash slotTrash = new SlotTrash();
    private GuiTrashSlot guiTrashSlot;
    private ContainerSettings currentContainerSettings = ContainerSettings.NONE;
    private boolean ignoreMouseUp;

    private boolean sentMissingMessage;
    private long missingMessageTime;
    private boolean isLeftMouseDown;

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiContainerCreative) {
            currentContainerSettings = ContainerSettings.NONE;
            guiTrashSlot = null;
            return;
        }

        if (event.getGui() instanceof GuiContainer) {
            GuiContainer gui = (GuiContainer) event.getGui();
            if (!TrashSlot.isServerSideInstalled && !sentMissingMessage) {
                missingMessageTime = System.currentTimeMillis();
                sentMissingMessage = true;
                return;
            }

            // For some reason this event gets fired with GuiInventory right after opening the creative menu, AFTER it got fired for GuiContainerCreative
            if (gui instanceof GuiInventory && Minecraft.getInstance().player.abilities.isCreativeMode) {
                return;
            }

            IGuiContainerLayout layout = LayoutManager.getLayout(gui);
            currentContainerSettings = TrashSlotConfig.getSettings(gui, layout);
            if (currentContainerSettings != ContainerSettings.NONE) {
                guiTrashSlot = new GuiTrashSlot(this, gui, layout, currentContainerSettings, slotTrash);
            } else {
                guiTrashSlot = null;
            }
        } else {
            currentContainerSettings = ContainerSettings.NONE;
            guiTrashSlot = null;
        }
    }

    @SubscribeEvent
    public void onGuiMouseReleased(GuiScreenEvent.MouseReleasedEvent.Pre event) {
        if (event.getButton() == 0) {
            isLeftMouseDown = false;
        }

        if (ignoreMouseUp) {
            event.setCanceled(true);
            ignoreMouseUp = false;
        }
    }

    @SubscribeEvent
    public void onGuiMouseClicked(GuiScreenEvent.MouseClickedEvent.Pre event) {
        if (event.getButton() == 0) {
            isLeftMouseDown = true;
        }

        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
            return;
        }

        int mouseButton = event.getButton();
        if (event.getGui() instanceof GuiContainer) {
            GuiContainer gui = (GuiContainer) event.getGui();
            double mouseX = event.getMouseX();
            double mouseY = event.getMouseY();
            if (gui.isSlotSelected(slotTrash, mouseX, mouseY)) {
                EntityPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack mouseItem = player.inventory.getItemStack();
                    boolean isRightClick = mouseButton == 1;
                    if (mouseItem.isEmpty()) {
                        deletionProvider.undeleteLast(player, slotTrash, isRightClick);
                    } else {
                        deletionProvider.deleteMouseItem(player, mouseItem, slotTrash, isRightClick);
                    }

                    event.setCanceled(true);
                    ignoreMouseUp = true;
                }
            } else if (guiTrashSlot.isInside((int) mouseX, (int) mouseY)) {
                // Prevent click-through on the background and border of the slot
                event.setCanceled(true);
                ignoreMouseUp = true;
            }
        }
    }

    @SubscribeEvent // TODO not currently fired due to https://github.com/MinecraftForge/MinecraftForge/pull/5367 not being pulled
    public void onGuiKeyboard(GuiScreenEvent.KeyboardKeyPressedEvent.Post event) {
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null) {
            return;
        }

        int keyCode = event.getKeyCode();
        int scanCode = event.getScanCode();
        InputMappings.Input input = InputMappings.getInputByCode(keyCode, scanCode);
        if (currentContainerSettings.isEnabled()) {
            boolean isDelete = KeyBindings.keyBindDelete.isActiveAndMatches(input);
            boolean isDeleteAll = KeyBindings.keyBindDeleteAll.isActiveAndMatches(input);
            if (isDelete || isDeleteAll) {
                EntityPlayer entityPlayer = Minecraft.getInstance().player;
                if (entityPlayer != null && event.getGui() instanceof GuiContainer) {
                    GuiContainer gui = ((GuiContainer) event.getGui());
                    Slot mouseSlot = gui.getSlotUnderMouse();
                    if (mouseSlot != null && mouseSlot.getHasStack()) {
                        deletionProvider.deleteContainerItem(gui.inventorySlots, mouseSlot.slotNumber, isDeleteAll);
                    } else {
                        double mouseX = Minecraft.getInstance().mouseHelper.getMouseX();
                        double mouseY = Minecraft.getInstance().mouseHelper.getMouseY();

                        if (gui.isSlotSelected(slotTrash, mouseX, mouseY)) {
                            deletionProvider.emptyTrashSlot(slotTrash);
                        }
                    }
                }
            }
        }

        if (event.getGui() instanceof GuiContainer && currentContainerSettings != ContainerSettings.NONE) {
            if (KeyBindings.keyBindToggleSlot.isActiveAndMatches(input)) {
                currentContainerSettings.setEnabled(!currentContainerSettings.isEnabled());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null) {
            return;
        }

        if (!currentContainerSettings.isEnabled()) {
            return;
        }

        if (event.getGui() instanceof GuiContainer) {
            GuiContainer gui = (GuiContainer) event.getGui();
            if (guiTrashSlot != null) {
                guiTrashSlot.update(event.getMouseX(), event.getMouseY());
                guiTrashSlot.drawBackground();
                if (gui.isSlotSelected(slotTrash, event.getMouseX(), event.getMouseY())) {
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepthTest();
                    int j1 = gui.getGuiLeft() + slotTrash.xPos;
                    int k1 = gui.getGuiTop() + slotTrash.yPos;
                    GlStateManager.colorMask(true, true, true, false);
                    GuiHelper.drawGradientRect(j1, k1, j1 + 16, k1 + 16, -600, -2130706433, -2130706433);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableDepthTest();
                }
            }
        }
    }

    @SubscribeEvent
    public void onBackgroundDrawn(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (event.getGui() instanceof GuiContainer) {
            GuiContainer gui = (GuiContainer) event.getGui();
            if (missingMessageTime != 0 && System.currentTimeMillis() - missingMessageTime < 3000) {
                String noHabloEspanol = TextFormatting.RED + I18n.format("trashslot.serverNotInstalled");
                GuiUtils.drawHoveringText(Lists.newArrayList(noHabloEspanol), gui.getGuiLeft() + gui.getXSize() / 2 - gui.mc.fontRenderer.getStringWidth(noHabloEspanol) / 2, 25, gui.width, gui.height, -1, gui.mc.fontRenderer);
            }

            DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
            if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
                return;
            }

            GlStateManager.pushMatrix();
            GlStateManager.translatef(gui.getGuiLeft(), gui.getGuiTop(), 0);
            RenderHelper.enableGUIStandardItemLighting();
            gui.drawSlot(slotTrash);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof GuiContainer) {
            GuiContainer gui = (GuiContainer) event.getGui();
            DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
            if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
                return;
            }

            boolean isMouseSlot = gui.isSlotSelected(slotTrash, event.getMouseX(), event.getMouseY());
            InventoryPlayer inventoryPlayer = Minecraft.getInstance().player.inventory;
            if (isMouseSlot && inventoryPlayer.getItemStack().isEmpty() && slotTrash.getHasStack()) {
                gui.renderToolTip(slotTrash.getStack(), event.getMouseX(), event.getMouseY());
            }
        }
    }

    public GuiTrashSlot getGuiTrashSlot() {
        return guiTrashSlot;
    }

    public SlotTrash getTrashSlot() {
        return slotTrash;
    }

    public boolean isLeftMouseDown() {
        return isLeftMouseDown;
    }
}
