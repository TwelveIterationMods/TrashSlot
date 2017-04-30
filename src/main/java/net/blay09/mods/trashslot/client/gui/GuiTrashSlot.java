package net.blay09.mods.trashslot.client.gui;

import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.api.SlotRenderStyle;
import net.blay09.mods.trashslot.api.Snap;
import net.blay09.mods.trashslot.client.SlotTrash;
import net.blay09.mods.trashslot.client.TrashContainerSettings;
import net.blay09.mods.trashslot.client.deletion.DeletionProvider;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class GuiTrashSlot extends Gui {

    private static final ResourceLocation texture = new ResourceLocation(TrashSlot.MOD_ID, "textures/gui/slot.png");
    private static final int SNAP_SIZE = 7;

    private final GuiContainer gui;
    private final IGuiContainerLayout layout;
    private final TrashContainerSettings settings;
    private final SlotTrash trashSlot;

    private SlotRenderStyle renderStyle = SlotRenderStyle.LONE;

    private boolean isDragging;
    private int dragStartX;
    private int dragStartY;

    public GuiTrashSlot(GuiContainer gui, IGuiContainerLayout layout, TrashContainerSettings settings, SlotTrash trashSlot) {
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
        if(Mouse.isButtonDown(0)) {
            if(!isDragging && isMouseOver) {
                if(gui.mc.player.inventory.getItemStack().isEmpty() && (!trashSlot.getHasStack() || !gui.isMouseOverSlot(trashSlot, mouseX, mouseY))) {
                    dragStartX = renderX - mouseX;
                    dragStartY = renderY - mouseY;
                    isDragging = true;
                }
            }
        } else {
            if(isDragging) {
                settings.save(TrashSlot.config);
                isDragging = false;
            }
        }
        if(isDragging) {
            int targetX = mouseX + dragStartX;
            int targetY = mouseY + dragStartY;
            for(Rectangle collisionArea : layout.getCollisionAreas(gui)) {
                int targetRight = targetX + renderStyle.getWidth();
                int targetBottom = targetY + renderStyle.getHeight();
                int rectRight = collisionArea.x + collisionArea.width;
                int rectBottom = collisionArea.y + collisionArea.height;
                if(targetRight >= collisionArea.x && targetX < rectRight && targetBottom >= collisionArea.y && targetY < rectBottom) {
                    int distLeft = targetRight - collisionArea.x;
                    int distRight = rectRight - targetX;
                    int distTop = targetBottom - collisionArea.y;
                    int distBottom = rectBottom - targetY;
                    if(anchoredX >= collisionArea.x && anchoredX < collisionArea.x + collisionArea.width) {
                        targetY = distTop < distBottom ? collisionArea.y - renderStyle.getHeight() : collisionArea.y + collisionArea.height;
                    } else {
                        targetX = distLeft < distRight ? collisionArea.x - renderStyle.getWidth() : collisionArea.x + collisionArea.width;
                    }
                }
            }
            if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
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
            settings.slotX = getUnanchoredX(targetX);
            settings.slotY = getUnanchoredY(targetY);
        }
    }

    public void drawBackground(int mouseX, int mouseY) {
        int renderX = getAnchoredX();
        int renderY = getAnchoredY();
        renderStyle = layout.getSlotRenderStyle(gui, renderX, renderY);
        trashSlot.xPos = renderX - gui.getGuiLeft() + renderStyle.getSlotOffsetX() + layout.getSlotOffsetX(gui, renderStyle);
        trashSlot.yPos = renderY - gui.getGuiTop() + renderStyle.getSlotOffsetY() + layout.getSlotOffsetY(gui, renderStyle);
        zLevel = 1f;
        GlStateManager.color(1f, 1f, 1f, 1f);
        gui.mc.getTextureManager().bindTexture(texture);
        renderX += renderStyle.getRenderOffsetX() + layout.getSlotOffsetX(gui, renderStyle);
        renderY += renderStyle.getRenderOffsetY() + layout.getSlotOffsetY(gui, renderStyle);
        DeletionProvider deletionProvider = TrashSlot.proxy.getDeletionProvider();
        int texOffsetX = 0;
        if(deletionProvider == null || !deletionProvider.canUndeleteLast()) {
            texOffsetX = 64;
        }
        switch(renderStyle) {
            case LONE:
                drawTexturedModalRect(renderX, renderY, texOffsetX, 56, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                break;
            case ATTACH_BOTTOM_CENTER:
                drawTexturedModalRect(renderX, renderY, texOffsetX, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX, renderY, texOffsetX + 50, 29, 4, 4);
                drawTexturedModalRect(renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 54, 29, 4, 4);
                break;
            case ATTACH_BOTTOM_LEFT:
                drawTexturedModalRect(renderX, renderY, texOffsetX, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 54, 29, 4, 4);
                break;
            case ATTACH_BOTTOM_RIGHT:
                drawTexturedModalRect(renderX, renderY, texOffsetX, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX, renderY, texOffsetX + 50, 29, 4, 4);
                break;
            case ATTACH_TOP_CENTER:
                drawTexturedModalRect(renderX, renderY, texOffsetX + 32, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 25, 4, 4);
                drawTexturedModalRect(renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 25, 4, 4);
                break;
            case ATTACH_TOP_LEFT:
                drawTexturedModalRect(renderX, renderY, texOffsetX + 32, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 25, 4, 4);
                break;
            case ATTACH_TOP_RIGHT:
                drawTexturedModalRect(renderX, renderY, texOffsetX + 32, 0, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 25, 4, 4);
                break;
            case ATTACH_LEFT_CENTER:
                drawTexturedModalRect(renderX, renderY, texOffsetX + 25, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 50, 33, 4, 4);
                drawTexturedModalRect(renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 37, 4, 4);
                break;
            case ATTACH_LEFT_TOP:
                drawTexturedModalRect(renderX, renderY, texOffsetX + 25, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX + renderStyle.getRenderWidth() - 4, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 50, 37, 4, 4);
                break;
            case ATTACH_LEFT_BOTTOM:
                drawTexturedModalRect(renderX, renderY, texOffsetX + 25, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX + renderStyle.getRenderWidth() - 4, renderY, texOffsetX + 50, 33, 4, 4);
                break;
            case ATTACH_RIGHT_CENTER:
                drawTexturedModalRect(renderX, renderY, texOffsetX, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX, renderY, texOffsetX + 54, 33, 4, 4);
                drawTexturedModalRect(renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 37, 4, 4);
                break;
            case ATTACH_RIGHT_TOP:
                drawTexturedModalRect(renderX, renderY, texOffsetX, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX, renderY + renderStyle.getRenderHeight() - 4, texOffsetX + 54, 37, 4, 4);
                break;
            case ATTACH_RIGHT_BOTTOM:
                drawTexturedModalRect(renderX, renderY, texOffsetX, 25, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
                drawTexturedModalRect(renderX, renderY, texOffsetX + 54, 33, 4, 4);
                break;
        }
        zLevel = 0f;
    }

    private int getAnchoredX() {
        return MathHelper.clamp(settings.slotX + gui.getGuiLeft() + (int) (gui.getXSize() * settings.anchorX), 0, gui.width - renderStyle.getRenderWidth());
    }

    private int getUnanchoredX(int x) {
        return x - gui.getGuiLeft() - (int) (gui.getXSize() * settings.anchorX);
    }

    private int getAnchoredY() {
        return MathHelper.clamp(settings.slotY + gui.getGuiTop() + (int) (gui.getYSize() * settings.anchorY), 0, gui.width - renderStyle.getRenderWidth());
    }

    private int getUnanchoredY(int y) {
        return y - gui.getGuiTop() - (int) (gui.getYSize() * settings.anchorY);
    }

    public boolean isVisible() {
        return settings.isEnabled();
    }

	public Rectangle getRectangle() {
        int anchoredX = getAnchoredX();
        int anchoredY = getAnchoredY();
        int renderX = anchoredX + renderStyle.getRenderOffsetX() + layout.getSlotOffsetX(gui, renderStyle);
        int renderY = anchoredY + renderStyle.getRenderOffsetY() + layout.getSlotOffsetY(gui, renderStyle);
        return new Rectangle(renderX, renderY, renderStyle.getRenderWidth(), renderStyle.getRenderHeight());
	}

}
