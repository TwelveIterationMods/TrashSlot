package net.blay09.mods.trashslot.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class RenderHandler {

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        event.getMap().registerSprite(resourceManager, new ResourceLocation("trashslot", "item/trashcan"));
    }

}
