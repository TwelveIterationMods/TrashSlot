package net.blay09.mods.trashslot.api;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISimpleGuiContainerLayout {
	ISimpleGuiContainerLayout addCollisionArea(int x, int y, int width, int height);
	ISimpleGuiContainerLayout addVerticalSnap(int x);
	ISimpleGuiContainerLayout addHorizontalSnap(int y);
	ISimpleGuiContainerLayout addSnappingPoint(int x, int y);
	ISimpleGuiContainerLayout enableDefaultCollision();
	ISimpleGuiContainerLayout enableDefaultSnaps();
	ISimpleGuiContainerLayout setEnabledByDefault();
}
