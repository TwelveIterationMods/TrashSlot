package net.blay09.mods.trashslot;

import net.blay09.mods.trashslot.api.InternalMethods;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.blay09.mods.trashslot.client.TrashContainerLayoutManager;
import net.minecraft.resources.Identifier;

public class InternalMethodsImpl implements InternalMethods {
    @Override
    public void registerLayout(Identifier identifier, TrashContainerLayout layout) {
        TrashContainerLayoutManager.registerLayout(identifier, layout);
    }

    @Override
    public TrashContainerLayout getLayout(Identifier identifier) {
        return TrashContainerLayoutManager.getLayout(identifier);
    }

    @Override
    public TrashContainerLayout getDefaultLayout() {
        return TrashContainerLayoutManager.getDefaultLayout();
    }
}
