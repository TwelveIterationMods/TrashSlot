package net.blay09.mods.trashslot.client;

import net.blay09.mods.trashslot.TrashSlotConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TrashSlotSlot extends Slot {

    private static final Identifier ICON = Identifier.withDefaultNamespace("container/slot/trashslot");
    private static final Identifier DANGER_ICON = Identifier.withDefaultNamespace("container/slot/trashslot_danger");

    public static class TrashInventory implements Container {
        private ItemStack currentStack = ItemStack.EMPTY;

        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return currentStack.isEmpty();
        }

        @Override
        public ItemStack getItem(int index) {
            return currentStack;
        }

        @Override
        public ItemStack removeItem(int index, int count) {
            ItemStack itemStack = !currentStack.isEmpty() && count > 0 ? currentStack.split(count) : ItemStack.EMPTY;
            if (!itemStack.isEmpty()) {
                this.setChanged();
            }
            return itemStack;
        }

        @Override
        public ItemStack removeItemNoUpdate(int index) {
            ItemStack itemStack = currentStack;
            currentStack = ItemStack.EMPTY;
            return itemStack;
        }

        @Override
        public void setItem(int index, ItemStack stack) {
            currentStack = stack;
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void startOpen(ContainerUser user) {
        }

        @Override
        public void stopOpen(ContainerUser user) {
        }

        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
            return true;
        }

        @Override
        public void clearContent() {
            currentStack = ItemStack.EMPTY;
        }
    }

    public TrashSlotSlot() {
        super(new TrashInventory(), 0, 0, 0);
    }

    @Override
    public Identifier getNoItemIcon() {
        final var deletionProvider = TrashSlotConfig.getDeletionProvider();
        return deletionProvider != null && deletionProvider.canUndeleteLast() ? ICON : DANGER_ICON;
    }
}
