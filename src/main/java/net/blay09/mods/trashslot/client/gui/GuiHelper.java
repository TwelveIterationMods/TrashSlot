package net.blay09.mods.trashslot.client.gui;


import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;

public class GuiHelper extends AbstractGui {
    private static final GuiHelper instance = new GuiHelper();

    public static void drawGradientRect(int left, int top, int right, int bottom, int blitOffset, int startColor, int endColor) {
        int oldBlitOffset = instance.blitOffset;
        instance.blitOffset = blitOffset;
        instance.fillGradient(left, top, right, bottom, startColor, endColor);
        instance.blitOffset = oldBlitOffset;
    }

    public static void renderTooltip(Screen screen, ItemStack itemStack, int x, int y) {
        FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(itemStack);
        screen.renderTooltip(screen.getTooltipFromItem(itemStack), x, y, (font == null ? screen.getMinecraft().fontRenderer : font));
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }

}
