package net.blay09.mods.trashslot.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiTrashSlot extends Gui {

    private static final ResourceLocation texture = new ResourceLocation("trashslot", "textures/gui/slot.png");

    private final GuiInventory parentGui;
    private int x;
    private int y;
    private int width;
    private int height;

    public GuiTrashSlot(GuiInventory parentGui, int x, int y) {
        this.parentGui = parentGui;
        this.x = x;
        this.y = y;
        this.width = 30;
        this.height = 24;
    }

    public void drawBackground(int mouseX, int mouseY) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        zLevel = 1f;
        parentGui.mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(x, y, 0, 0, width, height);
        zLevel = 0f;
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
