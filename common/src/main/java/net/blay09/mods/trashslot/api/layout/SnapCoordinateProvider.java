package net.blay09.mods.trashslot.api.layout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Locale;
import java.util.Optional;

public interface SnapCoordinateProvider {
    Codec<SnapCoordinateProvider> CODEC = Codec.STRING.partialDispatch(
            "type",
            SnapCoordinateProvider::typeKey,
            SnapCoordinateProvider::codecByType
    );

    Optional<Integer> get(TrashSlotContainerContext context, int original);

    record Constant(int value) implements SnapCoordinateProvider {
        public static final MapCodec<Constant> CODEC = Codec.INT.fieldOf("value")
                .xmap(Constant::new, Constant::value);

        @Override
        public Optional<Integer> get(TrashSlotContainerContext context, int original) {
            return Optional.of(value);
        }
    }

    record Range(SnapCoordinateProvider from, SnapCoordinateProvider to) implements SnapCoordinateProvider {
        public static final MapCodec<Range> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                SnapCoordinateProvider.CODEC.fieldOf("from").forGetter(Range::from),
                SnapCoordinateProvider.CODEC.fieldOf("to").forGetter(Range::to)
        ).apply(instance, Range::new));

        @Override
        public Optional<Integer> get(TrashSlotContainerContext context, int original) {
            final var fromValue = from.get(context, original);
            final var toValue = to.get(context, original);
            return fromValue.map(it -> original >= it).orElse(false)
                    && toValue.map(it -> original <= it).orElse(false)
                    ? Optional.of(original) : Optional.empty();
        }
    }

    record Top(Identifier rect, int offset) implements SnapCoordinateProvider {
        public static final MapCodec<Top> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.optionalFieldOf("rect", ScreenBoundsProvider.SCREEN_ID).forGetter(Top::rect),
                Codec.INT.optionalFieldOf("offset", 0).forGetter(Top::offset)
        ).apply(instance, Top::new));

        @Override
        public Optional<Integer> get(TrashSlotContainerContext context, int original) {
            return context.rect(rect).map(it -> it.getY() + offset);
        }
    }

    record Bottom(Identifier rect, int offset) implements SnapCoordinateProvider {
        public static final MapCodec<Bottom> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.optionalFieldOf("rect", ScreenBoundsProvider.SCREEN_ID).forGetter(Bottom::rect),
                Codec.INT.optionalFieldOf("offset", 0).forGetter(Bottom::offset)
        ).apply(instance, Bottom::new));

        @Override
        public Optional<Integer> get(TrashSlotContainerContext context, int original) {
            return context.rect(rect).map(it -> it.getY() + it.getHeight() + offset);
        }
    }

    record Left(Identifier rect, int offset) implements SnapCoordinateProvider {
        public static final MapCodec<Left> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.optionalFieldOf("rect", ScreenBoundsProvider.SCREEN_ID).forGetter(Left::rect),
                Codec.INT.optionalFieldOf("offset", 0).forGetter(Left::offset)
        ).apply(instance, Left::new));

        @Override
        public Optional<Integer> get(TrashSlotContainerContext context, int original) {
            return context.rect(rect).map(it -> it.getX() + offset);
        }
    }

    record Right(Identifier rect, int offset) implements SnapCoordinateProvider {
        public static final MapCodec<Right> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.optionalFieldOf("rect", ScreenBoundsProvider.SCREEN_ID).forGetter(Right::rect),
                Codec.INT.optionalFieldOf("offset", 0).forGetter(Right::offset)
        ).apply(instance, Right::new));

        @Override
        public Optional<Integer> get(TrashSlotContainerContext context, int original) {
            return context.rect(rect).map(it -> it.getX() + it.getWidth() + offset);
        }
    }

    private static DataResult<String> typeKey(SnapCoordinateProvider provider) {
        return switch (provider) {
            case Constant _ -> DataResult.success("constant");
            case Range _ -> DataResult.success("range");
            case Top _ -> DataResult.success("top");
            case Bottom _ -> DataResult.success("bottom");
            case Left _ -> DataResult.success("left");
            case Right _ -> DataResult.success("right");
            default -> DataResult.error(() -> "Unsupported snap coordinate provider: " + provider.getClass().getName());
        };

    }

    private static DataResult<? extends MapCodec<? extends SnapCoordinateProvider>> codecByType(String type) {
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "constant" -> DataResult.success(Constant.CODEC);
            case "range" -> DataResult.success(Range.CODEC);
            case "top" -> DataResult.success(Top.CODEC);
            case "bottom" -> DataResult.success(Bottom.CODEC);
            case "left" -> DataResult.success(Left.CODEC);
            case "right" -> DataResult.success(Right.CODEC);
            default -> DataResult.error(() -> "Unknown snap coordinate provider type: " + type);
        };
    }
}
