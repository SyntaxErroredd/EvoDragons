package me.syntaxerror.evodragons.dragons.loot;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChooseDragonLootInventory implements Listener {

    private static final int ADD_LOOT_SLOT = 49;
    private final Inventory inventory;
    private final Player owner;
    private final String dragonKey;

    public ChooseDragonLootInventory(Player player, String dragonKey){
        EvoDragons.getInstance().getServer().getPluginManager().registerEvents(this, EvoDragons.getInstance());
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        String dragonConfig = Util.DRAGONS_CONFIG_PATH + "." + dragonKey + ".";
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.valueOf(config.getString(dragonConfig + "name_color")) +
                config.getString(dragonConfig + "name") +
                ChatColor.DARK_GRAY + " Loot:");
        this.inventory = inventory;
        this.owner = player;
        this.dragonKey = dragonKey;
        showDragonLootInventory();
    }

    private void showDragonLootInventory(){
        // Displays the loot information for the dragon to the player.
        inventory.clear();
        int slot = 0;
        for(ItemStack itemStack : LootConfigurationFile.getLootItems(dragonKey)){
            inventory.setItem(slot, itemStack);
            slot++;
        }

        ItemStack addLoot = new ItemStack(Material.LIME_WOOL);
        ItemMeta addLootMeta = addLoot.getItemMeta();
        addLootMeta.setDisplayName("Add Loot");
        addLoot.setItemMeta(addLootMeta);
        inventory.setItem(ADD_LOOT_SLOT, addLoot);

        owner.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!owner.equals(player))
            return;
        if(!inventory.equals(event.getClickedInventory()))
            return;

        if(event.getSlot() == ADD_LOOT_SLOT){
            new EditLootInventory(player, dragonKey, null);
        }
        else if(event.getCurrentItem() != null){
            new EditLootInventory(player, dragonKey, String.valueOf(event.getSlot()));
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        // Unregisters the ChooseDragonLootInventory if the player is done editing and closes the inventory.
        if(!owner.equals(event.getPlayer()))
            return;
        if(!event.getInventory().equals(inventory))
            return;
        HandlerList.unregisterAll(this);
    }
}
