package net.blay09.mods.trashslot.api.layout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record Snap(
        Optional<SnapCoordinateProvider> x,
        Optional<SnapCoordinateProvider> y,
        SlotVisual visual
) {
    public static final MapCodec<Snap> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SnapCoordinateProvider.CODEC.optionalFieldOf("x").forGetter(Snap::x),
            SnapCoordinateProvider.CODEC.optionalFieldOf("y").forGetter(Snap::y),
            SlotVisual.CODEC.optionalFieldOf("visual", SlotVisual.DEFAULT).forGetter(Snap::visual)
    ).apply(instance, Snap::new));

    public static final Codec<Snap> CODEC = MAP_CODEC.codec();

    public Optional<Integer> x(TrashSlotContainerContext context, int original) {
        return x.flatMap(it -> it.get(context, original));
    }

    public Optional<Integer> y(TrashSlotContainerContext context, int original) {
        return y.flatMap(it -> it.get(context, original));
    }
}
