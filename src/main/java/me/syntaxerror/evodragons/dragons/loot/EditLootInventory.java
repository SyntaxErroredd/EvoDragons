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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.Callable;

public class EditLootInventory implements Listener {

    public static final int LOOT_DISPLAY_SLOT = 13;
    public static final int CLOSE_DISPLAY_SLOT = 18;
    public static final int CHANCE_DISPLAY_SLOT = 22;
    private static final int REMOVE_LOOT_SLOT = 26;
    private final Player owner;
    private final String dragonKey;
    private final Inventory inventory;
    private String lootKey;
    private boolean editingDropChance = false;

    public EditLootInventory(Player player, String dragonKey, String lootKey){
        // Shows the editing loot inventory to the player, where loot stats can be edited.
        EvoDragons.getInstance().getServer().getPluginManager().registerEvents(this, EvoDragons.getInstance());
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        String dragonConfig = Util.DRAGONS_CONFIG_PATH + "." + dragonKey + ".";
        Inventory inventory = Bukkit.createInventory(null, 27, "Edit " +
                ChatColor.valueOf(config.getString(dragonConfig + "name_color")) +
                config.getString(dragonConfig + "name") +
                ChatColor.DARK_GRAY + " Loot:");
        this.owner = player;
        this.dragonKey = dragonKey;
        this.lootKey = lootKey;
        this.inventory = inventory;
        openEditLootInventory();
    }

    public String getDragonKey() {
        return dragonKey;
    }

    public String getLootKey() {
        return lootKey;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Player getOwner(){
        return owner;
    }

    public boolean isEditingDropChance() {
        return editingDropChance;
    }

    public void setEditingDropChance(boolean editingDropChance){
        this.editingDropChance = editingDropChance;
    }

    public void chooseLootItemFromInventory(ItemStack chosenItem){
        // Updates the edit loot inventory and the config with the new loot item.
        inventory.setItem(EditLootInventory.LOOT_DISPLAY_SLOT, chosenItem);

        if(lootKey == null) {
            lootKey = LootConfigurationFile.addItem(chosenItem, dragonKey);

            ItemStack chanceItem = new ItemStack(Material.OAK_SIGN);
            ItemMeta chanceMeta = chanceItem.getItemMeta();
            chanceMeta.setDisplayName("Drop Chance: " + LootConfigurationFile.DEFAULT_CHANCE);
            chanceItem.setItemMeta(chanceMeta);
            inventory.setItem(CHANCE_DISPLAY_SLOT, chanceItem);

            ItemStack removeLoot = new ItemStack(Material.RED_WOOL);
            ItemMeta removeLootMeta = removeLoot.getItemMeta();
            removeLootMeta.setDisplayName("Remove Loot");
            removeLoot.setItemMeta(removeLootMeta);
            inventory.setItem(REMOVE_LOOT_SLOT, removeLoot);
        }
        else {
            LootConfigurationFile.editItem(dragonKey, lootKey, chosenItem, null);
        }
        owner.updateInventory();
    }

    public void chooseDropChance(double chance){
        // Updates the edit loot inventory and the config with the new drop chance.
        ItemStack chanceItem = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = chanceItem.getItemMeta();
        meta.setDisplayName("Drop Chance: " + chance);
        chanceItem.setItemMeta(meta);
        inventory.setItem(CHANCE_DISPLAY_SLOT, chanceItem);
        owner.updateInventory();
        LootConfigurationFile.editItem(dragonKey, lootKey, null, chance);
        editingDropChance = false;
    }

    public void openEditLootInventory(){
        // Reopens the edit loot inventory to the owner.
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName("Close");
        closeItem.setItemMeta(closeMeta);
        inventory.setItem(CLOSE_DISPLAY_SLOT, closeItem);

        if(lootKey != null && !lootKey.isEmpty() && Integer.parseInt(lootKey) <= LootConfigurationFile.getLootItems(dragonKey).size() - 1) {
            inventory.setItem(LOOT_DISPLAY_SLOT, LootConfigurationFile.getLootItems(dragonKey).get(Integer.parseInt(lootKey)));

            ItemStack chanceItem = new ItemStack(Material.OAK_SIGN);
            ItemMeta meta = chanceItem.getItemMeta();
            meta.setDisplayName("Drop Chance: " + LootConfigurationFile.getLootChance(dragonKey, lootKey));
            chanceItem.setItemMeta(meta);
            inventory.setItem(CHANCE_DISPLAY_SLOT, chanceItem);

            ItemStack removeLoot = new ItemStack(Material.RED_WOOL);
            ItemMeta removeLootMeta = removeLoot.getItemMeta();
            removeLootMeta.setDisplayName("Remove Loot");
            removeLoot.setItemMeta(removeLootMeta);
            inventory.setItem(REMOVE_LOOT_SLOT, removeLoot);
        }
        owner.openInventory(inventory);
    }

    public void closeInventory(){
        // Closes the Edit Loot inventory and opens the CHoose Dragon Loot inventory.
        owner.closeInventory();
        new ChooseDragonLootInventory(owner, dragonKey);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player))
            return;
        if(!owner.equals(event.getWhoClicked()))
            return;
        if(!inventory.equals(event.getInventory()))
            return;
        if(event.getCurrentItem() == null)
            return;

        event.setCancelled(true);

        // If the player selects an item from their inventory, update the edit loot inventory and the config based on the clicked item.
        if(event.getClickedInventory().equals(owner.getInventory())){
            if(event.getCurrentItem() == null)
                return;
            chooseLootItemFromInventory(event.getCurrentItem());
        }

        // If the player selects an option in the edit loot inventory.
        else if(event.getClickedInventory().equals(event.getInventory())){
            switch(event.getSlot()){
                case CHANCE_DISPLAY_SLOT:
                    owner.sendMessage(ChatColor.RED + "Enter the new Drop Chance in chat, or type cancel to cancel:");
                    editingDropChance = true;
                    owner.closeInventory();
                    return;
                case REMOVE_LOOT_SLOT:
                    LootConfigurationFile.removeItem(dragonKey, lootKey);
                    new ChooseDragonLootInventory(owner, dragonKey);
                    return;
                case CLOSE_DISPLAY_SLOT:
                    closeInventory();
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if(!owner.equals(event.getPlayer()))
            return;

        // If player is currently editing the drop chance.
        if(editingDropChance){
            if(event.getMessage().equalsIgnoreCase("cancel")){
                editingDropChance = false;
                event.getPlayer().sendMessage(ChatColor.RED + "You are now no longer editing the Drop Chance.");
            }
            else{
                try {
                    double chance = Double.parseDouble(event.getMessage());
                    if (chance < 0 || chance > 1) {
                        event.getPlayer().sendMessage(ChatColor.RED + "Drop Chance must be between 0 and 1!");
                    } else {
                        chooseDropChance(chance);
                        Callable<Void> callable = () -> {
                            openEditLootInventory();
                            return null;
                        };
                        Bukkit.getScheduler().callSyncMethod(EvoDragons.getInstance(), callable);
                    }
                } catch (NumberFormatException e){
                    event.getPlayer().sendMessage(ChatColor.RED + "Drop Chance must be a decimal value between 0 and 1!");
                }
            }
            event.setCancelled(true);
        }
    }
}
