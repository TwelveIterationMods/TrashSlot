package net.blay09.mods.trashslot.client;

import net.blay09.mods.trashslot.SlotTrash;
import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiTrashSlot extends Gui {

    private static final ResourceLocation texture = new ResourceLocation("trashslot", "textures/gui/slot.png");

    private final GuiInventory parentGui;
    private final SlotTrash trashSlot;
    private int x;
    private int y;
    private int width;
    private int height;

    private static final int UPDOWN_WIDTH = 31;
    private static final int UPDOWN_HEIGHT = 25;
    private static final int LEFTRIGHT_WIDTH = 25;
    private static final int LEFTRIGHT_HEIGHT = 31;
    private static final int LONELY_WIDTH = 28;
    private static final int LONELY_HEIGHT = 29;

    private boolean lastHover;
    private boolean lastMouseDown;
    private boolean dragging;
    private int dragStartX;
    private int dragStartY;

    public GuiTrashSlot(GuiInventory parentGui, SlotTrash trashSlot, int x, int y) {
        this.parentGui = parentGui;
        this.trashSlot = trashSlot;
        this.x = Math.max(0, Math.min(parentGui.width - LONELY_WIDTH, x));
        this.y = Math.max(0, Math.min(parentGui.height - LONELY_HEIGHT, y));
        if(this.x + width > parentGui.guiLeft && this.x < parentGui.guiLeft + parentGui.xSize) {
            if(this.y > parentGui.height / 2) {
                this.y = Math.max(this.y, parentGui.guiTop + parentGui.ySize);
            } else {
                this.y = Math.min(this.y, parentGui.guiTop - height);
            }
        }
        if (this.y + height > parentGui.guiTop && this.y < parentGui.guiTop + parentGui.ySize) {
            if (this.x > parentGui.width / 2) {
                this.x = Math.max(this.x, parentGui.guiLeft + parentGui.xSize);
            } else {
                this.x = Math.min(this.x, parentGui.guiLeft - width);
            }
        }
    }

    public void update(int mouseX, int mouseY) {
        boolean hover = isInside(mouseX, mouseY);
        if(Mouse.isButtonDown(0)) {
            if(!lastMouseDown && lastHover && hover) {
                if(!dragging) {
                    dragStartX = x - mouseX;
                    dragStartY = y - mouseY;
                }
                dragging = true;
            }
            lastMouseDown = true;
        } else {
            lastMouseDown = false;
            if(dragging) {
                if(TrashSlot.trashSlotRelative) {
                    TrashSlot.trashSlotX = (float) x / (float) parentGui.width;
                    TrashSlot.trashSlotY = (float) y / (float) parentGui.height;
                } else {
                    TrashSlot.trashSlotX = x;
                    TrashSlot.trashSlotY = y;
                }
                TrashSlot.instance.saveConfig();
                dragging = false;
            }
        }
        if(dragging) {
            int oldX = x;
            int oldY = y;
            x = mouseX + dragStartX;
            y = mouseY + dragStartY;
            x = Math.max(0, Math.min(parentGui.width - width, x));
            y = Math.max(0, Math.min(parentGui.height - height, y));
            if(oldX + width > parentGui.guiLeft && oldX < parentGui.guiLeft + parentGui.xSize) {
                if(y > parentGui.height / 2) {
                    y = Math.max(y, parentGui.guiTop + parentGui.ySize);
                } else {
                    y = Math.min(y, parentGui.guiTop - height);
                }
            } else if (oldY + height > parentGui.guiTop && oldY < parentGui.guiTop + parentGui.ySize) {
                if (x > parentGui.width / 2) {
                    x = Math.max(x, parentGui.guiLeft + parentGui.xSize);
                } else {
                    x = Math.min(x, parentGui.guiLeft - width);
                }
            }
        }
        lastHover = hover;
    }

    public void drawBackground(int mouseX, int mouseY) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        zLevel = 1f;
        parentGui.mc.getTextureManager().bindTexture(texture);
        if(y == parentGui.guiTop + parentGui.ySize && x >= parentGui.guiLeft && x + UPDOWN_WIDTH < parentGui.guiLeft + parentGui.xSize) {
            width = UPDOWN_WIDTH;
            height = UPDOWN_HEIGHT;
            trashSlot.xDisplayPosition = x + 7 - parentGui.guiLeft;
            trashSlot.yDisplayPosition = y + 3 - parentGui.guiTop;
            drawTexturedModalRect(x, y, 0, 0, width, height);
        } else if(x == parentGui.guiLeft + parentGui.xSize && y >= parentGui.guiTop && y + LEFTRIGHT_HEIGHT < parentGui.guiTop + parentGui.ySize) {
            width = LEFTRIGHT_WIDTH;
            height = LEFTRIGHT_HEIGHT;
            trashSlot.xDisplayPosition = x + 3 - parentGui.guiLeft;
            trashSlot.yDisplayPosition = y + 7 - parentGui.guiTop;
            drawTexturedModalRect(x, y, 0, 25, width, height);
        } else{
            width = LONELY_WIDTH;
            height = LONELY_HEIGHT;
            trashSlot.xDisplayPosition = x + 6 - parentGui.guiLeft;
            trashSlot.yDisplayPosition = y + 6 - parentGui.guiTop;
            drawTexturedModalRect(x, y, 0, 56, width, height);
        }
        zLevel = 0f;
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
