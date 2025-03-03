package com.level500.shellsaga.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

/**
 * ConfigManager loads config JSON files from:
 *  - gearSlots.json (for gear slot names)
 *  - rarityConfig.json (for rarity data: multiplier, upgradeChance, nextTier)
 *
 * Example rarityConfig.json structure:
 * {
 *   "Common": {
 *     "multiplier": 1.0,
 *     "upgradeChance": 0.10,
 *     "nextTier": "Uncommon"
 *   },
 *   "Uncommon": {
 *     "multiplier": 1.1,
 *     "upgradeChance": 0.07,
 *     "nextTier": "Rare"
 *   },
 *   ...
 * }
 */
public class ConfigManager {

    // gearSlotNames: loads from gearSlots.json (simple map: { "EXECUTION_MODULE": "Execution Module", ... })
    private static Map<String, String> gearSlotNames = new HashMap<>();

    // rarityData: loads from rarityConfig.json (nested map structure: rarity -> { "multiplier": x, "upgradeChance": y, "nextTier": z })
    private static Map<String, Map<String, Object>> rarityData = new HashMap<>();

    static {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // ================== Load gearSlots.json ==================
            try (InputStream gearStream = ConfigManager.class.getClassLoader()
                    .getResourceAsStream("config/gearSlots.json")) {
                if (gearStream == null) {
                    throw new RuntimeException("gearSlots.json not found in resources!");
                }
                String gearJson = new Scanner(gearStream).useDelimiter("\\A").next();
                // parse as Map<String, String>
                gearSlotNames = objectMapper.readValue(gearJson, new TypeReference<>() {});
            }

            // ================== Load rarityConfig.json ==================
            try (InputStream rarityStream = ConfigManager.class.getClassLoader()
                    .getResourceAsStream("config/rarityConfig.json")) {
                if (rarityStream == null) {
                    throw new RuntimeException("rarityConfig.json not found in resources!");
                }
                String rarityJson = new Scanner(rarityStream).useDelimiter("\\A").next();
                // parse as Map<String, Map<String, Object>>
                rarityData = objectMapper.readValue(rarityJson, new TypeReference<>() {});
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON configuration files: " + e.getMessage(), e);
        }
    }

    /**
     * Looks up a gear slot name from gearSlots.json
     * e.g., "EXECUTION_MODULE" -> "Execution Module"
     */
    public static String getConfigValue(String key) {
        return gearSlotNames.getOrDefault(key, "Unknown Slot");
    }

    /**
     * Returns the multiplier for a given rarity, e.g. "Common" -> 1.0, "Rare" -> 1.2, etc.
     */
    public static double getRarityMultiplier(String rarity) {
        Map<String, Object> data = rarityData.get(rarity);
        if (data == null) {
            // fallback if the rarity isn't found
            return 1.0;
        }
        Object multObj = data.get("multiplier");
        if (multObj instanceof Number) {
            return ((Number) multObj).doubleValue();
        }
        return 1.0;
    }

    /**
     * Returns the chance to upgrade from this rarity to the next, e.g. "Common" -> 0.10
     */
    public static double getUpgradeChance(String rarity) {
        Map<String, Object> data = rarityData.get(rarity);
        if (data == null) {
            return 0.0;
        }
        Object chanceObj = data.get("upgradeChance");
        if (chanceObj instanceof Number) {
            return ((Number) chanceObj).doubleValue();
        }
        return 0.0;
    }

    /**
     * Returns the next rarity tier, e.g. "Common" -> "Uncommon", "Rare" -> "Epic"
     */
    public static String getNextTier(String rarity) {
        Map<String, Object> data = rarityData.get(rarity);
        if (data == null) {
            return rarity; // fallback, no upgrade
        }
        Object nextObj = data.get("nextTier");
        if (nextObj instanceof String) {
            return (String) nextObj;
        }
        return rarity; // fallback
    }

    /**
     * Old method: If you want to still keep getNextRarity() for backward compatibility
     * or rename it to getNextTier() if you prefer.
     * This one can simply call getNextTier(rarity).
     */
    public static String getNextRarity(String currentRarity) {
        return getNextTier(currentRarity);
    }

}
