package net.blay09.mods.trashslot.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public class TrashSlotSlot extends Slot {

    public TrashSlotSlot() {
        super(new TrashInventory(), 0, 0, 0);
        // TODO setBackground(new ResourceLocation("minecraft", "textures/atlas/blocks.png"), new ResourceLocation("trashslot", "gui/trashcan"));
    }

}
