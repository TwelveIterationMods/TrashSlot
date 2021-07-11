package net.blay09.mods.trashslot.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.client.keybinds.BalmKeyMappings;
import net.blay09.mods.balm.event.client.BalmClientEvents;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.balm.mixin.MouseHandlerAccessor;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.config.TrashSlotConfig;
import net.blay09.mods.trashslot.TrashSlotSaveState;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.blay09.mods.trashslot.client.gui.GuiHelper;
import net.blay09.mods.trashslot.client.gui.TrashSlotWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class TrashSlotWidgetHandler {

    private static final TrashSlotSlot slot = new TrashSlotSlot();
    private static TrashSlotWidget widget;
    private static ContainerSettings currentContainerSettings = ContainerSettings.NONE;
    private static boolean ignoreMouseUp;

    private static boolean sentMissingMessage;
    private static long missingMessageTime;
    private static boolean isLeftMouseDown;

    public static void initialize() {
        BalmClientEvents.onScreenInitialized(TrashSlotWidgetHandler::onScreenInitialized);
        BalmClientEvents.onScreenKeyPressed(TrashSlotWidgetHandler::onScreenKeyPressed);
        BalmClientEvents.onScreenMouseClick(TrashSlotWidgetHandler::onScreenMouseClick);
        BalmClientEvents.onScreenMouseRelease(TrashSlotWidgetHandler::onScreenMouseRelease);
        BalmClientEvents.onScreenDrawn(TrashSlotWidgetHandler::onScreenDraw);
    }

    public static void onScreenInitialized(Screen screen) {
        if (screen instanceof CreativeModeInventoryScreen) {
            currentContainerSettings = ContainerSettings.NONE;
            widget = null;
            return;
        }

        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            if (!TrashSlot.isServerSideInstalled && !sentMissingMessage) {
                missingMessageTime = System.currentTimeMillis();
                sentMissingMessage = true;
                return;
            }

            // For some reason this event gets fired with GuiInventory right after opening the creative menu, AFTER it got fired for GuiContainerCreative
            if (screen instanceof InventoryScreen && Minecraft.getInstance().player.getAbilities().instabuild) {
                return;
            }

            IGuiContainerLayout layout = LayoutManager.getLayout(containerScreen);
            currentContainerSettings = TrashSlotSaveState.getSettings(containerScreen, layout);
            if (currentContainerSettings != ContainerSettings.NONE) {
                widget = new TrashSlotWidget(containerScreen, layout, currentContainerSettings, slot);
            } else {
                widget = null;
            }
        } else {
            currentContainerSettings = ContainerSettings.NONE;
            widget = null;
        }
    }

    public static boolean onScreenMouseRelease(Screen screen, double mouseX, double mouseY, int button) {
        if (button == 0) {
            isLeftMouseDown = false;
        }

        if (ignoreMouseUp) {
            ignoreMouseUp = false;
            return true;
        }

        return false;
    }

    public static boolean onScreenMouseClick(Screen screen, double mouseX, double mouseY, int button) {
        if (button == 0) {
            isLeftMouseDown = true;
        }

        DeletionProvider deletionProvider = TrashSlotConfig.getActive().getDeletionProvider();
        if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
            return false;
        }

        if (runKeyBindings(screen, InputConstants.Type.MOUSE, button, 0)) {
            return true;
        }

        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            if (((AbstractContainerScreenAccessor) containerScreen).callIsHovering(slot, mouseX, mouseY)) {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack mouseItem = containerScreen.getMenu().getCarried();
                    boolean isRightClick = button == 1;
                    if (mouseItem.isEmpty()) {
                        deletionProvider.undeleteLast(player, slot, isRightClick);
                    } else {
                        deletionProvider.deleteMouseItem(player, mouseItem, slot, isRightClick);
                    }

                    ignoreMouseUp = true;
                    return true;
                }
            } else if (widget.isInside((int) mouseX, (int) mouseY)) {
                // Prevent click-through on the background and border of the slot
                ignoreMouseUp = true;
                return true;
            }
        }

        return false;
    }

    public static boolean onScreenKeyPressed(Screen screen, int keyCode, int scanCode, int modifiers) {
        DeletionProvider deletionProvider = TrashSlotConfig.getActive().getDeletionProvider();
        if (deletionProvider == null) {
            return false;
        }

        if (runKeyBindings(screen, InputConstants.Type.KEYSYM, keyCode, scanCode)) {
            return true;
        }

        return false;
    }

    private static boolean runKeyBindings(Screen screen, InputConstants.Type type, int keyCode, int scanCode) {
        DeletionProvider deletionProvider = TrashSlotConfig.getActive().getDeletionProvider();
        if (deletionProvider == null) {
            return false;
        }

        if (currentContainerSettings.isEnabled()) {
            boolean isDelete = BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keyBindDelete, type, keyCode, scanCode);
            boolean isDeleteAll = BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keyBindDeleteAll, type, keyCode, scanCode);
            if (isDelete || isDeleteAll) {
                Player entityPlayer = Minecraft.getInstance().player;
                if (entityPlayer != null && screen instanceof AbstractContainerScreen<?> containerScreen) {
                    Slot mouseSlot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
                    if (mouseSlot != null && mouseSlot.hasItem()) {
                        deletionProvider.deleteContainerItem(containerScreen.getMenu(), mouseSlot.index, isDeleteAll, slot);
                    } else {
                        Window mainWindow = Minecraft.getInstance().getWindow();
                        MouseHandlerAccessor mouseHandler = (MouseHandlerAccessor) Minecraft.getInstance().mouseHandler;
                        double rawMouseX = mouseHandler.getMouseX();
                        double rawMouseY = mouseHandler.getMouseY();
                        double mouseX = rawMouseX * (double) mainWindow.getGuiScaledWidth() / (double) mainWindow.getWidth();
                        double mouseY = rawMouseY * (double) mainWindow.getGuiScaledHeight() / (double) mainWindow.getHeight();

                        if (((AbstractContainerScreenAccessor) containerScreen).callIsHovering(slot, mouseX, mouseY)) {
                            deletionProvider.emptyTrashSlot(slot);
                        }
                    }
                    return true;
                }
            }
        }

        if (screen instanceof AbstractContainerScreen<?> && currentContainerSettings != ContainerSettings.NONE) {
            if (BalmKeyMappings.isActiveAndMatches(ModKeyMappings.keyBindToggleSlot, type, keyCode, scanCode)) {
                currentContainerSettings.setEnabled(!currentContainerSettings.isEnabled());
                TrashSlotSaveState.save();
                return true;
            }
        }

        return false;
    }

    public static void onScreenDraw(Screen screen, PoseStack poseStack, int mouseX, int mouseY) {
        DeletionProvider deletionProvider = TrashSlotConfig.getActive().getDeletionProvider();
        if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
            return;
        }

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        int leftPos = accessor.getLeftPos();
        int topPos = accessor.getTopPos();
        if (widget != null) {
            widget.update(mouseX, mouseY);
            widget.drawBackground(poseStack);
            if (accessor.callIsHovering(slot, mouseX, mouseY)) {
                // TODO RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                int j1 = leftPos + slot.x;
                int k1 = topPos + slot.y;
                RenderSystem.colorMask(true, true, true, false);
                GuiHelper.fillGradient(poseStack, j1, k1, j1 + 16, k1 + 16, -600, -2130706433, -2130706433);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }

            if (missingMessageTime != 0 && System.currentTimeMillis() - missingMessageTime < 3000) {
                TranslatableComponent noHabloEspanol = new TranslatableComponent("trashslot.serverNotInstalled");
                noHabloEspanol.withStyle(ChatFormatting.RED);
                screen.renderTooltip(poseStack, Lists.newArrayList(noHabloEspanol), Optional.empty(), leftPos + accessor.getImageWidth() / 2 - Minecraft.getInstance().font.width(noHabloEspanol) / 2, 25);
            }

            poseStack.pushPose();
            poseStack.translate(leftPos, topPos, 0);
            // TODO RenderHelper.enableStandardItemLighting();

            // this is drawSlot, but it also does some item moving logic, so it's called moveItems now...
            accessor.callRenderSlot(poseStack, slot);

            // TODO RenderHelper.disableStandardItemLighting();
            poseStack.popPose();
        }
    }

    public static void screenDrawn(Screen screen, PoseStack poseStack, int mouseX, int mouseY) {
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            DeletionProvider deletionProvider = TrashSlotConfig.getActive().getDeletionProvider();
            if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
                return;
            }

            boolean isMouseSlot = ((AbstractContainerScreenAccessor) containerScreen).callIsHovering(slot, mouseX, mouseY);
            if (isMouseSlot && containerScreen.getMenu().getCarried().isEmpty() && slot.hasItem()) {
                List<Component> tooltip = containerScreen.getTooltipFromItem(slot.getItem());
                containerScreen.renderTooltip(poseStack, tooltip, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    public static TrashSlotWidget getWidget() {
        return widget;
    }

    public static TrashSlotSlot getSlot() {
        return slot;
    }

    public static boolean isLeftMouseDown() {
        return isLeftMouseDown;
    }
}
