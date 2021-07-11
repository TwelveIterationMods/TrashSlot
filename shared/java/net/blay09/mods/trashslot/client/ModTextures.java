package net.blay09.mods.trashslot.client;

import net.blay09.mods.balm.client.rendering.BalmTextures;
import net.blay09.mods.trashslot.TrashSlot;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class ModTextures extends BalmTextures {

    public static void initialize() {
        BalmTextures.addSprite(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(TrashSlot.MOD_ID, "gui/trashcan"));
    }

}
