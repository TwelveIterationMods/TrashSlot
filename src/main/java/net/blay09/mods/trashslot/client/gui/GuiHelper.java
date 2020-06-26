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
        int oldBlitOffset = instance.func_230927_p_(); // getBlitOffset()
        instance.func_230926_e_(blitOffset); // setBlitOffset()
        instance.func_238468_a_(matrixStack, left, top, right, bottom, startColor, endColor); // fillGradient
        instance.func_230926_e_(oldBlitOffset); // setBlitOffset()
    }

    public static void renderTooltip(MatrixStack matrixStack, Screen screen, ItemStack itemStack, int x, int y) {
        FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
        GuiUtils.preItemToolTip(itemStack);
        screen.func_238654_b_(matrixStack, screen.func_231151_a_(itemStack), x, y, (font == null ? screen.getMinecraft().fontRenderer : font)); // renderTooltip, getTooltipFromItem
        GuiUtils.postItemToolTip();
    }

}
