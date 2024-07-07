package me.syntaxerror.evodragons.dragons.loot;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.ScrollableInventory;
import me.syntaxerror.evodragons.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChooseDragonTypeLootInventory extends ScrollableInventory implements Listener {

    public ChooseDragonTypeLootInventory(Player player){
        super(player, "Choose EvoDragon:");
        showInventory();
    }

    @Override
    public void addInventorySpecifics() {
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        List<String> dragonKeys = new ArrayList<>(config.getConfigurationSection(Util.DRAGONS_CONFIG_PATH).getKeys(false));
        for(int slot = 0; slot < 45; slot++){
            int dragonKeyIndex = slot + (getPage() - 1) * 45;
            if(dragonKeyIndex >= dragonKeys.size())
                return;
            String dragonConfig = Util.DRAGONS_CONFIG_PATH + "." + dragonKeys.get(dragonKeyIndex) + ".";
            ItemStack itemStack = new ItemStack(Material.DRAGON_EGG, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.valueOf(config.getString(dragonConfig + "name_color")) +
                    config.getString(dragonConfig + "name"));
            itemStack.setItemMeta(itemMeta);
            getInventory().setItem(slot, itemStack);
        }
    }

    private String getDragonKey(String name){
        // Returns the dragon key based on the name of the dragon.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        for(String dragonKey : config.getConfigurationSection(Util.DRAGONS_CONFIG_PATH).getKeys(false)){
            String dragonConfig = Util.DRAGONS_CONFIG_PATH + "." + dragonKey + ".";
            if(name.contains(config.getString(dragonConfig + "name")))
                return dragonKey;
        }
        return null;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player))
            return;
        if(!getOwner().equals(event.getWhoClicked()))
            return;
        if(!getInventory().equals(event.getClickedInventory()))
            return;
        if(event.getCurrentItem() == null)
            return;
        if(event.getCurrentItem().getType() != Material.DRAGON_EGG)
            return;
        event.setCancelled(true);
        String dragonKey = getDragonKey(event.getCurrentItem().getItemMeta().getDisplayName());
        if(dragonKey == null)
            return;
        new ChooseDragonLootInventory(getOwner(), dragonKey);
    }
}
