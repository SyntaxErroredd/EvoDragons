package me.syntaxerror.evodragons.powerups;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class HomingArrows extends AbstractPowerUp {

    public HomingArrows(EvoDragon evoDragon) {
        super(evoDragon, PowerUpType.HOMING_ARROWS);
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event){
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if(!getPoweredUp().contains(player))
            return;
        if(!player.getWorld().equals(getEvoDragon().getEnderDragon().getWorld()))
            return;
        Entity projectile = event.getProjectile();
        new BukkitRunnable(){
            @Override
            public void run() {
                if(projectile.isDead() || projectile.isOnGround() || getEvoDragon().getEnderDragon().isDead()) {
                    cancel();
                    return;
                }
                projectile.setVelocity(getEvoDragon().getEnderDragon().getLocation().subtract(projectile.getLocation()).toVector().normalize().multiply(2));
            }
        }.runTaskTimer(EvoDragons.getInstance(), 0L, 1L);
    }
}
