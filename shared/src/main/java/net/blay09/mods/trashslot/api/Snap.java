package net.blay09.mods.trashslot.api;

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
