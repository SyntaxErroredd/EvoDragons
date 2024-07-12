package me.syntaxerror.evodragons;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public abstract class ScrollableInventory implements Listener {

    public static final int ARROW_NEXT_SLOT = 53;
    public static final int ARROW_PREVIOUS_SLOT = 45;
    private final Inventory inventory;
    private final Player owner;
    private int page = 1;

    public ScrollableInventory(Player owner, String title){
        EvoDragons.getInstance().getServer().getPluginManager().registerEvents(this, EvoDragons.getInstance());
        this.owner = owner;
        Inventory inventory = Bukkit.createInventory(null, 54, title);
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Player getOwner() {
        return owner;
    }

    public int getPage() {
        return page;
    }

    public void showInventory(){
        // Opens the inventory with arrows and inventory specific items to the owner.
        inventory.clear();

        addInventorySpecifics();

        inventory.setItem(ARROW_NEXT_SLOT, Util.ARROW_NEXT);
        inventory.setItem(ARROW_PREVIOUS_SLOT, Util.ARROW_PREVIOUS);

        owner.openInventory(inventory);
    }

    private void increasePage(){
        // Shows the next page of dragon types.
        page++;
        showInventory();
    }

    private void decreasePage(){
        // Shows the previous page of dragon types.
        if(page <= 1)
            return;
        page--;
        showInventory();
    }

    public abstract void addInventorySpecifics();

    @EventHandler
    public void onAbstractClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player))
            return;
        if(!owner.equals(event.getWhoClicked()))
            return;
        if(!inventory.equals(event.getClickedInventory()))
            return;
        if(event.getCurrentItem() == null)
            return;
        event.setCancelled(true);
        switch(event.getSlot()){
            case ARROW_PREVIOUS_SLOT:
                decreasePage();
                return;
            case ARROW_NEXT_SLOT:
                increasePage();
        }
    }
}
