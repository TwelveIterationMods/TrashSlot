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
    private int offsetX;
    private int offsetY;
    private int width;
    private int height;
    private int snapGridTop;
    private int snapGridBottom;
    private int snapGridLeft;
    private int snapGridRight;

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

    public GuiTrashSlot(GuiInventory parentGui, Slot trashSlot) {
        this.parentGui = parentGui;
        this.snapGridLeft = parentGui.guiLeft + 4;
        this.snapGridTop = parentGui.guiTop + 4;
        this.snapGridRight = parentGui.guiLeft + parentGui.xSize - 4;
        this.snapGridBottom = parentGui.guiTop + parentGui.ySize - 4;
        this.trashSlot = trashSlot;

        offsetX = (int) (TrashSlot.trashSlotRelative ? TrashSlot.trashSlotX * parentGui.width : TrashSlot.trashSlotX);
        offsetY = (int) (TrashSlot.trashSlotRelative ? TrashSlot.trashSlotY * parentGui.height : TrashSlot.trashSlotY);
        int renderX = TrashSlot.trashSlotRelative ? offsetX : (parentGui.guiLeft + parentGui.xSize / 2 + offsetX);
        int renderY = TrashSlot.trashSlotRelative ? offsetY : (parentGui.guiTop + parentGui.ySize / 2 + offsetY);
        renderX = Math.max(0, Math.min(parentGui.width - LONELY_WIDTH, renderX));
        renderY = Math.max(0, Math.min(parentGui.height - LONELY_HEIGHT, renderY));
        if(renderX + width > snapGridLeft && renderX < snapGridRight) {
            if(renderY > parentGui.height / 2) {
                renderY = Math.max(renderY, snapGridBottom);
            } else {
                renderY = Math.min(renderY, snapGridTop - height);
            }
        }
        if (renderY + height > snapGridTop && renderY < snapGridBottom) {
            if (renderX > parentGui.width / 2) {
                renderX = Math.max(renderX, snapGridRight);
            } else {
                renderX = Math.min(renderX, snapGridLeft - width);
            }
        }
        offsetX = TrashSlot.trashSlotRelative ? renderX : (renderX - (parentGui.guiLeft + parentGui.xSize / 2));
        offsetY = TrashSlot.trashSlotRelative ? renderY : (renderY - (parentGui.guiTop + parentGui.ySize / 2));
    }

    public void update(int mouseX, int mouseY) {
        this.snapGridLeft = parentGui.guiLeft + 4;
        this.snapGridTop = parentGui.guiTop + 4;
        this.snapGridRight = parentGui.guiLeft + parentGui.xSize - 4;
        this.snapGridBottom = parentGui.guiTop + parentGui.ySize - 4;
        int renderX = TrashSlot.trashSlotRelative ? offsetX : (parentGui.guiLeft + parentGui.xSize / 2 + offsetX);
        int renderY = TrashSlot.trashSlotRelative ? offsetY : (parentGui.guiTop + parentGui.ySize / 2 + offsetY);
        boolean hover = isInside(mouseX, mouseY);
        if(Mouse.isButtonDown(0)) {
            if(!lastMouseDown && lastHover && hover) {
                if(!dragging) {
                    if(Minecraft.getMinecraft().thePlayer.inventory.getItemStack() == null) {
                        if(!trashSlot.getHasStack() || mouseX < parentGui.guiLeft + trashSlot.xDisplayPosition || mouseX >= parentGui.guiLeft + trashSlot.xDisplayPosition + 16 || mouseY < parentGui.guiTop + trashSlot.yDisplayPosition || mouseY >= parentGui.guiTop + trashSlot.yDisplayPosition + 16) {
                            dragStartX = renderX - mouseX;
                            dragStartY = renderY - mouseY;
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
                    TrashSlot.trashSlotX = (float) renderX / (float) parentGui.width;
                    TrashSlot.trashSlotY = (float) renderY / (float) parentGui.height;
                } else {
                    TrashSlot.trashSlotX = renderX - (parentGui.guiLeft + parentGui.xSize / 2);
                    TrashSlot.trashSlotY = renderY - (parentGui.guiTop + parentGui.ySize / 2);
                }
                TrashSlot.instance.saveConfig();
                dragging = false;
            }
        }
        if(dragging) {
            int oldX = renderX;
            int oldY = renderY;
            renderX = mouseX + dragStartX;
            renderY = mouseY + dragStartY;
            renderX = Math.max(0, Math.min(parentGui.width - width, renderX));
            renderY = Math.max(0, Math.min(parentGui.height - height, renderY));
            if(oldX + LEFTRIGHT_WIDTH > snapGridLeft && oldX < snapGridRight) {
                if(renderY > parentGui.height / 2) {
                    renderY = Math.max(renderY, snapGridBottom);
                } else {
                    renderY = Math.min(renderY, snapGridTop - UPDOWN_HEIGHT);
                }
            } else if (oldY + UPDOWN_HEIGHT > snapGridTop && oldY < snapGridBottom) {
                if (renderX > parentGui.width / 2) {
                    renderX = Math.max(renderX, snapGridRight);
                } else {
                    renderX = Math.min(renderX, snapGridLeft - LEFTRIGHT_WIDTH);
                }
            }

            if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                if(Math.abs(renderX - parentGui.guiLeft) <= SNAP_SIZE) {
                    renderX = parentGui.guiLeft;
                } else if(Math.abs((renderX + width) - (parentGui.guiLeft + parentGui.xSize)) <= SNAP_SIZE) {
                    renderX = (parentGui.guiLeft + parentGui.xSize) - width;
                }
                if(Math.abs(renderY - parentGui.guiTop) <= SNAP_SIZE) {
                    renderY = parentGui.guiTop;
                } else if(Math.abs((renderY + height) - (parentGui.guiTop + parentGui.ySize)) <= SNAP_SIZE) {
                    renderY = (parentGui.guiTop + parentGui.ySize) - height;
                }
            }
            offsetX = TrashSlot.trashSlotRelative ? renderX : (renderX - (parentGui.guiLeft + parentGui.xSize / 2));
            offsetY = TrashSlot.trashSlotRelative ? renderY : (renderY - (parentGui.guiTop + parentGui.ySize / 2));
        }
        lastHover = hover;
    }

    public void drawBackground(int mouseX, int mouseY) {
        int renderX = TrashSlot.trashSlotRelative ? offsetX : (parentGui.guiLeft + parentGui.xSize / 2 + offsetX);
        int renderY = TrashSlot.trashSlotRelative ? offsetY : (parentGui.guiTop + parentGui.ySize / 2 + offsetY);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        zLevel = 1f;
        parentGui.mc.getTextureManager().bindTexture(texture);
        boolean isLonely = true;
        if(renderX >= parentGui.guiLeft && renderX + UPDOWN_WIDTH <= parentGui.guiLeft + parentGui.xSize) {
            width = UPDOWN_WIDTH;
            height = UPDOWN_HEIGHT;
            if(renderY == snapGridBottom) {
                trashSlot.xDisplayPosition = renderX + 8 - parentGui.guiLeft;
                trashSlot.yDisplayPosition = renderY + 3 - parentGui.guiTop;
                drawTexturedModalRect(renderX, renderY, 0, 0, width, height);
                if(renderX > parentGui.guiLeft + 4) {
                    drawTexturedModalRect(renderX, renderY, 50, 29, 4, 4);
                }
                if(renderX + width < parentGui.guiLeft + parentGui.xSize - 4) {
                    drawTexturedModalRect(renderX + width - 4, renderY, 54, 29, 4, 4);
                }
                isLonely = false;
            } else if(renderY + height == snapGridTop) {
                trashSlot.xDisplayPosition = renderX + 8 - parentGui.guiLeft;
                trashSlot.yDisplayPosition = renderY + 6 - parentGui.guiTop;
                drawTexturedModalRect(renderX, renderY, 32, 0, width, height);
                if(renderX > parentGui.guiLeft + 4) {
                    drawTexturedModalRect(renderX, renderY + height - 4, 50, 25, 4, 4);
                }
                if(renderX + width < parentGui.guiLeft + parentGui.xSize - 4) {
                    drawTexturedModalRect(renderX + width - 4, renderY + height - 4, 54, 25, 4, 4);
                }
                isLonely = false;
            }
        } else if(renderY >= parentGui.guiTop && renderY + LEFTRIGHT_HEIGHT <= parentGui.guiTop + parentGui.ySize) {
            width = LEFTRIGHT_WIDTH;
            height = LEFTRIGHT_HEIGHT;
            if(renderX == snapGridRight) {
                trashSlot.xDisplayPosition = renderX + 3 - parentGui.guiLeft;
                trashSlot.yDisplayPosition = renderY + 7 - parentGui.guiTop;
                drawTexturedModalRect(renderX, renderY, 0, 25, width, height);
                if(renderY > parentGui.guiTop + 4) {
                    drawTexturedModalRect(renderX, renderY, 54, 33, 4, 4);
                }
                if(renderY + height < parentGui.guiTop + parentGui.ySize - 3) {
                    drawTexturedModalRect(renderX, renderY + height - 4, 54, 37, 4, 4);
                }
                isLonely = false;
            } else if(renderX + width == snapGridLeft) {
                trashSlot.xDisplayPosition = renderX + 6 - parentGui.guiLeft;
                trashSlot.yDisplayPosition = renderY + 7 - parentGui.guiTop;
                drawTexturedModalRect(renderX, renderY, 25, 25, width, height);
                if(renderY > parentGui.guiTop + 4) {
                    drawTexturedModalRect(renderX + width - 4, renderY, 50, 33, 4, 4);
                }
                if(renderY + height < parentGui.guiTop + parentGui.ySize - 4) {
                    drawTexturedModalRect(renderX + width - 4, renderY + height - 4, 50, 37, 4, 4);
                }
                isLonely = false;
            }
        }
        if(isLonely) {
            width = LONELY_WIDTH;
            height = LONELY_HEIGHT;
            trashSlot.xDisplayPosition = renderX + 6 - parentGui.guiLeft;
            trashSlot.yDisplayPosition = renderY + 6 - parentGui.guiTop;
            drawTexturedModalRect(renderX, renderY, 0, 56, width, height);
        }
        zLevel = 0f;
    }

    public boolean isInside(int mouseX, int mouseY) {
        int renderX = TrashSlot.trashSlotRelative ? offsetX : (parentGui.guiLeft + parentGui.xSize / 2 + offsetX);
        int renderY = TrashSlot.trashSlotRelative ? offsetY : (parentGui.guiTop + parentGui.ySize / 2 + offsetY);
        return mouseX >= renderX && mouseY >= renderY && mouseX < renderX + width && mouseY < renderY + height;
    }

}
