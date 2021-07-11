package net.blay09.mods.trashslot.client.gui.layout;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.api.ISimpleGuiContainerLayout;
import net.blay09.mods.trashslot.api.SlotRenderStyle;
import net.blay09.mods.trashslot.api.Snap;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class SimpleGuiContainerLayout implements IGuiContainerLayout, ISimpleGuiContainerLayout {

    public static final IGuiContainerLayout DEFAULT = (IGuiContainerLayout) new SimpleGuiContainerLayout().enableDefaultCollision().enableDefaultSnaps();
    public static final IGuiContainerLayout DEFAULT_ENABLED = (IGuiContainerLayout) new SimpleGuiContainerLayout().enableDefaultCollision().enableDefaultSnaps().setEnabledByDefault();

    private final List<Rectangle> collisionAreas = new ArrayList<>();
    private final List<Snap> snaps = new ArrayList<>();
    private boolean defaultCollision;
    private boolean defaultSnaps;
    private boolean enabledByDefault;

    @Override
    public ISimpleGuiContainerLayout addCollisionArea(int x, int y, int width, int height) {
        collisionAreas.add(new Rectangle(x, y, width, height));
        return this;
    }

    @Override
    public ISimpleGuiContainerLayout addVerticalSnap(int x) {
        snaps.add(new Snap(Snap.Type.VERTICAL, x, 0));
        return this;
    }

    @Override
    public ISimpleGuiContainerLayout addHorizontalSnap(int y) {
        snaps.add(new Snap(Snap.Type.HORIZONTAL, 0, y));
        return this;
    }

    @Override
    public ISimpleGuiContainerLayout addSnappingPoint(int x, int y) {
        snaps.add(new Snap(Snap.Type.FIXED, x, y));
        return this;
    }

    @Override
    public ISimpleGuiContainerLayout enableDefaultCollision() {
        defaultCollision = true;
        return this;
    }

    @Override
    public ISimpleGuiContainerLayout enableDefaultSnaps() {
        defaultSnaps = true;
        return this;
    }

    @Override
    public ISimpleGuiContainerLayout setEnabledByDefault() {
        enabledByDefault = true;
        return this;
    }

    @Override
    public List<Rectangle> getCollisionAreas(AbstractContainerScreen<?> screen) {
        if (!defaultCollision) {
            return collisionAreas;
        }
        List<Rectangle> list = Lists.newArrayList(collisionAreas);
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        list.add(new Rectangle(accessor.getLeftPos(), accessor.getTopPos(), accessor.getImageWidth(), accessor.getImageHeight()));
        return list;
    }

    @Override
    public List<Snap> getSnaps(AbstractContainerScreen<?> screen, SlotRenderStyle renderStyle) {
        if (!defaultSnaps) {
            return snaps;
        }
        List<Snap> list = Lists.newArrayList(snaps);
		AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        list.add(new Snap(Snap.Type.HORIZONTAL, 0, accessor.getTopPos()));
        list.add(new Snap(Snap.Type.HORIZONTAL, 0, accessor.getTopPos() + accessor.getImageHeight() - renderStyle.getHeight()));
        list.add(new Snap(Snap.Type.VERTICAL, accessor.getLeftPos(), 0));
        list.add(new Snap(Snap.Type.VERTICAL, accessor.getLeftPos() + accessor.getImageWidth() - renderStyle.getWidth(), 0));
        return list;
    }

    @Override
    public SlotRenderStyle getSlotRenderStyle(AbstractContainerScreen<?> screen, int slotX, int slotY) {
		AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        if (slotY == accessor.getTopPos() + accessor.getImageHeight()) {
            int slotRight = slotX + SlotRenderStyle.LONE.getWidth();
            if (slotX == accessor.getLeftPos()) {
                return SlotRenderStyle.ATTACH_BOTTOM_LEFT;
            } else if (slotRight == accessor.getLeftPos() + accessor.getImageWidth()) {
                return SlotRenderStyle.ATTACH_BOTTOM_RIGHT;
            } else if (slotX >= accessor.getLeftPos() && slotRight < accessor.getLeftPos() + accessor.getImageWidth()) {
                return SlotRenderStyle.ATTACH_BOTTOM_CENTER;
            }
        }
        if (slotY + SlotRenderStyle.LONE.getHeight() == accessor.getTopPos()) {
            int slotRight = slotX + SlotRenderStyle.LONE.getWidth();
            if (slotX == accessor.getLeftPos()) {
                return SlotRenderStyle.ATTACH_TOP_LEFT;
            } else if (slotRight == accessor.getLeftPos() + accessor.getImageWidth()) {
                return SlotRenderStyle.ATTACH_TOP_RIGHT;
            } else if (slotX >= accessor.getLeftPos() && slotRight < accessor.getLeftPos() + accessor.getImageWidth()) {
                return SlotRenderStyle.ATTACH_TOP_CENTER;
            }
        }
        if (slotX + SlotRenderStyle.LONE.getWidth() == accessor.getLeftPos()) {
            int slotBottom = slotY + SlotRenderStyle.LONE.getHeight();
            if (slotY == accessor.getTopPos()) {
                return SlotRenderStyle.ATTACH_LEFT_TOP;
            } else if (slotBottom == accessor.getTopPos() + accessor.getImageHeight()) {
                return SlotRenderStyle.ATTACH_LEFT_BOTTOM;
            } else if (slotY >= accessor.getTopPos() && slotBottom < accessor.getTopPos() + accessor.getImageHeight()) {
                return SlotRenderStyle.ATTACH_LEFT_CENTER;
            }
        }
        if (slotX == accessor.getLeftPos() + accessor.getImageWidth()) {
            int slotBottom = slotY + SlotRenderStyle.LONE.getHeight();
            if (slotY == accessor.getTopPos()) {
                return SlotRenderStyle.ATTACH_RIGHT_TOP;
            } else if (slotBottom == accessor.getTopPos() + accessor.getImageHeight()) {
                return SlotRenderStyle.ATTACH_RIGHT_BOTTOM;
            } else if (slotY >= accessor.getTopPos() && slotBottom < accessor.getTopPos() + accessor.getImageHeight()) {
                return SlotRenderStyle.ATTACH_RIGHT_CENTER;
            }
        }
        return SlotRenderStyle.LONE;
    }

    @Override
    public int getDefaultSlotX(AbstractContainerScreen<?> screen) {
		AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        return accessor.getImageWidth() / 2 - SlotRenderStyle.LONE.getWidth();
    }

    @Override
    public int getDefaultSlotY(AbstractContainerScreen<?> screen) {
		AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        return accessor.getImageHeight() / 2;
    }

    @Override
    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    @Override
    public int getSlotOffsetX(AbstractContainerScreen<?> gui, SlotRenderStyle renderStyle) {
        return 0;
    }

    @Override
    public int getSlotOffsetY(AbstractContainerScreen<?> gui, SlotRenderStyle renderStyle) {
        return 0;
    }

}
