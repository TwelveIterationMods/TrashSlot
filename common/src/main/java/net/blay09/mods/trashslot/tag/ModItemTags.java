package net.blay09.mods.trashslot.tag;

import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> CANNOT_DELETE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TrashSlot.MOD_ID, "cannot_delete"));
}
