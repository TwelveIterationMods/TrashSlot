package net.blay09.mods.trashslot.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.kuma.api.*;
import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.resources.ResourceLocation;

public class ModKeyMappings {

    public static ManagedKeyMapping keyBindToggleSlot;
    public static ManagedKeyMapping keyBindToggleSlotLock;
    public static ManagedKeyMapping keyBindDelete;
    public static ManagedKeyMapping keyBindDeleteAll;

    public static void initialize() {
        keyBindToggleSlot = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(TrashSlot.MOD_ID, "toggle"))
                .withDefault(InputBinding.key(InputConstants.KEY_T))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyBindToggleSlotLock = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(TrashSlot.MOD_ID, "toggle_lock"))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyBindDelete = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(TrashSlot.MOD_ID, "delete"))
                .withDefault(InputBinding.key(InputConstants.KEY_DELETE))
                .withContext(KeyConflictContext.SCREEN)
                .build();

        keyBindDeleteAll = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath(TrashSlot.MOD_ID, "delete_all"))
                .withDefault(InputBinding.key(InputConstants.KEY_DELETE, KeyModifiers.of(KeyModifier.SHIFT)))
                .withContext(KeyConflictContext.SCREEN)
                .build();
    }

}
