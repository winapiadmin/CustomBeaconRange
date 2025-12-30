package com.winapiadmin.custombeaconrange;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.Reader;
import java.io.Writer;

public final class Config {
    public static double Level1Range = 20.0;
    public static double Level2Range = 30.0;
    public static double Level3Range = 40.0;
    public static double Level4Range = 50.0;


    public static final Path PATH =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("custom_beacon_range.json");
    private Config() {}
    private static void writeDefault() {
        try {
            Files.createDirectories(PATH.getParent());

            JsonObject json = new JsonObject();
            json.addProperty("level1", Level1Range);
            json.addProperty("level2", Level2Range);
            json.addProperty("level3", Level3Range);
            json.addProperty("level4", Level4Range);

            try (Writer writer = Files.newBufferedWriter(PATH)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(json, writer);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to write default beacon config", e);
        }
    }

    public static void load() {
        if (!Files.exists(PATH)) {
            writeDefault();
            return;
        }

        try (Reader reader = Files.newBufferedReader(PATH)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            Config.Level1Range = json.get("level1").getAsDouble();
            Config.Level2Range = json.get("level2").getAsDouble();
            Config.Level3Range = json.get("level3").getAsDouble();
            Config.Level4Range = json.get("level4").getAsDouble();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load beacon config", e);
        }
    }
}
