package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnderSpiritAttack extends AbstractAttack implements Listener {

    private static final String ATTACK_STRING = "attacks.ender_spirit_attack.";
    private final List<Vex> spirits = new ArrayList<>();

    public EnderSpiritAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.ENDER_SPIRIT);
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Summons a number of Ender Spirits to attack players based on the config.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        int number = config.getInt(ATTACK_STRING + "number");
        double health = config.getDouble(ATTACK_STRING + "health");
        String enderSpiritName = config.getString(ATTACK_STRING + "name");
        Random random = new Random();
        for(int i = 0; i < number; i++){
            Vex vex = (Vex) player.getWorld().spawnEntity(player.getLocation().add(random.nextInt(6) - 3, random.nextInt(6) - 3, random.nextInt(6) - 3), EntityType.VEX);
            vex.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            vex.setHealth(health);
            vex.setCustomName(ChatColor.DARK_PURPLE + enderSpiritName);
            vex.setCustomNameVisible(true);
            spirits.add(vex);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        // Set the Ender Spirit's damage to as specified in the config.
        if(!(event.getDamager() instanceof Vex))
            return;
        if(!spirits.contains((Vex) event.getDamager()))
            return;
        event.setDamage(EvoDragons.getInstance().getConfig().getDouble(ATTACK_STRING + "damage"));
    }

    public void endAttack(){
        // Remove all vexes when the attack is ended.
        for(Vex vex : spirits){
            vex.remove();
        }
    }
}
