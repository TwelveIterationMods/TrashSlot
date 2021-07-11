package net.blay09.mods.trashslot.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.balm.mixin.SlotAccessor;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.config.TrashSlotConfig;
import net.blay09.mods.trashslot.TrashSlotSaveState;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.api.SlotRenderStyle;
import net.blay09.mods.trashslot.api.Snap;
import net.blay09.mods.trashslot.client.ContainerSettings;
import net.blay09.mods.trashslot.client.TrashSlotSlot;
import net.blay09.mods.trashslot.client.TrashSlotWidgetHandler;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.awt.*;

public class TrashSlotWidget extends GuiComponent {

    private static final ResourceLocation texture = new ResourceLocation(TrashSlot.MOD_ID, "textures/gui/slot.png");
    private static final int SNAP_SIZE = 7;

    private final AbstractContainerScreen<?> screen;
    private final IGuiContainerLayout layout;
    private final ContainerSettings settings;
    private final TrashSlotSlot trashSlot;

    private SlotRenderStyle renderStyle = SlotRenderStyle.LONE;

    private boolean wasMouseDown;
    private boolean isDragging;
    private int dragStartX;
    private int dragStartY;

    public TrashSlotWidget(AbstractContainerScreen<?> screen, IGuiContainerLayout layout, ContainerSettings settings, TrashSlotSlot trashSlot) {
        this.screen = screen;
        this.layout = layout;
        this.settings = settings;
        this.trashSlot = trashSlot;
    }

    public boolean isInside(int mouseX, int mouseY) {
        int anchoredX = getAnchoredX();
        int anchoredY = getAnchoredY();
        int renderX = anchoredX + renderStyle.getRenderOffsetX() + layout.getSlotOffsetX(screen, renderStyle);
        int renderY = anchoredY + renderStyle.getRenderOffsetY() + layout.getSlotOffsetY(screen, renderStyle);
        return mouseX >= renderX && mouseY >= renderY && mouseX < renderX + renderStyle.getRenderWidth() && mouseY < renderY + renderStyle.getRenderHeight();
    }

    public void update(int mouseX, int mouseY) {
        int anchoredX = getAnchoredX();
        int anchoredY = getAnchoredY();
        int renderX = anchoredX + renderStyle.getRenderOffsetX() + layout.getSlotOffsetX(screen, renderStyle);
        int renderY = anchoredY + renderStyle.getRenderOffsetY() + layout.getSlotOffsetY(screen, renderStyle);
        boolean isMouseOver = mouseX >= renderX && mouseY >= renderY && mouseX < renderX + renderStyle.getRenderWidth() && mouseY < renderY + renderStyle.getRenderHeight();
        if (TrashSlotWidgetHandler.isLeftMouseDown()) {
            if (!isDragging && isMouseOver && !wasMouseDown) {
                if (screen.getMenu().getCarried().isEmpty() && (!trashSlot.hasItem() || !((AbstractContainerScreenAccessor) screen).callIsHovering(trashSlot, mouseX, mouseY))) {
                    dragStartX = renderX - mouseX;
                    dragStartY = renderY - mouseY;
                    isDragging = true;
                }
            }
            wasMouseDown = true;
        } else {
            if (isDragging) {
                TrashSlotSaveState.save();
                isDragging = false;
            }
            wasMouseDown = false;
        }
        if (isDragging) {
            int targetX = mouseX + dragStartX;
            int targetY = mouseY + dragStartY;
            for (Rectangle collisionArea : layout.getCollisionAreas(screen)) {
                int targetRight = targetX + renderStyle.getWidth();
                int targetBottom = targetY + renderStyle.getHeight();
                int rectRight = collisionArea.x + collisionArea.width;
                int rectBottom = collisionArea.y + collisionArea.height;
                if (targetRight >= collisionArea.x && targetX < rectRight && targetBottom >= collisionArea.y && targetY < rectBottom) {
                    int distLeft = targetRight - collisionArea.x;
                    int distRight = rectRight - targetX;
                    int distTop = targetBottom - collisionArea.y;
                    int distBottom = rectBottom - targetY;
                    if (anchoredX >= collisionArea.x && anchoredX < collisionArea.x + collisionArea.width) {
                        targetY = distTop < distBottom ? collisionArea.y - renderStyle.getHeight() : collisionArea.y + collisionArea.height;
                    } else {
                        targetX = distLeft < distRight ? collisionArea.x - renderStyle.getWidth() : collisionArea.x + collisionArea.width;
                    }
                }
            }

            if (!Screen.hasShiftDown()) {
                int bestSnapDist = Integer.MAX_VALUE;
                Snap bestSnap = null;
                for (Snap snap : layout.getSnaps(screen, renderStyle)) {
                    int dist = Integer.MAX_VALUE;
                    switch (snap.getType()) {
                        case HORIZONTAL:
                            dist = Math.abs(snap.getY() - targetY);
                            break;
                        case VERTICAL:
                            dist = Math.abs(snap.getX() - targetX);
                            break;
                        case FIXED:
                            int distX = snap.getX() - targetX;
                            int distY = snap.getY() - targetY;
                            dist = (int) Math.sqrt(distX * distX + distY * distY);
                            break;
                    }
                    if (dist < SNAP_SIZE && dist < bestSnapDist) {
                        bestSnap = snap;
                        bestSnapDist = dist;
                    }
                }
                if (bestSnap != null) {
                    if (bestSnap.getType() == Snap.Type.VERTICAL || bestSnap.getType() == Snap.Type.FIXED) {
                        targetX = bestSnap.getX();
                    }
                    if (bestSnap.getType() == Snap.Type.HORIZONTAL || bestSnap.getType() == Snap.Type.FIXED) {
                        targetY = bestSnap.getY();
                    }
                }
            }
            targetX = Mth.clamp(targetX, 0, screen.width - renderStyle.getRenderWidth());
            targetY = Mth.clamp(targetY, 0, screen.height - renderStyle.getRenderHeight());
            settings.setSlotX(getUnanchoredX(targetX));
            settings.setSlotY(getUnanchoredY(targetY));
        }
    }

