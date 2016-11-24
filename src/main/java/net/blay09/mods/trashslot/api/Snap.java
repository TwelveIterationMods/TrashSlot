package net.blay09.mods.trashslot.api;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Snap {
	public enum Type {
		VERTICAL,
		HORIZONTAL,
		FIXED
	}

	private final int x;
	private final int y;
	private final Type type;

	public Snap(Type type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Type getType() {
		return type;
	}
}
