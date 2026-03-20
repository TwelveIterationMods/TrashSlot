package net.blay09.mods.trashslot;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerInfoProvider;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerRegistrar;
import net.blay09.mods.balm.platform.event.callback.ServerPlayerCallback;
import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.blay09.mods.trashslot.client.TrashSlotGuiHandler;
import net.blay09.mods.trashslot.network.MessageTrashSlotContent;
import net.blay09.mods.trashslot.network.ModNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class TrashSlot {

    public static final Logger logger = LogManager.getLogger();

    public static final String MOD_ID = "trashslot";
    public static boolean isServerSideInstalled;

    public static void initialize(BalmRegistrars registrars) {
        TrashSlotAPI.__setupAPI(new InternalMethodsImpl());

        TrashSlotConfig.initialize();
        ModNetworking.initialize(Balm.networking());

        ServerPlayerCallback.Join.EVENT.register(player -> {
            TrashHelper.setTrashItem(player, ItemStack.EMPTY);
            Balm.networking().sendTo(player, new MessageTrashSlotContent(ItemStack.EMPTY));
        });

        ServerPlayerCallback.Respawn.EVENT.register((oldPlayer, newPlayer)
                -> {
            TrashHelper.setTrashItem(newPlayer, ItemStack.EMPTY);
            Balm.networking().sendTo(newPlayer, new MessageTrashSlotContent(ItemStack.EMPTY));
        });

        ServerPlayerCallback.OpenMenu.EVENT.register((player, menu) -> {
            ItemStack trashItem = TrashHelper.getTrashItem(player);
            Balm.networking().sendTo(player, new MessageTrashSlotContent(trashItem));
        });

        Balm.modSupport().recipeViewers().register(Identifier.fromNamespaceAndPath("trashslot", "occlusions"), new RecipeViewerInfoProvider() {
            @Override
            public void initialize(RecipeViewerRegistrar recipeViewerRegistrar) {
                recipeViewerRegistrar.registerGlobalScreenOcclusion(containerScreen -> {
                    final var trashSlot = TrashSlotGuiHandler.getTrashSlotComponent();
                    return trashSlot != null && trashSlot.isVisible() ? List.of(trashSlot.getRectangle()) : Collections.emptyList();
                });
            }
        });
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
