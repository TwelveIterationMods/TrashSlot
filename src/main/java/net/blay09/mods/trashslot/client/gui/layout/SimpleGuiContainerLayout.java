package net.blay09.mods.trashslot.client.gui.layout;

import com.google.common.collect.Lists;
import net.blay09.mods.trashslot.api.IGuiContainerLayout;
import net.blay09.mods.trashslot.api.ISimpleGuiContainerLayout;
import net.blay09.mods.trashslot.api.SlotRenderStyle;
import net.blay09.mods.trashslot.api.Snap;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.awt.Rectangle;
import java.util.List;

public class SimpleGuiContainerLayout implements IGuiContainerLayout, ISimpleGuiContainerLayout {

	public static final IGuiContainerLayout DEFAULT = (IGuiContainerLayout) new SimpleGuiContainerLayout().enableDefaultCollision().enableDefaultSnaps();
	public static final IGuiContainerLayout DEFAULT_ENABLED = (IGuiContainerLayout) new SimpleGuiContainerLayout().enableDefaultCollision().enableDefaultSnaps().setEnabledByDefault();

	private final List<Rectangle> collisionAreas = Lists.newArrayList();
	private final List<Snap> snaps = Lists.newArrayList();
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
	public List<Rectangle> getCollisionAreas(GuiContainer gui) {
		if(!defaultCollision) {
			return collisionAreas;
		}
		List<Rectangle> list = Lists.newArrayList(collisionAreas);
		list.add(new Rectangle(gui.guiLeft, gui.guiTop, gui.xSize, gui.ySize));
		return list;
	}

	@Override
	public List<Snap> getSnaps(GuiContainer gui, SlotRenderStyle renderStyle) {
		if(!defaultSnaps) {
			return snaps;
		}
		List<Snap> list = Lists.newArrayList(snaps);
		list.add(new Snap(Snap.Type.HORIZONTAL, 0, gui.guiTop));
		list.add(new Snap(Snap.Type.HORIZONTAL, 0, gui.guiTop + gui.ySize - renderStyle.getHeight()));
		list.add(new Snap(Snap.Type.VERTICAL, gui.guiLeft, 0));
		list.add(new Snap(Snap.Type.VERTICAL, gui.guiLeft + gui.xSize - renderStyle.getWidth(), 0));
		return list;
	}

	@Override
	public SlotRenderStyle getSlotRenderStyle(GuiContainer gui, int slotX, int slotY) {
		if(slotY == gui.guiTop + gui.ySize) {
			int slotRight = slotX + SlotRenderStyle.LONE.getWidth();
			if(slotX == gui.guiLeft) {
				return SlotRenderStyle.ATTACH_BOTTOM_LEFT;
			} else if(slotRight == gui.guiLeft + gui.xSize) {
				return SlotRenderStyle.ATTACH_BOTTOM_RIGHT;
			} else if(slotX >= gui.guiLeft && slotRight < gui.guiLeft + gui.xSize) {
				return SlotRenderStyle.ATTACH_BOTTOM_CENTER;
			}
		}
		if(slotY + SlotRenderStyle.LONE.getHeight() == gui.guiTop) {
			int slotRight = slotX + SlotRenderStyle.LONE.getWidth();
			if(slotX == gui.guiLeft) {
				return SlotRenderStyle.ATTACH_TOP_LEFT;
			} else if(slotRight == gui.guiLeft + gui.xSize) {
				return SlotRenderStyle.ATTACH_TOP_RIGHT;
			} else if(slotX >= gui.guiLeft && slotRight < gui.guiLeft + gui.xSize) {
				return SlotRenderStyle.ATTACH_TOP_CENTER;
			}
		}
		if(slotX + SlotRenderStyle.LONE.getWidth() == gui.guiLeft) {
			int slotBottom = slotY + SlotRenderStyle.LONE.getHeight();
			if(slotY == gui.guiTop) {
				return SlotRenderStyle.ATTACH_LEFT_TOP;
			} else if(slotBottom == gui.guiTop + gui.ySize) {
				return SlotRenderStyle.ATTACH_LEFT_BOTTOM;
			} else if(slotY >= gui.guiTop && slotBottom < gui.guiTop + gui.ySize) {
				return SlotRenderStyle.ATTACH_LEFT_CENTER;
			}
		}
		if(slotX == gui.guiLeft + gui.xSize) {
			int slotBottom = slotY + SlotRenderStyle.LONE.getHeight();
			if(slotY == gui.guiTop) {
				return SlotRenderStyle.ATTACH_RIGHT_TOP;
			} else if(slotBottom == gui.guiTop + gui.ySize) {
				return SlotRenderStyle.ATTACH_RIGHT_BOTTOM;
			} else if(slotY >= gui.guiTop && slotBottom < gui.guiTop + gui.ySize) {
				return SlotRenderStyle.ATTACH_RIGHT_CENTER;
			}
		}
		return SlotRenderStyle.LONE;
	}

	@Override
	public int getDefaultSlotX(GuiContainer gui) {
		return gui.xSize / 2 - SlotRenderStyle.LONE.getWidth();
	}

	@Override
	public int getDefaultSlotY(GuiContainer gui) {
		return gui.ySize / 2;
	}

	@Override
	public boolean isEnabledByDefault() {
		return enabledByDefault;
	}

	@Override
	public int getSlotOffsetX(GuiContainer gui, SlotRenderStyle renderStyle) {
		return 0;
	}

	@Override
	public int getSlotOffsetY(GuiContainer gui, SlotRenderStyle renderStyle) {
		return 0;
	}

}
