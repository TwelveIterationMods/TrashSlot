package net.blay09.mods.trashslot.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.event.client.screen.*;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.balm.mixin.SlotAccessor;
import net.blay09.mods.trashslot.PlatformBindings;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.config.TrashSlotConfig;
import net.blay09.mods.trashslot.TrashSlotSaveState;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.blay09.mods.trashslot.client.gui.TrashSlotComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TrashSlotGuiHandler {

    private static final TrashSlotSlot trashSlot = new TrashSlotSlot();
    private static TrashSlotComponent trashSlotComponent;
    private static ContainerSettings currentContainerSettings = ContainerSettings.NONE;
    private static boolean ignoreMouseUp;

    private static boolean sentMissingMessage;
    private static long missingMessageTime;
    private static boolean isLeftMouseDown;

    public static void initialize() {
        Balm.getEvents().onEvent(ScreenInitEvent.Post.class, TrashSlotGuiHandler::onScreenInit);
        Balm.getEvents().onEvent(ScreenMouseEvent.Release.Pre.class, TrashSlotGuiHandler::onMouseRelease);
        Balm.getEvents().onEvent(ScreenMouseEvent.Click.Pre.class, TrashSlotGuiHandler::onMouseClick);
        Balm.getEvents().onEvent(ScreenKeyEvent.Press.Post.class, TrashSlotGuiHandler::onKeyPress);
        Balm.getEvents().onEvent(ContainerScreenDrawEvent.Background.class, TrashSlotGuiHandler::onBackgroundDrawn);
        Balm.getEvents().onEvent(ContainerScreenDrawEvent.Foreground.class, TrashSlotGuiHandler::onScreenDrawn);
    }

    private static void onScreenInit(ScreenInitEvent.Post event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen) {
            currentContainerSettings = ContainerSettings.NONE;
            trashSlotComponent = null;
            return;
        }

        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
            if (!TrashSlot.isServerSideInstalled && !sentMissingMessage) {
                TrashSlot.logger.info("TrashSlot is not installed on the server and thus will be unavailable.");
                missingMessageTime = System.currentTimeMillis();
                sentMissingMessage = true;
                return;
            }

            // For some reason this event gets fired with GuiInventory right after opening the creative menu, AFTER it got fired for GuiContainerCreative
            if (screen instanceof InventoryScreen && Minecraft.getInstance().player.getAbilities().instabuild) {
                return;
            }

            IGuiContainerLayout layout = LayoutManager.getLayout(screen);
            currentContainerSettings = TrashSlotSaveState.getSettings(screen, layout);
            if (currentContainerSettings != ContainerSettings.NONE) {
                trashSlotComponent = new TrashSlotComponent(screen, layout, currentContainerSettings, trashSlot);
            } else {
                trashSlotComponent = null;
            }
        } else {
            currentContainerSettings = ContainerSettings.NONE;
            trashSlotComponent = null;
        }
    }

    private static void onMouseRelease(ScreenMouseEvent.Release.Pre event) {
        if (event.getButton() == 0) {
            isLeftMouseDown = false;
        }

        if (ignoreMouseUp) {
            event.setCanceled(true);
            ignoreMouseUp = false;
        }
    }

    private static void onMouseClick(ScreenMouseEvent.Click.Pre event) {
        if (event.getButton() == 0) {
            isLeftMouseDown = true;
        }

        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
            return;
        }

        int mouseButton = event.getButton();
        if (runKeyBindings(event.getScreen(), InputConstants.Type.MOUSE, mouseButton, 0)) {
            event.setCanceled(true);
            return;
        }

        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
            double mouseX = event.getMouseX();
            double mouseY = event.getMouseY();
            if (((AbstractContainerScreenAccessor) screen).callIsHovering(trashSlot, mouseX, mouseY)) {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack mouseItem = screen.getMenu().getCarried();
                    boolean isRightClick = mouseButton == 1;
                    if (mouseItem.isEmpty()) {
                        deletionProvider.undeleteLast(player, trashSlot, isRightClick);
                    } else {
                        deletionProvider.deleteMouseItem(player, mouseItem, trashSlot, isRightClick);
                    }

                    event.setCanceled(true);
                    ignoreMouseUp = true;
                }
            } else if (trashSlotComponent.isInside((int) mouseX, (int) mouseY)) {
                // Prevent click-through on the background and border of the slot
                event.setCanceled(true);
                ignoreMouseUp = true;
            }
        }
    }

    private static void onKeyPress(ScreenKeyEvent.Press.Post event) {
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null) {
            return;
        }

        int keyCode = event.getKey();
        int scanCode = event.getScanCode();
        InputConstants.Key input = InputConstants.getKey(keyCode, scanCode);
        if (runKeyBindings(event.getScreen(), input.getType(), keyCode, scanCode)) {
            event.setCanceled(true);
        }
    }

    private static boolean runKeyBindings(Screen screen, InputConstants.Type type, int keyCode, int scanCode) {
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null) {
            return false;
        }

        if (currentContainerSettings.isEnabled()) {
            boolean isDelete = BalmClient.getKeyMappings().isActiveAndMatches(ModKeyMappings.keyBindDelete, type, keyCode, scanCode);
            boolean isDeleteAll = BalmClient.getKeyMappings().isActiveAndMatches(ModKeyMappings.keyBindDeleteAll, type, keyCode, scanCode);

            // For Fabric: if both delete and delete all match, and we don't support key modifiers (as in Fabric), specifically require Shift for isDeleteAll
            if(isDelete && isDeleteAll && !PlatformBindings.INSTANCE.supportsKeyModifiers()) {
                isDelete = !Screen.hasShiftDown();
                isDeleteAll = Screen.hasShiftDown();
            }

            if (isDelete || isDeleteAll) {
                Player entityPlayer = Minecraft.getInstance().player;
                if (entityPlayer != null && screen instanceof AbstractContainerScreen<?> containerScreen) {
                    Slot mouseSlot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
                    if (mouseSlot != null && mouseSlot.hasItem()) {
                        deletionProvider.deleteContainerItem(containerScreen.getMenu(), mouseSlot.index, isDeleteAll, trashSlot);
                    } else {
                        Window mainWindow = Minecraft.getInstance().getWindow();
                        double rawMouseX = Minecraft.getInstance().mouseHandler.xpos();
                        double rawMouseY = Minecraft.getInstance().mouseHandler.ypos();
                        double mouseX = rawMouseX * (double) mainWindow.getGuiScaledWidth() / (double) mainWindow.getWidth();
                        double mouseY = rawMouseY * (double) mainWindow.getGuiScaledHeight() / (double) mainWindow.getHeight();

                        if (((AbstractContainerScreenAccessor) containerScreen).callIsHovering(trashSlot, mouseX, mouseY)) {
                            deletionProvider.emptyTrashSlot(trashSlot);
                        }
                    }
                    return true;
                }
            }
        }

        if (screen instanceof AbstractContainerScreen<?> && currentContainerSettings != ContainerSettings.NONE) {
            if (BalmClient.getKeyMappings().isActiveAndMatches(ModKeyMappings.keyBindToggleSlot, type, keyCode, scanCode)) {
                currentContainerSettings.setEnabled(!currentContainerSettings.isEnabled());
                TrashSlotSaveState.save();
                return true;
            }
        }

        return false;
    }

    public static void onBackgroundDrawn(ContainerScreenDrawEvent.Background event) {
        PoseStack poseStack = event.getPoseStack();
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
            return;
        }

        if (event.getScreen() instanceof AbstractContainerScreen screen && trashSlotComponent != null) {
            trashSlotComponent.update(event.getMouseX(), event.getMouseY());
            trashSlotComponent.drawBackground(poseStack);

            if (((AbstractContainerScreenAccessor) screen).callIsHovering(trashSlot, event.getMouseX(), event.getMouseY())) {
                poseStack.pushPose();
                poseStack.translate(((AbstractContainerScreenAccessor) screen).getLeftPos(), ((AbstractContainerScreenAccessor) screen).getTopPos(), 0);
                AbstractContainerScreen.renderSlotHighlight(poseStack, trashSlot.x, trashSlot.y, screen.getBlitOffset());
                poseStack.popPose();
            }
        }
    }

    public static void onScreenDrawn(ContainerScreenDrawEvent.Foreground event) {
        PoseStack poseStack = event.getPoseStack();
        if (missingMessageTime != 0 && System.currentTimeMillis() - missingMessageTime < 3000 && event.getScreen() instanceof AbstractContainerScreen<?> screen) {
            TranslatableComponent noHabloEspanol = new TranslatableComponent("trashslot.serverNotInstalled");
            noHabloEspanol.withStyle(ChatFormatting.RED);
            screen.renderComponentTooltip(poseStack, Lists.newArrayList(noHabloEspanol), ((AbstractContainerScreenAccessor) screen).getLeftPos() + ((AbstractContainerScreenAccessor) screen).getImageWidth() / 2 - Minecraft.getInstance().font.width(noHabloEspanol) / 2, 25);
        }

        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
            return;
        }

        if (event.getScreen() instanceof AbstractContainerScreen screen && trashSlotComponent != null) {
            // TODO bit ugly for now since renderSlot ignores the pose stack translation
            TrashSlotSlot trashSlot = TrashSlotGuiHandler.trashSlot;
            SlotAccessor slotAccessor = (SlotAccessor) trashSlot;
            slotAccessor.setX(trashSlot.x + ((AbstractContainerScreenAccessor) screen).getLeftPos());
            slotAccessor.setY(trashSlot.y + ((AbstractContainerScreenAccessor) screen).getTopPos());
            ((AbstractContainerScreenAccessor) screen).callRenderSlot(poseStack, trashSlot);
            slotAccessor.setX(trashSlot.x - ((AbstractContainerScreenAccessor) screen).getLeftPos());
            slotAccessor.setY(trashSlot.y - ((AbstractContainerScreenAccessor) screen).getTopPos());

            boolean isMouseSlot = ((AbstractContainerScreenAccessor) screen).callIsHovering(trashSlot, event.getMouseX(), event.getMouseY());
            if (isMouseSlot && screen.getMenu().getCarried().isEmpty() && trashSlot.hasItem()) {
                screen.renderComponentTooltip(poseStack, screen.getTooltipFromItem(trashSlot.getItem()), event.getMouseX(), event.getMouseY());
            }
        }
    }

    public static TrashSlotComponent getTrashSlotComponent() {
        return trashSlotComponent;
    }

    public static TrashSlotSlot getTrashSlot() {
        return trashSlot;
    }

    public static boolean isLeftMouseDown() {
        return isLeftMouseDown;
    }
}
