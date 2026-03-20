package net.blay09.mods.trashslot.client.gui.layout;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayoutBuilder;
import net.blay09.mods.trashslot.api.layout.SlotRenderStyle;
import net.blay09.mods.trashslot.api.layout.Snap;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;

public class SimpleGuiContainerLayout implements TrashContainerLayout, TrashContainerLayoutBuilder {

	public static final TrashContainerLayout DEFAULT = (TrashContainerLayout) new SimpleGuiContainerLayout().enableDefaultCollision().enableDefaultSnaps();
	public static final TrashContainerLayout DEFAULT_ENABLED = (TrashContainerLayout) new SimpleGuiContainerLayout().enableDefaultCollision().enableDefaultSnaps().setEnabledByDefault();

	private final List<Rect2i> collisionAreas = new ArrayList<>();
	private final List<Snap> snaps = new ArrayList<>();
	private boolean defaultCollision;
	private boolean defaultSnaps;
	private boolean enabledByDefault;

	@Override
	public TrashContainerLayoutBuilder addCollisionArea(int x, int y, int width, int height) {
		collisionAreas.add(new Rect2i(x, y, width, height));
		return this;
	}

	@Override
	public TrashContainerLayoutBuilder addVerticalSnap(int x) {
		snaps.add(new Snap(Snap.Type.VERTICAL, x, 0));
		return this;
	}

	@Override
	public TrashContainerLayoutBuilder addHorizontalSnap(int y) {
		snaps.add(new Snap(Snap.Type.HORIZONTAL, 0, y));
		return this;
	}

	@Override
	public TrashContainerLayoutBuilder addSnappingPoint(int x, int y) {
		snaps.add(new Snap(Snap.Type.FIXED, x, y));
		return this;
	}

	@Override
	public TrashContainerLayoutBuilder enableDefaultCollision() {
		defaultCollision = true;
		return this;
	}

	@Override
	public TrashContainerLayoutBuilder enableDefaultSnaps() {
		defaultSnaps = true;
		return this;
	}

	@Override
	public TrashContainerLayoutBuilder setEnabledByDefault() {
		enabledByDefault = true;
		return this;
	}

	@Override
	public List<Rect2i> getCollisionAreas(AbstractContainerScreen<?> screen) {
		if(!defaultCollision) {
			return collisionAreas;
		}
		
		List<Rect2i> list = Lists.newArrayList(collisionAreas);
		AbstractContainerScreenAccessor screenAccessor = (AbstractContainerScreenAccessor) screen;
		list.add(new Rect2i(screenAccessor.getLeftPos(), screenAccessor.getTopPos(), screenAccessor.getImageWidth(), screenAccessor.getImageHeight()));
		return list;
	}

	@Override
	public List<Snap> getSnaps(AbstractContainerScreen<?> screen, SlotRenderStyle renderStyle) {
		if(!defaultSnaps) {
			return snaps;
		}
		List<Snap> list = Lists.newArrayList(snaps);
		AbstractContainerScreenAccessor screenAccessor = (AbstractContainerScreenAccessor) screen;
		list.add(new Snap(Snap.Type.HORIZONTAL, 0, screenAccessor.getTopPos()));
		list.add(new Snap(Snap.Type.HORIZONTAL, 0, screenAccessor.getTopPos() + screenAccessor.getImageHeight() - renderStyle.getHeight()));
		list.add(new Snap(Snap.Type.VERTICAL, screenAccessor.getLeftPos(), 0));
		list.add(new Snap(Snap.Type.VERTICAL, screenAccessor.getLeftPos() + screenAccessor.getImageWidth() - renderStyle.getWidth(), 0));
		return list;
	}

