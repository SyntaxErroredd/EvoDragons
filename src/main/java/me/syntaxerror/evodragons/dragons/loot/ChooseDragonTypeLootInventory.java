package me.syntaxerror.evodragons.dragons.loot;

import me.syntaxerror.evodragons.EvoDragons;
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

public class ChooseDragonTypeLootInventory implements Listener {

    private final Inventory inventory;
    private final Player owner;

    public ChooseDragonTypeLootInventory(Player player){
        EvoDragons.getInstance().getServer().getPluginManager().registerEvents(this, EvoDragons.getInstance());
        this.owner = player;
        Inventory inventory = Bukkit.createInventory(null, 54, "Choose EvoDragon:");
        this.inventory = inventory;
        showDragonInventory();
    }

    private void showDragonInventory(){
        // Displays the choose dragon inventory to the player.
        inventory.clear();
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        int slot = 0;
        for(String dragonKey : config.getConfigurationSection(Util.DRAGONS_CONFIG_PATH).getKeys(false)){
            String dragonConfig = Util.DRAGONS_CONFIG_PATH + "." + dragonKey + ".";
            ItemStack itemStack = new ItemStack(Material.DRAGON_EGG, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.valueOf(config.getString(dragonConfig + "name_color")) +
                    config.getString(dragonConfig + "name"));
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(slot, itemStack);
            slot++;
        }
        owner.openInventory(inventory);
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
        if(!owner.equals(event.getWhoClicked()))
            return;
        if(!inventory.equals(event.getClickedInventory()))
            return;
        if(event.getCurrentItem() == null)
            return;
        if(event.getCurrentItem().getType() != Material.DRAGON_EGG)
            return;
        event.setCancelled(true);
        String dragonKey = getDragonKey(event.getCurrentItem().getItemMeta().getDisplayName());
        if(dragonKey == null)
            return;
        new ChooseDragonLootInventory(owner, dragonKey);
    }
}