    public void drawBackground(PoseStack matrixStack) {
        int renderX = getAnchoredX();
        int renderY = getAnchoredY();
        renderStyle = layout.getSlotRenderStyle(screen, renderX, renderY);
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        ((SlotAccessor) trashSlot).setX(renderX - accessor.getLeftPos() + renderStyle.getSlotOffsetX() + layout.getSlotOffsetX(screen, renderStyle));
        ((SlotAccessor) trashSlot).setY(renderY - accessor.getTopPos() + renderStyle.getSlotOffsetY() + layout.getSlotOffsetY(screen, renderStyle));
        setBlitOffset(1);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, texture);
        renderX += renderStyle.getRenderOffsetX() + layout.getSlotOffsetX(screen, renderStyle);
        renderY += renderStyle.getRenderOffsetY() + layout.getSlotOffsetY(screen, renderStyle);
        DeletionProvider deletionProvider = TrashSlotConfig.getActive().getDeletionProvider();
        int texOffsetX = 0;
        if (deletionProvider == null || !deletionProvider.canUndeleteLast()) {
            texOffsetX = 64;
        }
        switch (renderStyle) {
            case LONE:
                blit(matrixStack, renderX, renderY, texOffsetX, 56, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                break;
            case ATTACH_BOTTOM_CENTER:
                blit(matrixStack, renderX, renderY, texOffsetX, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX, renderY, texOffsetX + 50, 29, 4, 4);
                blit(matrixStack, renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 54, 29, 4, 4);
                break;
            case ATTACH_BOTTOM_LEFT:
                blit(matrixStack, renderX, renderY, texOffsetX, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 54, 29, 4, 4);
                break;
            case ATTACH_BOTTOM_RIGHT:
                blit(matrixStack, renderX, renderY, texOffsetX, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX, renderY, texOffsetX + 50, 29, 4, 4);
                break;
            case ATTACH_TOP_CENTER:
                blit(matrixStack, renderX, renderY, texOffsetX + 32, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 25, 4, 4);
                blit(matrixStack, renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 25, 4, 4);
                break;
            case ATTACH_TOP_LEFT:
                blit(matrixStack, renderX, renderY, texOffsetX + 32, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 25, 4, 4);
                break;
            case ATTACH_TOP_RIGHT:
                blit(matrixStack, renderX, renderY, texOffsetX + 32, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 25, 4, 4);
                break;
            case ATTACH_LEFT_CENTER:
                blit(matrixStack, renderX, renderY, texOffsetX + 25, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 50, 33, 4, 4);
                blit(matrixStack, renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 37, 4, 4);
                break;
            case ATTACH_LEFT_TOP:
                blit(matrixStack, renderX, renderY, texOffsetX + 25, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 37, 4, 4);
                break;
            case ATTACH_LEFT_BOTTOM:
                blit(matrixStack, renderX, renderY, texOffsetX + 25, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 50, 33, 4, 4);
                break;
            case ATTACH_RIGHT_CENTER:
                blit(matrixStack, renderX, renderY, texOffsetX, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX, renderY, texOffsetX + 54, 33, 4, 4);
                blit(matrixStack, renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 37, 4, 4);
                break;
            case ATTACH_RIGHT_TOP:
                blit(matrixStack, renderX, renderY, texOffsetX, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 37, 4, 4);
                break;
            case ATTACH_RIGHT_BOTTOM:
                blit(matrixStack, renderX, renderY, texOffsetX, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(matrixStack, renderX, renderY, texOffsetX + 54, 33, 4, 4);
                break;
        }
        setBlitOffset(0);
    }

    private int getAnchoredX() {
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this.screen;
        return Mth.clamp(settings.getSlotX() + accessor.getLeftPos() + (int) (accessor.getImageHeight() * settings.getAnchorX()), 0, screen.width - renderStyle.getRenderWidth());
    }

    private int getUnanchoredX(int x) {
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this.screen;
        return x - accessor.getLeftPos() - (int) (accessor.getImageWidth() * settings.getAnchorX());
    }

    private int getAnchoredY() {
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this.screen;
        return Mth.clamp(settings.getSlotY() + accessor.getTopPos() + (int) (accessor.getImageHeight() * settings.getAnchorY()), 0, screen.width - renderStyle.getRenderWidth());
    }

    private int getUnanchoredY(int y) {
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this.screen;
        return y - accessor.getTopPos() - (int) (accessor.getImageHeight() * settings.getAnchorY());
    }

    public boolean isVisible() {
        return settings.isEnabled();
    }

    public Rect2i getRectangle() {
        int anchoredX = getAnchoredX();
        int anchoredY = getAnchoredY();
        int renderX = anchoredX + renderStyle.getRenderOffsetX() + layout.getSlotOffsetX(screen, renderStyle);
        int renderY = anchoredY + renderStyle.getRenderOffsetY() + layout.getSlotOffsetY(screen, renderStyle);
        return new Rect2i(renderX, renderY, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
    }

}
