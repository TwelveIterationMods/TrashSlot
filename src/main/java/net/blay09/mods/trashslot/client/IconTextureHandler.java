package net.blay09.mods.trashslot.client;

import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TrashSlot.MOD_ID, value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IconTextureHandler {

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().getTextureLocation().getPath().equals("textures/atlas/blocks.png")) {
            ResourceLocation trashcanIconLocation = new ResourceLocation("trashslot", "gui/trashcan");
            event.addSprite(trashcanIconLocation);
        }
    }

}
