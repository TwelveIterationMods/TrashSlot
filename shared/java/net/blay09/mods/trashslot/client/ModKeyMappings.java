package net.blay09.mods.trashslot.client;

import net.blay09.mods.balm.client.keybinds.BalmKeyMappings;
import net.blay09.mods.balm.client.keybinds.KeyConflictContext;
import net.blay09.mods.balm.client.keybinds.KeyModifier;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings extends BalmKeyMappings {

    public static KeyMapping keyBindToggleSlot;
    public static KeyMapping keyBindDelete;
    public static KeyMapping keyBindDeleteAll;

    public static void initialize() {
        keyBindToggleSlot = registerKeyMapping("key.trashslot.toggle", KeyConflictContext.GUI, KeyModifier.NONE, GLFW.GLFW_KEY_T, "key.categories.trashslot");
        keyBindDelete = registerKeyMapping("key.trashslot.delete", KeyConflictContext.GUI, KeyModifier.NONE, GLFW.GLFW_KEY_DELETE, "key.categories.trashslot");
        keyBindDeleteAll = registerKeyMapping("key.trashslot.deleteAll", KeyConflictContext.GUI, KeyModifier.SHIFT, GLFW.GLFW_KEY_DELETE, "key.categories.trashslot");
    }

}
