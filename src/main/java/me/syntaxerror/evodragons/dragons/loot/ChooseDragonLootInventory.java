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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.logging.Logger;

public class ChooseDragonLootInventory extends ScrollableInventory implements Listener {

    private static final int CLOSE_SLOT = 48;
    private static final int ADD_LOOT_SLOT = 49;
    private final String dragonKey;

    public ChooseDragonLootInventory(Player player, String dragonKey){
        super(player,
                ChatColor.valueOf(EvoDragons.getInstance().getConfig().getString(Util.DRAGONS_CONFIG_PATH + "." + dragonKey + ".name_color")) +
                        EvoDragons.getInstance().getConfig().getString(Util.DRAGONS_CONFIG_PATH + "." + dragonKey + ".name") +
                        ChatColor.DARK_GRAY + " Loot:");
        this.dragonKey = dragonKey;
        showInventory();
    }

    @Override
    public void addInventorySpecifics() {
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName("Close");
        closeItem.setItemMeta(closeMeta);
        getInventory().setItem(CLOSE_SLOT, closeItem);

        ItemStack addLoot = new ItemStack(Material.LIME_WOOL);
        ItemMeta addLootMeta = addLoot.getItemMeta();
        addLootMeta.setDisplayName("Add Loot");
        addLoot.setItemMeta(addLootMeta);
        getInventory().setItem(ADD_LOOT_SLOT, addLoot);

        List<ItemStack> loot = LootConfigurationFile.getLootItems(dragonKey);
        for(int slot = 0; slot < 45; slot++){
            int lootKeyIndex = slot + (getPage() - 1) * 45;
            if(lootKeyIndex >= loot.size())
                return;
            getInventory().setItem(slot, loot.get(lootKeyIndex));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player))
            return;
        Player player = (Player) event.getWhoClicked();
        if(!getOwner().equals(player))
            return;
        if(!getInventory().equals(event.getClickedInventory()))
            return;

        event.setCancelled(true);

        if(event.getCurrentItem() != null && event.getSlot() < ARROW_PREVIOUS_SLOT){
            new EditLootInventory(player, dragonKey, String.valueOf(event.getSlot() + (getPage() - 1) * 45));
        }
        else{
            switch(event.getSlot()){
                case ADD_LOOT_SLOT:
                    new EditLootInventory(player, dragonKey, null);
                    return;
                case CLOSE_SLOT:
                    getOwner().closeInventory();
                    new ChooseDragonTypeLootInventory(getOwner());
            }
        }
    }
}
