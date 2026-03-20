package net.blay09.mods.trashslot.client;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.blay09.mods.trashslot.TrashSlot;
import net.blay09.mods.trashslot.api.layout.TrashContainerLayout;
import net.blay09.mods.trashslot.api.layout.TrashSlotAvailability;
import net.blay09.mods.trashslot.data.DataDrivenTrashContainerLayout;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TrashContainerLayoutReloadListener extends SimplePreparableReloadListener<Map<Identifier, DataDrivenTrashContainerLayout>> {

    private static final FileToIdConverter LAYOUT_FILE_CONVERTER = FileToIdConverter.json("trashslot/container_layout");

    @Override
    protected Map<Identifier, DataDrivenTrashContainerLayout> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        final var layouts = new HashMap<Identifier, DataDrivenTrashContainerLayout>();
        for (final var entry : LAYOUT_FILE_CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            final var resourceId = entry.getKey();
            final var layoutId = LAYOUT_FILE_CONVERTER.fileToId(resourceId);
            try (final var reader = entry.getValue().openAsReader()) {
                final var layout = DataDrivenTrashContainerLayout.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader))
                        .getOrThrow(JsonParseException::new);
                layouts.put(layoutId, layout);
            } catch (Exception e) {
                TrashSlot.logger.error("Failed to load container layout {} from {}", layoutId, resourceId, e);
            }
        }
        return layouts;
    }

    @Override
    protected void apply(Map<Identifier, DataDrivenTrashContainerLayout> layoutDatas, ResourceManager resourceManager, ProfilerFiller profiler) {
        final var layouts = new HashMap<Identifier, TrashContainerLayout>();
        var defaultLayoutData = layoutDatas.get(TrashContainerLayoutManager.DEFAULT_LAYOUT);
        if (defaultLayoutData == null) {
            defaultLayoutData = new DataDrivenTrashContainerLayout(TrashSlotAvailability.NEVER, Optional.empty(), Optional.empty(), Collections.emptyMap(), Optional.empty());
        }
        for (final var entry : layoutDatas.entrySet()) {
            layouts.put(entry.getKey(), entry.getValue().createLayout(defaultLayoutData));
        }
        TrashContainerLayoutManager.replaceLayouts(layouts);
    }
}
