package net.blay09.mods.trashslot.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.trashslot.api.layout.ScreenBoundsProvider;
import net.blay09.mods.trashslot.api.layout.Snap;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.blay09.mods.trashslot.api.layout.TrashSlotAvailability;
import net.blay09.mods.trashslot.client.gui.layout.TrashContainerLayoutImpl;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.blay09.mods.trashslot.TrashSlot.id;

public record DataDrivenTrashContainerLayout(
        TrashSlotAvailability availability,
        Optional<Integer> width,
        Optional<Integer> height,
        Map<Identifier, Snap> snaps,
        Optional<Identifier> defaultSnap
) {
    public static final MapCodec<DataDrivenTrashContainerLayout> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TrashSlotAvailability.CODEC.fieldOf("availability").forGetter(DataDrivenTrashContainerLayout::availability),
            Codec.INT.optionalFieldOf("width").forGetter(DataDrivenTrashContainerLayout::width),
            Codec.INT.optionalFieldOf("height").forGetter(DataDrivenTrashContainerLayout::height),
            Codec.unboundedMap(Identifier.CODEC, Snap.CODEC).optionalFieldOf("snaps", Map.of()).forGetter(DataDrivenTrashContainerLayout::snaps),
            Identifier.CODEC.optionalFieldOf("defaultSnap").forGetter(DataDrivenTrashContainerLayout::defaultSnap)
    ).apply(instance, DataDrivenTrashContainerLayout::new));

    public static final Codec<DataDrivenTrashContainerLayout> CODEC = MAP_CODEC.codec();

    public TrashContainerLayout createLayout(DataDrivenTrashContainerLayout defaults) {
        final var bounds = new HashMap<Identifier, ScreenBoundsProvider>();
        if (width.isEmpty() && height.isEmpty()) {
            bounds.put(ScreenBoundsProvider.SCREEN_ID, ScreenBoundsProvider.SCREEN);
        } else {
            bounds.put(ScreenBoundsProvider.SCREEN_ID, context -> {
                final var screenAccessor = (AbstractContainerScreenAccessor) context.screen();
                final var effectiveWidth = width.orElseGet(screenAccessor::getImageWidth);
                final var effectiveHeight = height.orElseGet(screenAccessor::getImageHeight);
                return new Rect2i(screenAccessor.getLeftPos(), screenAccessor.getTopPos(), effectiveWidth, effectiveHeight);
            });
        }
        final var snaps = new HashMap<>(defaults.snaps);
        snaps.putAll(this.snaps);
        return new TrashContainerLayoutImpl(availability, bounds, snaps, defaultSnap.orElse(defaults.defaultSnap.orElseGet(() -> id("bottom_right"))));
    }
}
