package net.blay09.mods.trashslot.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static net.blay09.mods.trashslot.TrashSlot.id;

public class ModItemTags {
    public static final TagKey<Item> CANNOT_DELETE = TagKey.create(Registries.ITEM, id("cannot_delete"));
}
