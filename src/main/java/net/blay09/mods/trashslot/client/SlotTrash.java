package net.blay09.mods.trashslot.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotTrash extends Slot {

	public static class TrashInventory implements IInventory {
		private ItemStack currentStack = ItemStack.EMPTY;

		@Override
		public int getSizeInventory() {
			return 1;
		}

		@Override
		public boolean isEmpty() {
			return currentStack != null;
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			return currentStack;
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			ItemStack itemStack = !currentStack.isEmpty() && count > 0 ? currentStack.splitStack(count) : ItemStack.EMPTY;
			if (!itemStack.isEmpty()) {
				this.markDirty();
			}
			return itemStack;
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			ItemStack itemStack = currentStack;
			currentStack = ItemStack.EMPTY;
			return itemStack;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			currentStack = stack;
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public void markDirty() {
		}

		@Override
		public boolean isUsableByPlayer(EntityPlayer player) {
			return true;
		}

		@Override
		public void openInventory(EntityPlayer player) {
		}

		@Override
		public void closeInventory(EntityPlayer player) {
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return true;
		}

		@Override
		public int getField(int id) {
			return 0;
		}

		@Override
		public void setField(int id, int value) {
		}

		@Override
		public int getFieldCount() {
			return 0;
		}

		@Override
		public void clear() {
			currentStack = null;
		}

		@Override
		public String getName() {
			return "trash";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public ITextComponent getDisplayName() {
			return new TextComponentString(getName());
		}
	}

	public SlotTrash() {
		super(new TrashInventory(), 0, 0, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getBackgroundSprite() {
		return ClientProxy.trashSlotIcon;
	}

}
