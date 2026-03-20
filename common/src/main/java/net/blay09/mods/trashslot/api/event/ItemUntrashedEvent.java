package net.blay09.mods.trashslot.api.event;

import net.blay09.mods.balm.platform.event.BidirectionalEventMapper;
import net.blay09.mods.balm.platform.event.EventMapper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ItemUntrashedEvent {

    public static final BidirectionalEventMapper<Consumer<ItemUntrashedEvent>> EVENT = EventMapper.createBound(ItemUntrashedEvent.class);

    private final Player player;
    private final ItemStack itemStack;

    public ItemUntrashedEvent(Player player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}