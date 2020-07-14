package net.blay09.mods.trashslot.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.TrashSlotConfig;
import net.blay09.mods.trashslot.TrashSlotSaveState;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.blay09.mods.trashslot.client.gui.GuiHelper;
import net.blay09.mods.trashslot.client.gui.GuiTrashSlot;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TrashSlotGuiHandler {

    private final SlotTrash slotTrash = new SlotTrash();
    private GuiTrashSlot guiTrashSlot;
    private ContainerSettings currentContainerSettings = ContainerSettings.NONE;
    private boolean ignoreMouseUp;

    private boolean sentMissingMessage;
    private long missingMessageTime;
    private boolean isLeftMouseDown;

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof CreativeScreen) {
            currentContainerSettings = ContainerSettings.NONE;
            guiTrashSlot = null;
            return;
        }

        if (event.getGui() instanceof ContainerScreen<?>) {
            ContainerScreen<?> gui = (ContainerScreen<?>) event.getGui();
            if (!TrashSlot.isServerSideInstalled && !sentMissingMessage) {
                missingMessageTime = System.currentTimeMillis();
                sentMissingMessage = true;
                return;
            }

            // For some reason this event gets fired with GuiInventory right after opening the creative menu, AFTER it got fired for GuiContainerCreative
            if (gui instanceof InventoryScreen && Minecraft.getInstance().player.abilities.isCreativeMode) {
                return;
            }

            IGuiContainerLayout layout = LayoutManager.getLayout(gui);
            currentContainerSettings = TrashSlotSaveState.getSettings(gui, layout);
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
        InputMappings.Input input = InputMappings.Type.MOUSE.getOrMakeInput(mouseButton);
        if (runKeyBindings(event.getGui(), input)) {
            event.setCanceled(true);
            return;
        }

        if (event.getGui() instanceof ContainerScreen<?>) {
            ContainerScreen<?> gui = (ContainerScreen<?>) event.getGui();
            double mouseX = event.getMouseX();
            double mouseY = event.getMouseY();
            if (gui.isSlotSelected(slotTrash, mouseX, mouseY)) {
                PlayerEntity player = Minecraft.getInstance().player;
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

    @SubscribeEvent
    public void onGuiKeyboard(GuiScreenEvent.KeyboardKeyPressedEvent.Post event) {
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null) {
            return;
        }

        int keyCode = event.getKeyCode();
        int scanCode = event.getScanCode();
        InputMappings.Input input = InputMappings.getInputByCode(keyCode, scanCode);
        if (runKeyBindings(event.getGui(), input)) {
            event.setCanceled(true);
        }
    }

    private boolean runKeyBindings(Screen screen, InputMappings.Input input) {
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null) {
            return false;
        }

        if (currentContainerSettings.isEnabled()) {
            boolean isDelete = ModKeyBindings.keyBindDelete.isActiveAndMatches(input);
            boolean isDeleteAll = ModKeyBindings.keyBindDeleteAll.isActiveAndMatches(input);
            if (isDelete || isDeleteAll) {
                PlayerEntity entityPlayer = Minecraft.getInstance().player;
                if (entityPlayer != null && screen instanceof ContainerScreen<?>) {
                    ContainerScreen<?> gui = ((ContainerScreen<?>) screen);
                    Slot mouseSlot = gui.getSlotUnderMouse();
                    if (mouseSlot != null && mouseSlot.getHasStack()) {
                        deletionProvider.deleteContainerItem(gui.getContainer(), mouseSlot.slotNumber, isDeleteAll, slotTrash);
                    } else {
                        MainWindow mainWindow = Minecraft.getInstance().getMainWindow();
                        double rawMouseX = Minecraft.getInstance().mouseHelper.getMouseX();
                        double rawMouseY = Minecraft.getInstance().mouseHelper.getMouseY();
                        double mouseX = rawMouseX * (double) mainWindow.getScaledWidth() / (double) mainWindow.getWidth();
                        double mouseY = rawMouseY * (double) mainWindow.getScaledHeight() / (double) mainWindow.getHeight();

                        if (gui.isSlotSelected(slotTrash, mouseX, mouseY)) {
                            deletionProvider.emptyTrashSlot(slotTrash);
                        }
                    }
                    return true;
                }
            }
        }

        if (screen instanceof ContainerScreen<?> && currentContainerSettings != ContainerSettings.NONE) {
            if (ModKeyBindings.keyBindToggleSlot.isActiveAndMatches(input)) {
                currentContainerSettings.setEnabled(!currentContainerSettings.isEnabled());
                TrashSlotSaveState.save();
                return true;
            }
        }

        return false;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDrawScreen(GuiContainerEvent.DrawBackground event) {
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
            return;
        }

        ContainerScreen<?> gui = (ContainerScreen<?>) event.getGuiContainer();
        if (guiTrashSlot != null) {
            guiTrashSlot.update(event.getMouseX(), event.getMouseY());
            guiTrashSlot.drawBackground(event.getMatrixStack());
            if (gui.isSlotSelected(slotTrash, event.getMouseX(), event.getMouseY())) {
                RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                int j1 = gui.getGuiLeft() + slotTrash.xPos;
                int k1 = gui.getGuiTop() + slotTrash.yPos;
                RenderSystem.colorMask(true, true, true, false);
                GuiHelper.drawGradientRect(event.getMatrixStack(), j1, k1, j1 + 16, k1 + 16, -600, -2130706433, -2130706433);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }

            if (missingMessageTime != 0 && System.currentTimeMillis() - missingMessageTime < 3000) {
                TextComponent noHabloEspanol = new TranslationTextComponent("trashslot.serverNotInstalled");
                noHabloEspanol.func_240699_a_(TextFormatting.RED);
                gui.renderTooltip(event.getMatrixStack(), Lists.newArrayList(noHabloEspanol), gui.getGuiLeft() + gui.getXSize() / 2 - gui.getMinecraft().fontRenderer.func_238414_a_(noHabloEspanol) / 2, 25); // getStringWidth
            }

            RenderSystem.pushMatrix();
            RenderSystem.translatef(gui.getGuiLeft(), gui.getGuiTop(), 0);
            RenderHelper.enableStandardItemLighting();
            gui.func_238746_a_(event.getMatrixStack(), slotTrash); // drawSlot
            RenderHelper.disableStandardItemLighting();
            RenderSystem.popMatrix();
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof ContainerScreen<?>) {
            ContainerScreen<?> gui = (ContainerScreen<?>) event.getGui();
            DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
            if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
                return;
            }

            boolean isMouseSlot = gui.isSlotSelected(slotTrash, event.getMouseX(), event.getMouseY());
            PlayerInventory inventoryPlayer = Minecraft.getInstance().player.inventory;
            if (isMouseSlot && inventoryPlayer.getItemStack().isEmpty() && slotTrash.getHasStack()) {
                GuiHelper.renderTooltip(event.getMatrixStack(), gui, slotTrash.getStack(), event.getMouseX(), event.getMouseY());
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
