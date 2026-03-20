package net.blay09.mods.trashslot.api;

import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.minecraft.resources.Identifier;

public interface InternalMethods {
    void registerLayout(Identifier identifier, TrashContainerLayout layout);

    TrashContainerLayout getLayout(Identifier identifier);
}
