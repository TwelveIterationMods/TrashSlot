package net.blay09.mods.trashslot.api.event;

import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.balm.platform.event.EventMapper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public abstract class ItemTrashedEvent {
    private final Player player;
    private final ItemStack itemStack;

    public ItemTrashedEvent(Player player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public static class Pre extends ItemTrashedEvent {
        public static final BidirectionalEventMapper<Consumer<Pre>> EVENT = EventMapper.createBound(Pre.class);
        private boolean canceled;

        public Pre(Player player, ItemStack itemStack) {
            super(player, itemStack);
        }

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }
    }

    public static class Post extends ItemTrashedEvent {
        public static final BidirectionalEventMapper<Consumer<ItemTrashedEvent.Post>> EVENT = EventMapper.createBound(ItemTrashedEvent.Post.class);

        public Post(Player player, ItemStack itemStack) {
            super(player, itemStack);
        }
    }
}