package net.blay09.mods.trashslot.client.gui;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

public class GuiHelper extends GuiComponent {
    private static final GuiHelper instance = new GuiHelper();

    public static void drawGradientRect(PoseStack matrixStack, int left, int top, int right, int bottom, int blitOffset, int startColor, int endColor) {
        int oldBlitOffset = instance.getBlitOffset();
        instance.setBlitOffset(blitOffset);
        instance.fillGradient(matrixStack, left, top, right, bottom, startColor, endColor);
        instance.setBlitOffset(oldBlitOffset);
    }

}
