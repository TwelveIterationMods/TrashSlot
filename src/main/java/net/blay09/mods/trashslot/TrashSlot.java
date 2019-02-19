package net.blay09.mods.trashslot;

import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.blay09.mods.trashslot.client.KeyBindings;
import net.blay09.mods.trashslot.client.TrashSlotGui;
import net.blay09.mods.trashslot.client.gui.layout.ChestContainerLayout;
import net.blay09.mods.trashslot.client.gui.layout.SimpleGuiContainerLayout;
import net.blay09.mods.trashslot.net.MessageTrashSlotContent;
import net.blay09.mods.trashslot.net.NetworkHandler;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Optional;

@Mod(TrashSlot.MOD_ID)
public class TrashSlot {

    public static final String MOD_ID = "trashslot";
    public static boolean isServerSideInstalled;

    public static Optional<TrashSlotGui> trashSlotGui = Optional.empty();

    public TrashSlot() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TrashSlotConfig.clientSpec);
    }

    private void setupCommon(final FMLCommonSetupEvent event) {
        TrashSlotAPI.__setupAPI(new InternalMethodsImpl());

        DeferredWorkQueue.runLater(NetworkHandler::init);
    }

    private void setupClient(final FMLClientSetupEvent event) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            TrashSlotAPI.registerLayout(GuiInventory.class, SimpleGuiContainerLayout.DEFAULT_ENABLED);
            TrashSlotAPI.registerLayout(GuiCrafting.class, SimpleGuiContainerLayout.DEFAULT_ENABLED);
            TrashSlotAPI.registerLayout(GuiChest.class, new ChestContainerLayout());

            trashSlotGui = Optional.of(new TrashSlotGui());

            DeferredWorkQueue.runLater(() -> {
                trashSlotGui.ifPresent(MinecraftForge.EVENT_BUS::register);
                KeyBindings.init();
            });
        });
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        MessageTrashSlotContent message = new MessageTrashSlotContent(ItemStack.EMPTY);
        NetworkHandler.instance.send(PacketDistributor.PLAYER.with(() -> (EntityPlayerMP) event.getPlayer()), message);
    }

    @SubscribeEvent
    public void onPlayerOpenContainer(PlayerContainerEvent.Open event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player instanceof EntityPlayerMP) {
            ItemStack trashItem = TrashHelper.getTrashItem(player);
            NetworkHandler.instance.sendTo(new MessageTrashSlotContent(trashItem), ((EntityPlayerMP) player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
