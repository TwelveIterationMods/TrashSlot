package net.blay09.mods.trashslot.client;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public class Hint {

    private final String id;
    private final Component message;
    private final long endOfLife;

    public Hint(String id, Component message, int timeToDisplay) {
        this.id = id;
        this.message = message;
        this.endOfLife = System.currentTimeMillis() + timeToDisplay;
    }

    public void render(Screen screen, GuiGraphics guiGraphics) {
        final var font = Minecraft.getInstance().font;
        guiGraphics.renderTooltip(
                font,
                List.of(message),
                Optional.empty(),
                ((AbstractContainerScreenAccessor) screen).getLeftPos() + ((AbstractContainerScreenAccessor) screen).getImageWidth() / 2 - font.width(message) / 2 - 12, ((AbstractContainerScreenAccessor) screen).getTopPos() - 20 + 12);
    }

    public boolean isComplete() {
        return System.currentTimeMillis() >= endOfLife;
    }

    public String getId() {
        return id;
    }
}
