package net.blay09.mods.trashslot.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.client.screen.ScreenDrawEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenInitEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenKeyEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenMouseEvent;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.TrashSlotConfig;
import net.blay09.mods.trashslot.TrashSlotSaveState;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.blay09.mods.trashslot.client.gui.GuiHelper;
import net.blay09.mods.trashslot.client.gui.GuiTrashSlot;
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

    private static final SlotTrash slotTrash = new SlotTrash();
    private static GuiTrashSlot guiTrashSlot;
    private static ContainerSettings currentContainerSettings = ContainerSettings.NONE;
    private static boolean ignoreMouseUp;

    private static boolean sentMissingMessage;
    private static long missingMessageTime;
    private static boolean isLeftMouseDown;

    public static void initialize() {
        Balm.getEvents().onEvent(ScreenInitEvent.Post.class, TrashSlotGuiHandler::onInitGui);
        Balm.getEvents().onEvent(ScreenMouseEvent.Release.Pre.class, TrashSlotGuiHandler::onGuiMouseReleased);
        Balm.getEvents().onEvent(ScreenMouseEvent.Click.Pre.class, TrashSlotGuiHandler::onGuiMouseClicked);
        Balm.getEvents().onEvent(ScreenKeyEvent.Press.Post.class, TrashSlotGuiHandler::onGuiKeyboard);
        Balm.getEvents().onEvent(ScreenDrawEvent.Pre.class, TrashSlotGuiHandler::onDrawBackground, EventPriority.Lowest);
        Balm.getEvents().onEvent(ScreenDrawEvent.Post.class, TrashSlotGuiHandler::onDrawScreen);
    }

    private static void onInitGui(ScreenInitEvent.Post event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen) {
            currentContainerSettings = ContainerSettings.NONE;
            guiTrashSlot = null;
            return;
        }

        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
            if (!TrashSlot.isServerSideInstalled && !sentMissingMessage) {
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
                guiTrashSlot = new GuiTrashSlot(screen, layout, currentContainerSettings, slotTrash);
            } else {
                guiTrashSlot = null;
            }
        } else {
            currentContainerSettings = ContainerSettings.NONE;
            guiTrashSlot = null;
        }
    }

    private static void onGuiMouseReleased(ScreenMouseEvent.Release.Pre event) {
        if (event.getButton() == 0) {
            isLeftMouseDown = false;
        }

        if (ignoreMouseUp) {
            event.setCanceled(true);
            ignoreMouseUp = false;
        }
    }

    private static void onGuiMouseClicked(ScreenMouseEvent.Click.Pre event) {
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
            if (((AbstractContainerScreenAccessor) screen).callIsHovering(slotTrash, mouseX, mouseY)) {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack mouseItem = screen.getMenu().getCarried();
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

    private static void onGuiKeyboard(ScreenKeyEvent.Press.Post event) {
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
            if (isDelete || isDeleteAll) {
                Player entityPlayer = Minecraft.getInstance().player;
                if (entityPlayer != null && screen instanceof AbstractContainerScreen<?> gui) {
                    Slot mouseSlot = gui.getSlotUnderMouse();
                    if (mouseSlot != null && mouseSlot.hasItem()) {
                        deletionProvider.deleteContainerItem(gui.getMenu(), mouseSlot.index, isDeleteAll, slotTrash);
                    } else {
                        Window mainWindow = Minecraft.getInstance().getWindow();
                        double rawMouseX = Minecraft.getInstance().mouseHandler.xpos();
                        double rawMouseY = Minecraft.getInstance().mouseHandler.ypos();
                        double mouseX = rawMouseX * (double) mainWindow.getGuiScaledWidth() / (double) mainWindow.getWidth();
                        double mouseY = rawMouseY * (double) mainWindow.getGuiScaledHeight() / (double) mainWindow.getHeight();

                        if (((AbstractContainerScreenAccessor) gui).callIsHovering(slotTrash, mouseX, mouseY)) {
                            deletionProvider.emptyTrashSlot(slotTrash);
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

    public static void onDrawBackground(ScreenDrawEvent.Pre event) {
        PoseStack poseStack = event.getPoseStack();
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
            return;
        }

        AbstractContainerScreen<?> gui = (AbstractContainerScreen<?>) event.getScreen();
        if (guiTrashSlot != null) {
            guiTrashSlot.update(event.getMouseX(), event.getMouseY());
            guiTrashSlot.drawBackground(poseStack);
            if (((AbstractContainerScreenAccessor) gui).callIsHovering(slotTrash, event.getMouseX(), event.getMouseY())) {
                RenderSystem.disableDepthTest();
                int j1 = gui.getGuiLeft() + slotTrash.x;
                int k1 = gui.getGuiTop() + slotTrash.y;
                RenderSystem.colorMask(true, true, true, false);
                GuiHelper.drawGradientRect(poseStack, j1, k1, j1 + 16, k1 + 16, -600, -2130706433, -2130706433);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }

            if (missingMessageTime != 0 && System.currentTimeMillis() - missingMessageTime < 3000) {
                TranslatableComponent noHabloEspanol = new TranslatableComponent("trashslot.serverNotInstalled");
                noHabloEspanol.withStyle(ChatFormatting.RED);
                gui.renderComponentTooltip(poseStack, Lists.newArrayList(noHabloEspanol), gui.getGuiLeft() + gui.getXSize() / 2 - gui.getMinecraft().font.width(noHabloEspanol) / 2, 25);
            }

            poseStack.pushPose();
            poseStack.translate(gui.getGuiLeft(), gui.getGuiTop(), 0);

            // this is drawSlot, but it also does some item moving logic, so it's called moveItems now...
            ((AbstractContainerScreenAccessor) gui).callRenderSlot(poseStack, slotTrash);

            poseStack.popPose();
        }
    }

    private static void onDrawScreen(ScreenDrawEvent.Post event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
            DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
            if (deletionProvider == null || !currentContainerSettings.isEnabled()) {
                return;
            }

            boolean isMouseSlot = ((AbstractContainerScreenAccessor) screen).callIsHovering(slotTrash, event.getMouseX(), event.getMouseY());
            if (isMouseSlot && screen.getMenu().getCarried().isEmpty() && slotTrash.hasItem()) {
                screen.renderComponentTooltip(event.getPoseStack(), screen.getTooltipFromItem(slotTrash.getItem()), event.getMouseX(), event.getMouseY());
            }
        }
    }

    public static GuiTrashSlot getGuiTrashSlot() {
        return guiTrashSlot;
    }

    public static SlotTrash getTrashSlot() {
        return slotTrash;
    }

    public static boolean isLeftMouseDown() {
        return isLeftMouseDown;
    }
}
