package net.blay09.mods.trashslot.client;

import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = {Dist.CLIENT}, modid = TrashSlot.MOD_ID)
public class KeyBindings {

    public static final KeyBinding keyBindToggleSlot = new KeyBinding("key.trashslot.toggle", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.Type.KEYSYM.getOrMakeInput(GLFW.GLFW_KEY_T), "key.categories.trashslot");
    public static final KeyBinding keyBindDelete = new KeyBinding("key.trashslot.delete", KeyConflictContext.GUI, KeyModifier.NONE, InputMappings.Type.KEYSYM.getOrMakeInput(GLFW.GLFW_KEY_DELETE), "key.categories.trashslot");
    public static final KeyBinding keyBindDeleteAll = new KeyBinding("key.trashslot.deleteAll", KeyConflictContext.GUI, KeyModifier.SHIFT, InputMappings.Type.KEYSYM.getOrMakeInput(GLFW.GLFW_KEY_DELETE), "key.categories.trashslot");

    public static void init() {
        ClientRegistry.registerKeyBinding(keyBindToggleSlot);
        ClientRegistry.registerKeyBinding(keyBindDelete);
        ClientRegistry.registerKeyBinding(keyBindDeleteAll);
    }

}
