package net.blay09.mods.trashslot.api.layout;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;

public interface ScreenBoundsProvider {
    Rect2i get(TrashSlotContainerContext context);

    Identifier SCREEN_ID = Identifier.withDefaultNamespace("screen");

    ScreenBoundsProvider SCREEN = (context) -> {
        final var screenAccessor = (AbstractContainerScreenAccessor) context.screen();
        return new Rect2i(screenAccessor.getLeftPos(), screenAccessor.getTopPos(), screenAccessor.getImageWidth(), screenAccessor.getImageHeight());
    };
}
