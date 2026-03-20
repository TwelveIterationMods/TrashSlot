package net.blay09.mods.trashslot.api.layout;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum SlotVisual implements StringRepresentable {
	DEFAULT(Identifier.withDefaultNamespace("trashslot/slot_default"), 28, 28, -6, -6),
	ATTACH_TOP(Identifier.withDefaultNamespace("trashslot/slot_attach_top"), 32, 25, -8, -6),
	ATTACH_TOP_LEFT(Identifier.withDefaultNamespace("trashslot/slot_attach_top_left"), 32, 25, -8, -6),
	ATTACH_TOP_RIGHT(Identifier.withDefaultNamespace("trashslot/slot_attach_top_right"), 32, 25, -8, -6),

	ATTACH_BOTTOM(Identifier.withDefaultNamespace("trashslot/slot_attach_bottom"), 32, 25, -8, -3),
	ATTACH_BOTTOM_LEFT(Identifier.withDefaultNamespace("trashslot/slot_attach_bottom_left"), 32, 25, -8, -3),
	ATTACH_BOTTOM_RIGHT(Identifier.withDefaultNamespace("trashslot/slot_attach_bottom_right"), 32, 25, -8, -3),

	ATTACH_LEFT(Identifier.withDefaultNamespace("trashslot/slot_attach_left"), 25, 31, -6, -7),
	ATTACH_LEFT_TOP(Identifier.withDefaultNamespace("trashslot/slot_attach_left_top"), 25, 31, -6, -7),
	ATTACH_LEFT_BOTTOM(Identifier.withDefaultNamespace("trashslot/slot_attach_left_bottom"), 25, 31, -6, -7),

	ATTACH_RIGHT(Identifier.withDefaultNamespace("trashslot/slot_attach_right"), 25, 31, -3, -7),
	ATTACH_RIGHT_TOP(Identifier.withDefaultNamespace("trashslot/slot_attach_right_top"), 25, 31, -3, -7),
	ATTACH_RIGHT_BOTTOM(Identifier.withDefaultNamespace("trashslot/slot_attach_right_bottom"), 25, 31, -3, -7);


	public static final StringRepresentable.EnumCodec<SlotVisual> CODEC = StringRepresentable.fromEnum(SlotVisual::values);

	private final Identifier sprite;
	private final int width;
	private final int height;
	private final int offsetX;
	private final int offsetY;

	SlotVisual(Identifier sprite, int width, int height, int offsetX, int offsetY) {
        this.sprite = sprite;
        this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

    public Rect2i getBounds(int x, int y) {
        return new Rect2i(x + offsetX, y + offsetY, width, height);
    }

    public Identifier sprite() {
        return sprite;
    }

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}

	@Override
	public String toString() {
		return getSerializedName();
	}
}
