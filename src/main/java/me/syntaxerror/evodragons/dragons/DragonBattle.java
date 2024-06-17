package me.syntaxerror.evodragons.dragons;

import com.google.common.base.CaseFormat;
import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.attacks.AbstractAttack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DragonBattle implements Listener {

    private final List<EvoDragon> evoDragons = new ArrayList<>();

    private boolean ongoing = true;

    public DragonBattle(EnderDragon enderDragon, boolean isNewBattle){
        EvoDragons.getInstance().getServer().getPluginManager().registerEvents(this, EvoDragons.getInstance());
        if(isNewBattle) {
            evoDragons.add(rollDragon(enderDragon));
        }
        else{
            evoDragons.add(new EvoDragon(enderDragon));
        }
        startTimer();
        EvoDragons.addDragonBattle(this);
    }

    private EvoDragon rollDragon(EnderDragon enderDragon){
        // Create an EvoDragon for the Ender Dragon and prepare it.
        double current = 0.0;
        Random random = new Random();
        double roll = random.nextDouble();
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        for(String dragonKey : config.getConfigurationSection(Util.DRAGONS_CONFIG_PATH).getKeys(false)) {
            double dragonSpawnChance = config.getDouble(Util.DRAGONS_CONFIG_PATH + "." + dragonKey + ".spawn_chance");
            current += dragonSpawnChance;
            if(dragonSpawnChance != 0 && roll <= current)
                return new EvoDragon(enderDragon, dragonKey);
        }
        return null;
    }

    private void startTimer(){
        // Manage all time related events to the Dragon Battle here.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        new BukkitRunnable(){
            int i = 0;
            int seconds = 0;
            @Override
            public void run() {
                if(!ongoing)
                    cancel();
                if(evoDragons.stream().allMatch(EvoDragon::isDead)) {
                    cancel();
                    ongoing = false;
                }

                if(i != 0 && i % Util.TICKS_PER_SECOND == 0)
                    seconds++;

                for(EvoDragon evoDragon : evoDragons){
                    if(seconds == 0)
                        break;
                    if(i % Util.TICKS_PER_SECOND != 0)
                        break;
                    if(seconds % config.getInt(evoDragon.getConfigPath() + "seconds_between_attack") == 0)
                        evoDragon.attack();
                    if(seconds % config.getInt(evoDragon.getConfigPath() + "seconds_between_talk") == 0)
                        evoDragon.talk();
                    if(seconds % config.getInt(evoDragon.getConfigPath() + "seconds_between_power_up") == 0)
                        evoDragon.createPowerUps();
                    if(evoDragon.getEnderDragon().isDead() && !evoDragon.isDead()){
                        evoDragon.dragonDeath();
                    }
                }

                i++;
            }
        }.runTaskTimer(EvoDragons.getInstance(), 0L, 1L);
    }

    public void endBattle(){
        // Ends the battle and removes all entities related to it.
        ongoing = false;
        for(EvoDragon evoDragon : evoDragons){
            evoDragon.endDragon();
        }
    }
}
