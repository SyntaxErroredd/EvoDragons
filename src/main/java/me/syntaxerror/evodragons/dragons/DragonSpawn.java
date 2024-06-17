package me.syntaxerror.evodragons.dragons;

import me.syntaxerror.evodragons.EvoDragons;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class DragonSpawn implements Listener {

    @EventHandler
    public void onDragonSpawn(EntitySpawnEvent event){
        // Start a Dragon Battle if an Ender Dragon spawns.
        if(!(event.getEntity() instanceof EnderDragon))
            return;
        new DragonBattle((EnderDragon) event.getEntity(), true);
    }

    public static void checkAllDragons(){
        // Create a Dragon Battle for every Ender Dragon already in a Dragon Battle before a server reload.
        for(World world : Bukkit.getWorlds()){
            for(EnderDragon enderDragon : world.getEntitiesByClass(EnderDragon.class)){
                FileConfiguration config = EvoDragons.getInstance().getConfig();
                for(String dragonKey : config.getConfigurationSection("dragons").getKeys(false)) {
                    if(enderDragon.getCustomName() != null && enderDragon.getCustomName().contains(config.getString("dragons." + dragonKey + ".name"))){
                        new DragonBattle(enderDragon, false);
                    }
                }
            }
        }
    }
}
