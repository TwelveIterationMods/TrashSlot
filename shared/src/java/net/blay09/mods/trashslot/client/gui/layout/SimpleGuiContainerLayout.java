package net.blay09.mods.trashslot.client.gui.layout;

import com.google.common.collect.Lists;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.api.ISimpleGuiContainerLayout;
import net.blay09.mods.trashslot.api.SlotRenderStyle;
import net.blay09.mods.trashslot.api.Snap;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;

public class SimpleGuiContainerLayout implements IGuiContainerLayout, ISimpleGuiContainerLayout {

	public static final IGuiContainerLayout DEFAULT = (IGuiContainerLayout) new SimpleGuiContainerLayout().enableDefaultCollision().enableDefaultSnaps();
	public static final IGuiContainerLayout DEFAULT_ENABLED = (IGuiContainerLayout) new SimpleGuiContainerLayout().enableDefaultCollision().enableDefaultSnaps().setEnabledByDefault();

	private final List<Rect2i> collisionAreas = new ArrayList<>();
	private final List<Snap> snaps = new ArrayList<>();
	private boolean defaultCollision;
	private boolean defaultSnaps;
	private boolean enabledByDefault;

	@Override
	public ISimpleGuiContainerLayout addCollisionArea(int x, int y, int width, int height) {
		collisionAreas.add(new Rect2i(x, y, width, height));
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
	public List<Rect2i> getCollisionAreas(AbstractContainerScreen<?> gui) {
		if(!defaultCollision) {
			return collisionAreas;
		}
		List<Rect2i> list = Lists.newArrayList(collisionAreas);
		list.add(new Rect2i(gui.getGuiLeft(), gui.getGuiTop(), gui.getXSize(), gui.getYSize()));
		return list;
	}

	@Override
	public List<Snap> getSnaps(AbstractContainerScreen<?> gui, SlotRenderStyle renderStyle) {
		if(!defaultSnaps) {
			return snaps;
		}
		List<Snap> list = Lists.newArrayList(snaps);
		list.add(new Snap(Snap.Type.HORIZONTAL, 0, gui.getGuiTop()));
		list.add(new Snap(Snap.Type.HORIZONTAL, 0, gui.getGuiTop() + gui.getYSize() - renderStyle.getHeight()));
		list.add(new Snap(Snap.Type.VERTICAL, gui.getGuiLeft(), 0));
		list.add(new Snap(Snap.Type.VERTICAL, gui.getGuiLeft() + gui.getXSize() - renderStyle.getWidth(), 0));
		return list;
	}

	@Override
	public SlotRenderStyle getSlotRenderStyle(AbstractContainerScreen<?> gui, int slotX, int slotY) {
		if(slotY == gui.getGuiTop() + gui.getYSize()) {
			int slotRight = slotX + SlotRenderStyle.LONE.getWidth();
			if(slotX == gui.getGuiLeft()) {
				return SlotRenderStyle.ATTACH_BOTTOM_LEFT;
			} else if(slotRight == gui.getGuiLeft() + gui.getXSize()) {
				return SlotRenderStyle.ATTACH_BOTTOM_RIGHT;
			} else if(slotX >= gui.getGuiLeft() && slotRight < gui.getGuiLeft() + gui.getXSize()) {
				return SlotRenderStyle.ATTACH_BOTTOM_CENTER;
			}
		}
		if(slotY + SlotRenderStyle.LONE.getHeight() == gui.getGuiTop()) {
			int slotRight = slotX + SlotRenderStyle.LONE.getWidth();
			if(slotX == gui.getGuiLeft()) {
				return SlotRenderStyle.ATTACH_TOP_LEFT;
			} else if(slotRight == gui.getGuiLeft() + gui.getXSize()) {
				return SlotRenderStyle.ATTACH_TOP_RIGHT;
			} else if(slotX >= gui.getGuiLeft() && slotRight < gui.getGuiLeft() + gui.getXSize()) {
				return SlotRenderStyle.ATTACH_TOP_CENTER;
			}
		}
		if(slotX + SlotRenderStyle.LONE.getWidth() == gui.getGuiLeft()) {
			int slotBottom = slotY + SlotRenderStyle.LONE.getHeight();
			if(slotY == gui.getGuiTop()) {
				return SlotRenderStyle.ATTACH_LEFT_TOP;
			} else if(slotBottom == gui.getGuiTop() + gui.getYSize()) {
				return SlotRenderStyle.ATTACH_LEFT_BOTTOM;
			} else if(slotY >= gui.getGuiTop() && slotBottom < gui.getGuiTop() + gui.getYSize()) {
				return SlotRenderStyle.ATTACH_LEFT_CENTER;
			}
		}
		if(slotX == gui.getGuiLeft() + gui.getXSize()) {
			int slotBottom = slotY + SlotRenderStyle.LONE.getHeight();
			if(slotY == gui.getGuiTop()) {
				return SlotRenderStyle.ATTACH_RIGHT_TOP;
			} else if(slotBottom == gui.getGuiTop() + gui.getYSize()) {
				return SlotRenderStyle.ATTACH_RIGHT_BOTTOM;
			} else if(slotY >= gui.getGuiTop() && slotBottom < gui.getGuiTop() + gui.getYSize()) {
				return SlotRenderStyle.ATTACH_RIGHT_CENTER;
			}
		}
		return SlotRenderStyle.LONE;
	}

	@Override
	public int getDefaultSlotX(AbstractContainerScreen<?> gui) {
		return gui.getXSize() / 2 - SlotRenderStyle.LONE.getWidth();
	}

	@Override
	public int getDefaultSlotY(AbstractContainerScreen<?> gui) {
		return gui.getYSize() / 2;
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
