package me.syntaxerror.evodragons.dragons.loot;

import me.syntaxerror.evodragons.EvoDragons;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LootConfigurationFile {

    public static double DEFAULT_CHANCE = 1.0;
    private static File configFile;
    private static FileConfiguration config;

    public static void loadConfig(){
        // Loads in the custom_drops.yml config file. Creates one if there isn't one.
        File customDrops = new File(EvoDragons.getInstance().getDataFolder(), "custom_drops.yml");
        if(!customDrops.exists()){
            try {
                customDrops.createNewFile();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        configFile = customDrops;
        config = YamlConfiguration.loadConfiguration(customDrops);
    }

    public static String addItem(ItemStack itemStack, String dragonKey){
        // Adds an ItemStack to the config, in key dragonKey. Returns the added lootKey.
        ConfigurationSection section = config.getConfigurationSection(dragonKey);
        String lootKey;
        if(section == null){
            lootKey = "0";
        }
        else {
            lootKey = String.valueOf(section.getKeys(false).size());
        }
        config.set(dragonKey + "." + lootKey + ".item_stack", itemStack);
        config.set(dragonKey + "." + lootKey + ".chance", DEFAULT_CHANCE);
        saveConfig();
        return lootKey;
    }

    public static void removeItem(String dragonKey, String lootKey){
        // Removes an item from the config based on its dragon key and save key.
        config.set(dragonKey + "." + lootKey, null);
        saveConfig();
    }

    public static void editItem(String dragonKey, String lootKey, ItemStack newItem, Double chance){
        // Changes the config saved item to the new provided item based on the dragonKey and lootKey.
        // If null is passed, do not edit that data category.
        if(newItem != null) {
            config.set(dragonKey + "." + lootKey + ".item_stack", newItem);
        }
        if(chance != null) {
            config.set(dragonKey + "." + lootKey + ".chance", chance);
        }
        saveConfig();
    }

    public static List<ItemStack> getLootItems(String dragonKey){
        // Returns a list of all ItemStacks for the specified dragon.
        List<ItemStack> loot = new ArrayList<>();
        if(config.getConfigurationSection(dragonKey) != null) {
            for (String lootKey : config.getConfigurationSection(dragonKey).getKeys(false)) {
                loot.add(config.getItemStack(dragonKey + "." + lootKey + ".item_stack"));
            }
        }
        return loot;
    }

    public static double getLootChance(String dragonKey, String lootKey){
        // Returns the double probability of the loot dropped on a dragon's death.
        return config.getDouble(dragonKey + "." + lootKey + ".chance");
    }

    public static List<ItemStack> rollLoot(String dragonKey){
        // Returns a list of ItemStacks that can be dropped after taking drop chance into consideration.
        List<ItemStack> loot = new ArrayList<>();
        Random random = new Random();
        if(config.getConfigurationSection(dragonKey) != null) {
            for (String lootKey : config.getConfigurationSection(dragonKey).getKeys(false)) {
                String lootPath = dragonKey + "." + lootKey + ".";
                double chance = config.getDouble(lootPath + "chance");
                if (chance == 0)
                    continue;
                if (random.nextDouble() > chance)
                    continue;
                loot.add(config.getItemStack(lootPath + "item_stack"));
            }
        }
        return loot;
    }

    private static void saveConfig(){
        // Saves the config file.
        try {
            config.save(configFile);
        } catch(Exception e){
            e.printStackTrace();
        }
        loadConfig();
    }
}
