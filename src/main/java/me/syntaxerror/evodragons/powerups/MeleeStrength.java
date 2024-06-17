package me.syntaxerror.evodragons.powerups;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MeleeStrength extends AbstractPowerUp{

    private static final String POWER_UP_STRING = "power_ups.melee_strength.";

    public MeleeStrength(EvoDragon evoDragon) {
        super(evoDragon, PowerUpType.MELEE_STRENGTH);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Player))
            return;
        if(!getPoweredUp().contains((Player) event.getDamager()))
            return;
        double damageIncrease = EvoDragons.getInstance().getConfig().getDouble(POWER_UP_STRING + "damage_increase");
        event.setDamage(event.getDamage() + (1 + damageIncrease));
    }
}
