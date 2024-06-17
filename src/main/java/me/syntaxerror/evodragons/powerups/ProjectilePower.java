package me.syntaxerror.evodragons.powerups;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ProjectilePower extends AbstractPowerUp{

    private static final String POWER_UP_STRING = "power_ups.projectile_power.";

    public ProjectilePower(EvoDragon evoDragon) {
        super(evoDragon, PowerUpType.PROJECTILE_POWER);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Projectile))
            return;
        Projectile projectile = (Projectile) event.getDamager();
        if(!(projectile.getShooter() instanceof Player))
            return;
        if(!getPoweredUp().contains((Player) projectile.getShooter()))
            return;
        double damageIncrease = EvoDragons.getInstance().getConfig().getDouble(POWER_UP_STRING + "damage_increase");
        event.setDamage(event.getDamage() * (1 + damageIncrease));
    }
}
