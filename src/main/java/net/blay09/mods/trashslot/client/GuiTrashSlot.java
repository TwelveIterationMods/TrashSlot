package net.blay09.mods.trashslot.client;

import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiTrashSlot extends Gui {

    private static final ResourceLocation texture = new ResourceLocation("trashslot", "textures/gui/slot.png");
    private static final int SNAP_SIZE = 6;

    private final GuiInventory parentGui;
    private final Slot trashSlot;
    private int x;
    private int y;
    private int width;
    private int height;
    private int guiTop;
    private int guiBottom;
    private int guiLeft;
    private int guiRight;

    public static final int UPDOWN_WIDTH = 32;
    public static final int UPDOWN_HEIGHT = 25;
    public static final int LEFTRIGHT_WIDTH = 25;
    public static final int LEFTRIGHT_HEIGHT = 31;
    public static final int LONELY_WIDTH = 28;
    public static final int LONELY_HEIGHT = 28;

    private boolean lastHover;
    private boolean lastMouseDown;
    private boolean dragging;
    private int dragStartX;
    private int dragStartY;

    public GuiTrashSlot(GuiInventory parentGui, Slot trashSlot, int x, int y) {
        this.parentGui = parentGui;
        this.guiLeft = parentGui.guiLeft + 4;
        this.guiTop = parentGui.guiTop + 4;
        this.guiRight = parentGui.guiLeft + parentGui.xSize - 4;
        this.guiBottom = parentGui.guiTop + parentGui.ySize - 4;
        this.trashSlot = trashSlot;
        this.x = Math.max(0, Math.min(parentGui.width - LONELY_WIDTH, x));
        this.y = Math.max(0, Math.min(parentGui.height - LONELY_HEIGHT, y));
        if(this.x + width > guiLeft && this.x < guiRight) {
            if(this.y > parentGui.height / 2) {
                this.y = Math.max(this.y, guiBottom);
            } else {
                this.y = Math.min(this.y, guiTop - height);
            }
        }
        if (this.y + height > guiTop && this.y < guiBottom) {
            if (this.x > parentGui.width / 2) {
                this.x = Math.max(this.x, guiRight);
            } else {
                this.x = Math.min(this.x, guiLeft - width);
            }
        }

    }

    public void update(int mouseX, int mouseY) {
        boolean hover = isInside(mouseX, mouseY);
        if(Mouse.isButtonDown(0)) {
            if(!lastMouseDown && lastHover && hover) {
                if(!dragging) {
                    if(Minecraft.getMinecraft().thePlayer.inventory.getItemStack() == null) {
                        if(!trashSlot.getHasStack() || mouseX < parentGui.guiLeft + trashSlot.xDisplayPosition || mouseX >= parentGui.guiLeft + trashSlot.xDisplayPosition + 16 || mouseY < parentGui.guiTop + trashSlot.yDisplayPosition || mouseY >= parentGui.guiTop + trashSlot.yDisplayPosition + 16) {
                            dragStartX = x - mouseX;
                            dragStartY = y - mouseY;
                            dragging = true;
                        }
                    }
                }
            }
            lastMouseDown = true;
        } else {
            lastMouseDown = false;
            if(dragging) {
                if(TrashSlot.trashSlotRelative) {
                    TrashSlot.trashSlotX = (float) x / (float) parentGui.width;
                    TrashSlot.trashSlotY = (float) y / (float) parentGui.height;
                } else {
                    TrashSlot.trashSlotX = x - parentGui.width / 2;
                    TrashSlot.trashSlotY = y - parentGui.height / 2;
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
            if(oldX + LEFTRIGHT_WIDTH > guiLeft && oldX < guiRight) {
                if(y > parentGui.height / 2) {
                    y = Math.max(y, guiBottom);
                } else {
                    y = Math.min(y, guiTop - UPDOWN_HEIGHT);
                }
            } else if (oldY + UPDOWN_HEIGHT > guiTop && oldY < guiBottom) {
                if (x > parentGui.width / 2) {
                    x = Math.max(x, guiRight);
                } else {
                    x = Math.min(x, guiLeft - LEFTRIGHT_WIDTH);
                }
            }

            if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                if(Math.abs(x - parentGui.guiLeft) <= SNAP_SIZE) {
                    x = parentGui.guiLeft;
                } else if(Math.abs((x + width) - (parentGui.guiLeft + parentGui.xSize)) <= SNAP_SIZE) {
                    x = (parentGui.guiLeft + parentGui.xSize) - width;
                }
                if(Math.abs(y - parentGui.guiTop) <= SNAP_SIZE) {
                    y = parentGui.guiTop;
                } else if(Math.abs((y + height) - (parentGui.guiTop + parentGui.ySize)) <= SNAP_SIZE) {
                    y = (parentGui.guiTop + parentGui.ySize) - height;
                }
            }
        }
        lastHover = hover;
    }

    public void drawBackground(int mouseX, int mouseY) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        zLevel = 1f;
        parentGui.mc.getTextureManager().bindTexture(texture);
        boolean isLonely = true;
        if(x >= parentGui.guiLeft && x + UPDOWN_WIDTH <= parentGui.guiLeft + parentGui.xSize) {
            width = UPDOWN_WIDTH;
            height = UPDOWN_HEIGHT;
            if(y == guiBottom) {
                trashSlot.xDisplayPosition = x + 8 - parentGui.guiLeft;
                trashSlot.yDisplayPosition = y + 3 - parentGui.guiTop;
                drawTexturedModalRect(x, y, 0, 0, width, height);
                if(x > parentGui.guiLeft + 4) {
                    drawTexturedModalRect(x, y, 50, 29, 4, 4);
                }
                if(x + width < parentGui.guiLeft + parentGui.xSize - 4) {
                    drawTexturedModalRect(x + width - 4, y, 54, 29, 4, 4);
                }
                isLonely = false;
            } else if(y + height == guiTop) {
                trashSlot.xDisplayPosition = x + 8 - parentGui.guiLeft;
                trashSlot.yDisplayPosition = y + 6 - parentGui.guiTop;
                drawTexturedModalRect(x, y, 32, 0, width, height);
                if(x > parentGui.guiLeft + 4) {
                    drawTexturedModalRect(x, y + height - 4, 50, 25, 4, 4);
                }
                if(x + width < parentGui.guiLeft + parentGui.xSize - 4) {
                    drawTexturedModalRect(x + width - 4, y + height - 4, 54, 25, 4, 4);
                }
                isLonely = false;
            }
        } else if(y >= parentGui.guiTop && y + LEFTRIGHT_HEIGHT <= parentGui.guiTop + parentGui.ySize) {
            width = LEFTRIGHT_WIDTH;
            height = LEFTRIGHT_HEIGHT;
            if(x == guiRight) {
                trashSlot.xDisplayPosition = x + 3 - parentGui.guiLeft;
                trashSlot.yDisplayPosition = y + 7 - parentGui.guiTop;
                drawTexturedModalRect(x, y, 0, 25, width, height);
                if(y > parentGui.guiTop + 4) {
                    drawTexturedModalRect(x, y, 54, 33, 4, 4);
                }
                if(y + height < parentGui.guiTop + parentGui.ySize - 3) {
                    drawTexturedModalRect(x, y + height - 4, 54, 37, 4, 4);
                }
                isLonely = false;
            } else if(x + width == guiLeft) {
                trashSlot.xDisplayPosition = x + 6 - parentGui.guiLeft;
                trashSlot.yDisplayPosition = y + 7 - parentGui.guiTop;
                drawTexturedModalRect(x, y, 25, 25, width, height);
                if(y > parentGui.guiTop + 4) {
                    drawTexturedModalRect(x + width - 4, y, 50, 33, 4, 4);
                }
                if(y + height < parentGui.guiTop + parentGui.ySize - 4) {
                    drawTexturedModalRect(x + width - 4, y + height - 4, 50, 37, 4, 4);
                }
                isLonely = false;
            }
        }
        if(isLonely) {
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
