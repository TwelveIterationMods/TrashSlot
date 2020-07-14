package net.blay09.mods.trashslot.client.gui;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class GuiHelper extends AbstractGui {
    private static final GuiHelper instance = new GuiHelper();

    public static void drawGradientRect(MatrixStack matrixStack, int left, int top, int right, int bottom, int blitOffset, int startColor, int endColor) {
        int oldBlitOffset = instance.getBlitOffset();
        instance.setBlitOffset(blitOffset);
        instance.fillGradient(matrixStack, left, top, right, bottom, startColor, endColor);
        instance.setBlitOffset(oldBlitOffset);
    }

    public static void renderTooltip(MatrixStack matrixStack, Screen screen, ItemStack itemStack, int x, int y) {
        GuiUtils.preItemToolTip(itemStack);
        screen.renderTooltip(matrixStack, screen.getTooltipFromItem(itemStack), x, y);
        GuiUtils.postItemToolTip();
    }

}
