package net.blay09.mods.trashslot.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.TrashSlotConfig;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.api.SlotRenderStyle;
import net.blay09.mods.trashslot.api.Snap;
import net.blay09.mods.trashslot.client.ContainerSettings;
import net.blay09.mods.trashslot.client.SlotTrash;
import net.blay09.mods.trashslot.client.TrashSlotGui;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class GuiTrashSlot extends AbstractGui {

    private static final ResourceLocation texture = new ResourceLocation(TrashSlot.MOD_ID, "textures/gui/slot.png");
    private static final int SNAP_SIZE = 7;

    private final TrashSlotGui trashSlotGui;
    private final ContainerScreen<?> gui;
    private final IGuiContainerLayout layout;
    private final ContainerSettings settings;
    private final SlotTrash trashSlot;

    private SlotRenderStyle renderStyle = SlotRenderStyle.LONE;

    private boolean wasMouseDown;
    private boolean isDragging;
    private int dragStartX;
    private int dragStartY;

    public GuiTrashSlot(TrashSlotGui trashSlotGui, ContainerScreen<?> gui, IGuiContainerLayout layout, ContainerSettings settings, SlotTrash trashSlot) {
        this.trashSlotGui = trashSlotGui;
        this.gui = gui;
        this.layout = layout;
        this.settings = settings;
        this.trashSlot = trashSlot;
    }

    public boolean isInside(int mouseX, int mouseY) {
        int anchoredX = getAnchoredX();
        int anchoredY = getAnchoredY();
        int renderX = anchoredX + renderStyle.getRenderOffsetX() + layout.getSlotOffsetX(gui, renderStyle);
        int renderY = anchoredY + renderStyle.getRenderOffsetY() + layout.getSlotOffsetY(gui, renderStyle);
        return mouseX >= renderX && mouseY >= renderY && mouseX < renderX + renderStyle.getRenderWidth() && mouseY < renderY + renderStyle.getRenderHeight();
    }

    public void update(int mouseX, int mouseY) {
        int anchoredX = getAnchoredX();
        int anchoredY = getAnchoredY();
        int renderX = anchoredX + renderStyle.getRenderOffsetX() + layout.getSlotOffsetX(gui, renderStyle);
        int renderY = anchoredY + renderStyle.getRenderOffsetY() + layout.getSlotOffsetY(gui, renderStyle);
        boolean isMouseOver = mouseX >= renderX && mouseY >= renderY && mouseX < renderX + renderStyle.getRenderWidth() && mouseY < renderY + renderStyle.getRenderHeight();
        if (trashSlotGui.isLeftMouseDown()) {
            if (!isDragging && isMouseOver && !wasMouseDown) {
                if (gui.getMinecraft().player.inventory.getItemStack().isEmpty() && (!trashSlot.getHasStack() || !gui.isSlotSelected(trashSlot, mouseX, mouseY))) {
                    dragStartX = renderX - mouseX;
                    dragStartY = renderY - mouseY;
                    isDragging = true;
                }
            }
            wasMouseDown = true;
        } else {
            if (isDragging) {
                settings.save(TrashSlotConfig.clientConfig);
                isDragging = false;
            }
            wasMouseDown = false;
        }
        if (isDragging) {
            int targetX = mouseX + dragStartX;
            int targetY = mouseY + dragStartY;
            for (Rectangle collisionArea : layout.getCollisionAreas(gui)) {
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
                for (Snap snap : layout.getSnaps(gui, renderStyle)) {
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
            targetX = MathHelper.clamp(targetX, 0, gui.width - renderStyle.getRenderWidth());
            targetY = MathHelper.clamp(targetY, 0, gui.height - renderStyle.getRenderHeight());
            settings.setSlotX(getUnanchoredX(targetX));
            settings.setSlotY(getUnanchoredY(targetY));
        }
    }

    public void drawBackground() {
        int renderX = getAnchoredX();
        int renderY = getAnchoredY();
        renderStyle = layout.getSlotRenderStyle(gui, renderX, renderY);
        trashSlot.xPos = renderX - gui.getGuiLeft() + renderStyle.getSlotOffsetX() + layout.getSlotOffsetX(gui, renderStyle);
        trashSlot.yPos = renderY - gui.getGuiTop() + renderStyle.getSlotOffsetY() + layout.getSlotOffsetY(gui, renderStyle);
        blitOffset = 1;
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        gui.getMinecraft().getTextureManager().bindTexture(texture);
        renderX += renderStyle.getRenderOffsetX() + layout.getSlotOffsetX(gui, renderStyle);
        renderY += renderStyle.getRenderOffsetY() + layout.getSlotOffsetY(gui, renderStyle);
        DeletionProvider deletionProvider = TrashSlotConfig.getDeletionProvider();
        int texOffsetX = 0;
        if (deletionProvider == null || !deletionProvider.canUndeleteLast()) {
            texOffsetX = 64;
        }
        switch (renderStyle) {
            case LONE:
                blit(renderX, renderY, texOffsetX, 56, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                break;
            case ATTACH_BOTTOM_CENTER:
                blit(renderX, renderY, texOffsetX, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX, renderY, texOffsetX + 50, 29, 4, 4);
                blit(renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 54, 29, 4, 4);
                break;
            case ATTACH_BOTTOM_LEFT:
                blit(renderX, renderY, texOffsetX, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 54, 29, 4, 4);
                break;
            case ATTACH_BOTTOM_RIGHT:
                blit(renderX, renderY, texOffsetX, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX, renderY, texOffsetX + 50, 29, 4, 4);
                break;
            case ATTACH_TOP_CENTER:
                blit(renderX, renderY, texOffsetX + 32, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 25, 4, 4);
                blit(renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 25, 4, 4);
                break;
            case ATTACH_TOP_LEFT:
                blit(renderX, renderY, texOffsetX + 32, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 25, 4, 4);
                break;
            case ATTACH_TOP_RIGHT:
                blit(renderX, renderY, texOffsetX + 32, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 25, 4, 4);
                break;
            case ATTACH_LEFT_CENTER:
                blit(renderX, renderY, texOffsetX + 25, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 50, 33, 4, 4);
                blit(renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 37, 4, 4);
                break;
            case ATTACH_LEFT_TOP:
                blit(renderX, renderY, texOffsetX + 25, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 37, 4, 4);
                break;
            case ATTACH_LEFT_BOTTOM:
                blit(renderX, renderY, texOffsetX + 25, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 50, 33, 4, 4);
                break;
            case ATTACH_RIGHT_CENTER:
                blit(renderX, renderY, texOffsetX, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX, renderY, texOffsetX + 54, 33, 4, 4);
                blit(renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 37, 4, 4);
                break;
            case ATTACH_RIGHT_TOP:
                blit(renderX, renderY, texOffsetX, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 37, 4, 4);
                break;
            case ATTACH_RIGHT_BOTTOM:
                blit(renderX, renderY, texOffsetX, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                blit(renderX, renderY, texOffsetX + 54, 33, 4, 4);
                break;
        }
        blitOffset = 0;
    }

    private int getAnchoredX() {
        return MathHelper.clamp(settings.getSlotX() + gui.getGuiLeft() + (int) (gui.getXSize() * settings.getAnchorX()), 0, gui.width - renderStyle.getRenderWidth());
    }

    private int getUnanchoredX(int x) {
        return x - gui.getGuiLeft() - (int) (gui.getXSize() * settings.getAnchorX());
    }

    private int getAnchoredY() {
        return MathHelper.clamp(settings.getSlotY() + gui.getGuiTop() + (int) (gui.getYSize() * settings.getAnchorY()), 0, gui.width - renderStyle.getRenderWidth());
    }

    private int getUnanchoredY(int y) {
        return y - gui.getGuiTop() - (int) (gui.getYSize() * settings.getAnchorY());
    }

    public boolean isVisible() {
        return settings.isEnabled();
    }

    public Rectangle2d getRectangle() {
        int anchoredX = getAnchoredX();
        int anchoredY = getAnchoredY();
        int renderX = anchoredX + renderStyle.getRenderOffsetX() + layout.getSlotOffsetX(gui, renderStyle);
        int renderY = anchoredY + renderStyle.getRenderOffsetY() + layout.getSlotOffsetY(gui, renderStyle);
        return new Rectangle2d(renderX, renderY, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
    }

}
