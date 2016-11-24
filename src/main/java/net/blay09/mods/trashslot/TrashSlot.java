package net.blay09.mods.trashslot;

import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.blay09.mods.trashslot.net.NetworkHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

@Mod(modid = TrashSlot.MOD_ID, name = "TrashSlot", acceptableRemoteVersions = "*", guiFactory = "net.blay09.mods.trashslot.client.gui.GuiFactory")
public class TrashSlot {

    public static final String MOD_ID = "trashslot";

    public static boolean isServerSideInstalled;

    @Mod.Instance
    public static TrashSlot instance;

    @SidedProxy(serverSide = "net.blay09.mods.trashslot.CommonProxy", clientSide = "net.blay09.mods.trashslot.client.ClientProxy")
    public static CommonProxy proxy;

    public static Configuration config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());

        config.save();

        TrashSlotAPI.__setupAPI(new InternalMethodsImpl());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkHandler.init();

        proxy.init(event);
    }

    @NetworkCheckHandler
    public boolean checkNetwork(Map<String, String> map, Side side) {
        if(side == Side.SERVER) {
            isServerSideInstalled = map.containsKey(TrashSlot.MOD_ID);
        }
        return true;
    }

}
