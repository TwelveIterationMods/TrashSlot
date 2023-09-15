package net.blay09.mods.trashslot.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class Hint {

    private final String id;
    private final Component message;
    private final long endOfLife;

    public Hint(String id, Component message, int timeToDisplay) {
        this.id = id;
        this.message = message;
        this.endOfLife = System.currentTimeMillis() + timeToDisplay;
    }

    public void render(Screen screen, PoseStack poseStack) {
        screen.renderComponentTooltip(poseStack, Lists.newArrayList(message), ((AbstractContainerScreenAccessor) screen).getLeftPos() + ((AbstractContainerScreenAccessor) screen).getImageWidth() / 2 - Minecraft.getInstance().font.width(message) / 2 - 12, 25 + 12);
    }

    public boolean isComplete() {
        return System.currentTimeMillis() >= endOfLife;
    }

    public String getId() {
        return id;
    }
}
