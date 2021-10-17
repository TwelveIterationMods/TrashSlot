package net.blay09.mods.trashslot.client;

import net.blay09.mods.balm.api.client.keymappings.BalmKeyMappings;
import net.blay09.mods.balm.api.client.keymappings.KeyConflictContext;
import net.blay09.mods.balm.api.client.keymappings.KeyModifier;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {

    public static KeyMapping keyBindToggleSlot;
    public static KeyMapping keyBindDelete;
    public static KeyMapping keyBindDeleteAll;

    public static void initialize(BalmKeyMappings keyMappings) {
        keyBindToggleSlot = keyMappings.registerKeyMapping("key.trashslot.toggle", KeyConflictContext.GUI, KeyModifier.NONE, GLFW.GLFW_KEY_T, "key.categories.trashslot");
        keyBindDelete = keyMappings.registerKeyMapping("key.trashslot.delete", KeyConflictContext.GUI, KeyModifier.NONE, GLFW.GLFW_KEY_DELETE, "key.categories.trashslot");
        keyBindDeleteAll = keyMappings.registerKeyMapping("key.trashslot.deleteAll", KeyConflictContext.GUI, KeyModifier.SHIFT, GLFW.GLFW_KEY_DELETE, "key.categories.trashslot");
    }

}
