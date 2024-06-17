package me.syntaxerror.evodragons.powerups;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class ArmorResistance extends AbstractPowerUp {

    private static final String POWER_UP_STRING = "power_ups.armor_resistance.";

    public ArmorResistance(EvoDragon evoDragon) {
        super(evoDragon, PowerUpType.ARMOR_RESISTANCE);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player))
            return;
        if(!getPoweredUp().contains((Player) event.getEntity()))
            return;
        double damageReduction = EvoDragons.getInstance().getConfig().getDouble(POWER_UP_STRING + "damage_reduction");
        event.setDamage(event.getDamage() * (1 - damageReduction));
    }
}
