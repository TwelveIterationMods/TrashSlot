package net.blay09.mods.trashslot;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.blay09.mods.trashslot.net.NetworkHandler;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = TrashSlot.MOD_ID, name = "TrashSlot")
public class TrashSlot {

    public static final String MOD_ID = "trashslot";

    public static boolean drawSlotBackground;
    public static boolean enableDeleteKey;
    public static boolean trashSlotRelative;
    public static float trashSlotX;
    public static float trashSlotY;

    @Mod.Instance
    public static TrashSlot instance;

    @SidedProxy(serverSide = "net.blay09.mods.trashslot.CommonProxy", clientSide = "net.blay09.mods.trashslot.client.ClientProxy")
    public static CommonProxy proxy;

    private Configuration config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        drawSlotBackground = config.getBoolean("drawSlotBackground", "general", true, "Set this to false if you don't want the trash can icon to be rendered inside the trash slot.");
        enableDeleteKey = config.getBoolean("enableDeleteKey", "general", true, "Set this to false if you don't want the delete key to delete the item below the mouse cursor.");
        trashSlotRelative = config.getBoolean("trashSlotRelative", "general", false, "Set this to true if you want the position of the trash slot to be relative to the game window.");
        if(config.hasKey("general", "trashSlotX")) {
            trashSlotX = config.getFloat("trashSlotX", "general", 0f, 0f, 1f, "The absolute or relative x position of the trash slot.");
        } else {
            trashSlotX = -1;
        }
        if(config.hasKey("general", "trashSlotY")) {
            trashSlotY = config.getFloat("trashSlotY", "general", 0f, 0f, 1f, "The absolute or relative y position of the trash slot.");
        } else {
            trashSlotY = -1;
        }
        config.save();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkHandler.init();

        proxy.init(event);
    }

    public static boolean canDropStack(boolean result, int mouseX, int mouseY) {
        return proxy.canDropStack(mouseX, mouseY, result);
    }

    public void saveConfig() {
        config.get("general", "trashSlotX", -1f, "The absolute or relative x position of the trash slot.").set(TrashSlot.trashSlotX);
        config.get("general", "trashSlotY", -1f, "The absolute or relative y position of the trash slot.").set(TrashSlot.trashSlotY);
        config.save();
    }
}
