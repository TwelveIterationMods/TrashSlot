package net.blay09.mods.trashslot.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.blay09.mods.balm.client.platform.event.callback.ScreenCallback;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.trashslot.*;
import net.blay09.mods.trashslot.api.layout.TrashSlotAvailability;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.blay09.mods.trashslot.client.gui.TrashSlotComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class TrashSlotGuiHandler {

    private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
    private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");

    private static final TrashSlotSlot trashSlot = new TrashSlotSlot();
    private static @Nullable TrashSlotComponent trashSlotComponent;
    private static @Nullable ContainerSettings currentContainerSettings;
    private static boolean ignoreMouseUp;

    private static boolean sentMissingMessage;
    private static boolean isLeftMouseDown;

    private static @Nullable Hint currentHint;

    public static void initialize() {
        ScreenCallback.Init.After.EVENT.register(TrashSlotGuiHandler::onScreenInit);
        ScreenCallback.MouseRelease.Before.EVENT.register(TrashSlotGuiHandler::onMouseRelease);
        ScreenCallback.MousePress.Before.EVENT.register(TrashSlotGuiHandler::onMouseClick);
        ScreenCallback.KeyPress.After.EVENT.register(TrashSlotGuiHandler::onKeyPress);
        ScreenCallback.Render.AFTER_BACKGROUND.register(TrashSlotGuiHandler::onBackgroundDrawn);
    }

    private static void onScreenInit(Screen screen) {
        // Ignore screens from ReplayMod because they wrap every screen with their own class for some reason
        if (screen.getClass().getName().startsWith("com.replaymod")) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player != null && player.isSpectator()) {
            return;
        }

        if (screen instanceof CreativeModeInventoryScreen) {
            currentContainerSettings = null;
            trashSlotComponent = null;
            return;
        }

        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            if (!TrashSlot.isServerSideInstalled && !sentMissingMessage) {
                TrashSlot.logger.info("TrashSlot is not installed on the server and thus will be unavailable.");
                MutableComponent noHabloEspanol = Component.translatable("trashslot.serverNotInstalled");
                noHabloEspanol.withStyle(ChatFormatting.RED);
                showHint(Hints.SERVER_NOT_INSTALLED, noHabloEspanol, 5000, true);
                sentMissingMessage = true;
                return;
            }

            // For some reason this event gets fired with GuiInventory right after opening the creative menu, AFTER it got fired for GuiContainerCreative
            if (containerScreen instanceof InventoryScreen && player != null && player.getAbilities().instabuild) {
                return;
            }

            final var layout = TrashContainerLayoutManager.getLayout(containerScreen);
            final var context = layout.createContext(containerScreen);
            final var settings = TrashSlotSaveState.getSettings(context);
            if (layout.getAvailability() != TrashSlotAvailability.NEVER) {
                currentContainerSettings = settings;
                trashSlotComponent = new TrashSlotComponent(containerScreen, layout, settings, trashSlot);

                if (!settings.isEnabled() && !layout.isEnabledByDefault() && !ModKeyMappings.keyBindToggleSlot.getBinding()
                        .key()
                        .equals(InputConstants.UNKNOWN)) {
                    var hintMessage = Component.translatable("trashslot.hint.toggleOn", ModKeyMappings.keyBindToggleSlot.getBinding().key().getDisplayName());
                    showHint(Hints.TOGGLE_ON, hintMessage, 5000);
                }
            } else {
                currentContainerSettings = null;
                trashSlotComponent = null;
            }
        } else {
            currentContainerSettings = null;
            trashSlotComponent = null;
        }
    }

    private static boolean onMouseRelease(Screen screen, double mouseX, double mouseY, int button) {
        if (button == 0) {
            isLeftMouseDown = false;
        }

        if (ignoreMouseUp) {
            ignoreMouseUp = false;
            return true;
        }

        return false;
    }

    private static boolean onMouseClick(Screen screen, MouseButtonEvent event) {
        if (event.button() == 0) {
            isLeftMouseDown = true;
        }

        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null || currentContainerSettings == null || !currentContainerSettings.isEnabled()) {
            return false;
        }

        if (runKeyBindings(screen, event)) {
            return true;
        }

        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            if (((AbstractContainerScreenAccessor) containerScreen).callIsHovering(trashSlot, event.x(), event.y())) {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack mouseItem = containerScreen.getMenu().getCarried();
                    boolean isRightClick = event.button() == InputConstants.MOUSE_BUTTON_RIGHT;
                    if (mouseItem.isEmpty()) {
                        deletionProvider.undeleteLast(player, trashSlot, isRightClick);
                    } else {
                        if (TrashHelper.canDelete(mouseItem)) {
                            deletionProvider.deleteMouseItem(player, mouseItem, trashSlot, isRightClick);
                        } else {
                            var hintMessage = Component.translatable("trashslot.hint.deletionDenied");
                            hintMessage.withStyle(ChatFormatting.RED);
                            showHint(Hints.DELETION_DENIED, hintMessage, 1000, true);
                        }
                    }

                    ignoreMouseUp = true;
                    return true;
                }
            } else if (trashSlotComponent != null && trashSlotComponent.isInside((int) event.x(), (int) event.y())) {
                // Prevent click-through on the background and border of the slot
                ignoreMouseUp = true;
                return true;
            }
        }
        return false;
    }

    private static void onKeyPress(Screen screen, KeyEvent event) {
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider != null) {
            runKeyBindings(screen, event);
        }
    }

    private static boolean runKeyBindings(Screen screen, InputWithModifiers input) {
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null) {
            return false;
        }

        boolean isDelete = ModKeyMappings.keyBindDelete.isActiveAndMatchesInput(input);
        boolean isDeleteAll = ModKeyMappings.keyBindDeleteAll.isActiveAndMatchesInput(input);

        final var player = Minecraft.getInstance().player;

        // Special handling for creative inventory. We don't have a TrashSlot here, but we still allow deleting via DELETE key
        if ((isDelete || isDeleteAll) && TrashSlotConfig.getActive().enableDeleteKeysInCreative && screen instanceof CreativeModeInventoryScreen containerScreen && player != null) {
            Slot mouseSlot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
            DeletionProvider creativeDeletionProvider = TrashSlotConfig.getCreativeDeletionProvider();
            if (mouseSlot != null && mouseSlot.getClass() == Slot.class && mouseSlot.container == player.getInventory()) {
                creativeDeletionProvider.deleteContainerItem(player, containerScreen.getMenu(), mouseSlot.index - 9, isDeleteAll, trashSlot);
            } else if (mouseSlot != null && mouseSlot.getClass().getSimpleName().equals("SlotWrapper")) {
                creativeDeletionProvider.deleteContainerItem(player, containerScreen.getMenu(), mouseSlot.getContainerSlot(), isDeleteAll, trashSlot);
            }
        }

        // For all other screens, respect the normal settings
        if (((currentContainerSettings != null && currentContainerSettings.isEnabled()) || TrashSlotConfig.getActive().allowDeletionWhileTrashSlotIsInvisible) && (isDelete || isDeleteAll)) {
            if (player != null && screen instanceof AbstractContainerScreen<?> containerScreen) {
                Slot mouseSlot = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
                if (mouseSlot != null && mouseSlot.hasItem()) {
                    if (TrashHelper.canDelete(mouseSlot.getItem())) {
                        deletionProvider.deleteContainerItem(player, containerScreen.getMenu(), mouseSlot.index, isDeleteAll, trashSlot);
                        if (!currentContainerSettings.isEnabled()) {
                            var hintMessage = Component.translatable("trashslot.hint.deletedWhileHidden");
                            hintMessage.withStyle(ChatFormatting.GOLD);
                            showHint(Hints.DELETED_WHILE_HIDDEN, hintMessage, 800, true);
                        }
                    } else {
                        var hintMessage = Component.translatable("trashslot.hint.deletionDenied");
                        hintMessage.withStyle(ChatFormatting.RED);
                        showHint(Hints.DELETION_DENIED, hintMessage, 1000, true);
                    }
                } else {
                    Window mainWindow = Minecraft.getInstance().getWindow();
                    double rawMouseX = Minecraft.getInstance().mouseHandler.xpos();
                    double rawMouseY = Minecraft.getInstance().mouseHandler.ypos();
                    double mouseX = rawMouseX * (double) mainWindow.getGuiScaledWidth() / (double) mainWindow.getWidth();
                    double mouseY = rawMouseY * (double) mainWindow.getGuiScaledHeight() / (double) mainWindow.getHeight();

                    if (((AbstractContainerScreenAccessor) containerScreen).callIsHovering(trashSlot, mouseX, mouseY)) {
                        deletionProvider.emptyTrashSlot(player, trashSlot);
                    }
                }
                return true;
            }
        }

        // Toggling of trashslot
        if (screen instanceof AbstractContainerScreen<?> && currentContainerSettings != null) {
            if (ModKeyMappings.keyBindToggleSlot.isActiveAndMatchesInput(input)) {
                currentContainerSettings.setEnabled(!currentContainerSettings.isEnabled());
                if (!currentContainerSettings.isEnabled() && !ModKeyMappings.keyBindToggleSlot.getBinding().key().equals(InputConstants.UNKNOWN)) {
                    var hintMessage = Component.translatable("trashslot.hint.toggledOff", ModKeyMappings.keyBindToggleSlot.getBinding().key().getDisplayName());
                    showHint(Hints.TOGGLED_OFF, hintMessage, 5000);
                }
                TrashSlotSaveState.save();
                return true;
            } else if (ModKeyMappings.keyBindToggleSlotLock.isActiveAndMatchesInput(input)) {
                currentContainerSettings.setLocked(!currentContainerSettings.isLocked());
                if (currentContainerSettings.isLocked()) {
                    var hintMessage = Component.translatable("trashslot.hint.locked", ModKeyMappings.keyBindToggleSlotLock.getBinding().key().getDisplayName());
                    hintMessage.withStyle(ChatFormatting.GOLD);
                    showHint(Hints.LOCKED, hintMessage, 5000, true);
                } else {
                    var hintMessage = Component.translatable("trashslot.hint.unlocked",
                            ModKeyMappings.keyBindToggleSlotLock.getBinding().key().getDisplayName());
                    hintMessage.withStyle(ChatFormatting.GOLD);
                    showHint(Hints.UNLOCKED, hintMessage, 5000, true);
                }
                TrashSlotSaveState.save();
                return true;
            }
        }

        return false;
    }

    private static void showHint(String id, MutableComponent message, int timeToDisplay) {
        showHint(id, message, timeToDisplay, false);
    }

    private static void showHint(String id, MutableComponent message, int timeToDisplay, boolean force) {
        var saveState = TrashSlotSaveState.getOrLoad();
        if (force || (!saveState.hasSeenHint(id) && TrashSlotConfig.getActive().enableHints)) {
            currentHint = new Hint(id, message, timeToDisplay);
        }
    }

    public static void onBackgroundDrawn(Screen screen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        if (deletionProvider == null || currentContainerSettings == null || !currentContainerSettings.isEnabled()) {
            return;
        }

        if (screen instanceof AbstractContainerScreen<?> containerScreen && trashSlotComponent != null) {
            trashSlotComponent.update(mouseX, mouseY);
            trashSlotComponent.drawBackground(guiGraphics);

            final var poseStack = guiGraphics.pose();
            final var screenAccessor = (AbstractContainerScreenAccessor) containerScreen;
            final var hovering = screenAccessor.callIsHovering(trashSlot, mouseX, mouseY);
            if (hovering) {
                poseStack.pushMatrix();
                poseStack.translate(screenAccessor.getLeftPos(), screenAccessor.getTopPos());
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, trashSlot.x - 4, trashSlot.y - 4, 24, 24);
                poseStack.popMatrix();
            }

            poseStack.pushMatrix();
            poseStack.translate(screenAccessor.getLeftPos(), screenAccessor.getTopPos());
            TrashSlotSlot trashSlot = TrashSlotGuiHandler.trashSlot;
            screenAccessor.callExtractSlot(guiGraphics, trashSlot, mouseX, mouseY);
            poseStack.popMatrix();

            if (hovering) {
                poseStack.pushMatrix();
                poseStack.translate(screenAccessor.getLeftPos(), screenAccessor.getTopPos());
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, trashSlot.x - 4, trashSlot.y - 4, 24, 24);
                poseStack.popMatrix();
            }

            boolean isMouseSlot = screenAccessor.callIsHovering(trashSlot, mouseX, mouseY);
            if (isMouseSlot) {
                if (containerScreen.getMenu().getCarried().isEmpty() && trashSlot.hasItem()) {
                    guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, trashSlot.getItem(), mouseX, mouseY);
                } else if (!trashSlotComponent.isDragging()) {
                    if (TrashSlotConfig.getActive().instantDeletion) {
                        guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font,
                                Component.translatable("tooltip.trashslot.destroy_item"),
                                mouseX,
                                mouseY);
                    } else {
                        guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font,
                                Component.translatable("tooltip.trashslot.trash_item"),
                                mouseX,
                                mouseY);
                    }
                }
            }
        }

        if (currentHint != null) {
            currentHint.render(screen, guiGraphics);
            if (currentHint.isComplete()) {
                TrashSlotSaveState.getOrLoad().markHintAsSeen(currentHint.getId());
                TrashSlotSaveState.save();
                currentHint = null;
            }
        }
    }

    @Nullable
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
