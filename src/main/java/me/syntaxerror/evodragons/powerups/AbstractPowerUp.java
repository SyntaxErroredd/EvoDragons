package me.syntaxerror.evodragons.powerups;

import com.google.common.base.CaseFormat;
import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractPowerUp implements Listener {

    private final EvoDragon evoDragon;
    private final PowerUpType powerUpType;
    private final String powerUpString;
    private final List<Item> items = new ArrayList<>();
    private final List<Player> poweredUp = new ArrayList<>();

    public AbstractPowerUp(EvoDragon evoDragon, PowerUpType powerUpType){
        this.evoDragon = evoDragon;
        this.powerUpType = powerUpType;
        this.powerUpString = "power_ups." + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, powerUpType.name()) + ".";
    }

    public EvoDragon getEvoDragon() {
        return evoDragon;
    }

    public List<Player> getPoweredUp() {
        return poweredUp;
    }

    public void spawnPowerUpNearPlayer(Player player){
        // Spawns a Power Up at a random location around the specified player.
        Location spawnLoc = pickSpawnLocation(player, 25);
        if(spawnLoc == null)
            return;
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        String name = config.getString(powerUpString + "name");
        Item item = player.getWorld().dropItem(spawnLoc, new ItemStack(powerUpType.getMaterial()));
        item.setGlowing(true);
        item.setGravity(false);
        item.setVelocity(new Vector(0, 0, 0));
        item.setCustomName(ChatColor.YELLOW + name);
        item.setCustomNameVisible(true);
        items.add(item);
        player.sendMessage(ChatColor.GRAY + config.getString("power_up_spawn_quote"));
    }

    private Location pickSpawnLocation(Player player, int tryNumber){
        // Tries a number of times to get a random location around the player where the location can be accessed without block breaking.
        if(tryNumber == 0)
            return null;
        Random random = new Random();
        Location location = player.getLocation().add(random.nextInt(10) - 5, 1.5, random.nextInt(10) - 5);
        if(!location.getBlock().isPassable())
            return pickSpawnLocation(player, tryNumber - 1);
        else{
            return location;
        }
    }

    private void registerPlayer(Player player){
        // Add player to the poweredUp list and after the time stated in the config, remove the player.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        double seconds = config.getDouble(powerUpString + "seconds");
        poweredUp.add(player);
        player.sendMessage(ChatColor.GRAY + replacePowerUpPlaceholders(config.getString("power_up_activate_quote")));

        new BukkitRunnable(){
            @Override
            public void run() {
                poweredUp.remove(player);
                player.sendMessage(ChatColor.GRAY + replacePowerUpPlaceholders(config.getString("power_up_expire_quote")));
            }
        }.runTaskLater(EvoDragons.getInstance(), (long) seconds * Util.TICKS_PER_SECOND);
    }

    private String replacePowerUpPlaceholders(String message){
        // Replaces all Power Up related placeholders in config messages to be sent to players.
        String name = EvoDragons.getInstance().getConfig().getString(powerUpString + "name");
        if(message.contains("<power_up_name>") && name != null){
            message = message.replaceAll("<power_up_name>", ChatColor.YELLOW + name + ChatColor.GRAY);
        }
        return message;
    }

    public void endPowerUp(){
        // Removes all items related to the Power Up.
        for(Item item : items){
            item.remove();
        }
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent event){
        // Register the player under the Power Up.
        if(!items.contains(event.getItem()))
            return;
        event.setCancelled(true);
        if(!(event.getEntity() instanceof Player))
            return;
        event.getItem().getWorld().spawnParticle(Particle.TOTEM, event.getItem().getLocation(), 100);
        registerPlayer((Player) event.getEntity());
        items.remove(event.getItem());
        event.getItem().remove();
    }
}