	@Override
	public SlotRenderStyle getSlotRenderStyle(AbstractContainerScreen<?> screen, int slotX, int slotY) {
		AbstractContainerScreenAccessor screenAccessor = (AbstractContainerScreenAccessor) screen;
		if(slotY == screenAccessor.getTopPos() + screenAccessor.getImageHeight()) {
			int slotRight = slotX + SlotRenderStyle.LONE.getWidth();
			if(slotX == screenAccessor.getLeftPos()) {
				return SlotRenderStyle.ATTACH_BOTTOM_LEFT;
			} else if(slotRight == screenAccessor.getLeftPos() + screenAccessor.getImageWidth()) {
				return SlotRenderStyle.ATTACH_BOTTOM_RIGHT;
			} else if(slotX >= screenAccessor.getLeftPos() && slotRight < screenAccessor.getLeftPos() + screenAccessor.getImageWidth()) {
				return SlotRenderStyle.ATTACH_BOTTOM_CENTER;
			}
		}
		if(slotY + SlotRenderStyle.LONE.getHeight() == screenAccessor.getTopPos()) {
			int slotRight = slotX + SlotRenderStyle.LONE.getWidth();
			if(slotX == screenAccessor.getLeftPos()) {
				return SlotRenderStyle.ATTACH_TOP_LEFT;
			} else if(slotRight == screenAccessor.getLeftPos() + screenAccessor.getImageWidth()) {
				return SlotRenderStyle.ATTACH_TOP_RIGHT;
			} else if(slotX >= screenAccessor.getLeftPos() && slotRight < screenAccessor.getLeftPos() + screenAccessor.getImageWidth()) {
				return SlotRenderStyle.ATTACH_TOP_CENTER;
			}
		}
		if(slotX + SlotRenderStyle.LONE.getWidth() == screenAccessor.getLeftPos()) {
			int slotBottom = slotY + SlotRenderStyle.LONE.getHeight();
			if(slotY == screenAccessor.getTopPos()) {
				return SlotRenderStyle.ATTACH_LEFT_TOP;
			} else if(slotBottom == screenAccessor.getTopPos() + screenAccessor.getImageHeight()) {
				return SlotRenderStyle.ATTACH_LEFT_BOTTOM;
			} else if(slotY >= screenAccessor.getTopPos() && slotBottom < screenAccessor.getTopPos() + screenAccessor.getImageHeight()) {
				return SlotRenderStyle.ATTACH_LEFT_CENTER;
			}
		}
		if(slotX == screenAccessor.getLeftPos() + screenAccessor.getImageWidth()) {
			int slotBottom = slotY + SlotRenderStyle.LONE.getHeight();
			if(slotY == screenAccessor.getTopPos()) {
				return SlotRenderStyle.ATTACH_RIGHT_TOP;
			} else if(slotBottom == screenAccessor.getTopPos() + screenAccessor.getImageHeight()) {
				return SlotRenderStyle.ATTACH_RIGHT_BOTTOM;
			} else if(slotY >= screenAccessor.getTopPos() && slotBottom < screenAccessor.getTopPos() + screenAccessor.getImageHeight()) {
				return SlotRenderStyle.ATTACH_RIGHT_CENTER;
			}
		}
		return SlotRenderStyle.LONE;
	}

	@Override
	public int getDefaultSlotX(AbstractContainerScreen<?> screen) {
		return ((AbstractContainerScreenAccessor) screen).getImageWidth() / 2 - SlotRenderStyle.LONE.getWidth();
	}

	@Override
	public int getDefaultSlotY(AbstractContainerScreen<?> screen) {
		return ((AbstractContainerScreenAccessor) screen).getImageHeight() / 2;
	}

	@Override
	public boolean isEnabledByDefault() {
		return enabledByDefault;
	}

	@Override
	public int getSlotOffsetX(AbstractContainerScreen<?> screen, SlotRenderStyle renderStyle) {
		return 0;
	}

	@Override
	public int getSlotOffsetY(AbstractContainerScreen<?> screen, SlotRenderStyle renderStyle) {
		return 0;
	}

}
