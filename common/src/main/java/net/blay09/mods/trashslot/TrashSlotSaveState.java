package net.blay09.mods.trashslot;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.trashslot.api.layout.TrashSlotContainerContext;
import net.blay09.mods.trashslot.client.ContainerSettings;
import net.blay09.mods.trashslot.client.TrashContainerLayoutManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrashSlotSaveState {

    private static final Logger logger = LogManager.getLogger();
    private static final String SETTINGS_FILE = "TrashSlotSaveState.json";
    private static final String DEFAULT_SETTINGS_FILE = "TrashSlotSaveState.default.json";
    public static final MapCodec<TrashSlotSaveState> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.listOf().optionalFieldOf("hintsSeen", java.util.List.of()).forGetter(saveState -> java.util.List.copyOf(saveState.hintsSeen)),
            Codec.unboundedMap(Identifier.CODEC, ContainerSettings.CODEC).optionalFieldOf("settings", Map.of()).forGetter(saveState -> saveState.settings)
    ).apply(instance, TrashSlotSaveState::new));
    public static final Codec<TrashSlotSaveState> CODEC = MAP_CODEC.codec();
    private static TrashSlotSaveState instance;

    private final Set<String> hintsSeen = new HashSet<>();
    private final Map<Identifier, ContainerSettings> settings = new HashMap<>();

    private TrashSlotSaveState() {
    }

    private TrashSlotSaveState(java.util.List<String> hintsSeen, Map<Identifier, ContainerSettings> settings) {
        this.hintsSeen.addAll(hintsSeen);
        this.settings.putAll(settings);
    }

    public static ContainerSettings getSettings(TrashSlotContainerContext context) {
        final var screen = context.screen();
        if (screen instanceof InventoryScreen) {
            return getSettings(TrashContainerLayoutManager.INVENTORY_LAYOUT, context);
        }

        try {
            final var menuTypeId = BuiltInRegistries.MENU.getKey(screen.getMenu().getType());
            return getSettings(menuTypeId, context);
        } catch (Exception e) {
            return getSettings(TrashContainerLayoutManager.DEFAULT_LAYOUT, context);
        }
    }

    public static ContainerSettings getSettings(Identifier identifier, TrashSlotContainerContext context) {
        final var saveState = getOrLoad();
        return saveState.settings.computeIfAbsent(identifier, _ -> {
            final var layout = context.layout();
            final var defaultSnap = layout.getDefaultSnap(context);
            final var defaultSnapId = defaultSnap.flatMap(snap -> layout.getSnaps(context).entrySet().stream().filter(it -> it.getValue().equals(snap)).map(Map.Entry::getKey).findFirst());
            final var screenAccessor = (AbstractContainerScreenAccessor) context.screen();
            final var defaultX = defaultSnap.flatMap(it -> it.x(context, 0))
                    .map(it -> it - screenAccessor.getLeftPos())
                    .orElse(152);
            final var defaultY = defaultSnap.flatMap(it -> it.y(context, 0))
                    .map(it -> it - screenAccessor.getTopPos())
                    .orElse(165);
            return new ContainerSettings(defaultSnapId.orElse(null), defaultX, defaultY, layout.isEnabledByDefault(), false);
        });
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(new File(Minecraft.getInstance().gameDirectory, SETTINGS_FILE))) {
            final var saveState = getOrLoad();
            final var jsonElement = CODEC.encodeStart(JsonOps.INSTANCE, saveState)
                    .getOrThrow(JsonIOException::new);
            writer.write(jsonElement.toString());
        } catch (IOException | JsonIOException e) {
            logger.error("Failed to save TrashSlot save state", e);
        }
    }

    public boolean hasSeenHint(String hint) {
        return hintsSeen.contains(hint);
    }

    public void markHintAsSeen(String hint) {
        hintsSeen.add(hint);
    }

    public static TrashSlotSaveState getOrLoad() {
        if (instance == null) {
            File saveStateFile = new File(Minecraft.getInstance().gameDirectory, SETTINGS_FILE);
            File defaultSaveStateFile = new File(Minecraft.getInstance().gameDirectory, DEFAULT_SETTINGS_FILE);
            if (!saveStateFile.exists() && defaultSaveStateFile.exists()) {
                try {
                    Files.copy(defaultSaveStateFile.toPath(), saveStateFile.toPath());
                } catch (IOException e) {
                    logger.error("Failed to load TrashSlot default save state, will ignore defaults", e);
                }
            }

            if (saveStateFile.exists()) {
                try (FileReader reader = new FileReader(saveStateFile)) {
                    instance = CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader))
                            .getOrThrow(JsonParseException::new);
                } catch (Throwable e) {
                    logger.error("Failed to load TrashSlot save state, resetting to default", e);
                }
            }
        }

        if (instance == null) {
            instance = new TrashSlotSaveState();
        }

        return instance;
    }

}
