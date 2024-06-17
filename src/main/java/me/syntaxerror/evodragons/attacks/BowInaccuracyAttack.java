package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BowInaccuracyAttack extends AbstractAttack implements Listener {

    private static final String ATTACK_STRING = "attacks.bow_inaccuracy_attack.";

    private final List<Player> attacked = new ArrayList<>();

    public BowInaccuracyAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.BOW_INACCURACY);
        EvoDragons.getInstance().getServer().getPluginManager().registerEvents(this, EvoDragons.getInstance());
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Add the player to the list.
        double seconds = EvoDragons.getInstance().getConfig().getDouble(ATTACK_STRING + "seconds");
        attacked.add(player);
        new BukkitRunnable(){
            @Override
            public void run() {
                attacked.remove(player);
            }
        }.runTaskLater(EvoDragons.getInstance(), (long) seconds * Util.TICKS_PER_SECOND);
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event){
        // Randomize direction of every attacked player.
        if(!(event.getEntity() instanceof Player))
            return;
        if(!attacked.contains((Player) event.getEntity()))
            return;
        Random random = new Random();
        double maxDegrees = EvoDragons.getInstance().getConfig().getDouble(ATTACK_STRING + "max_degrees");
        Projectile projectile = (Projectile) event.getProjectile();
        projectile.setVelocity(projectile.getVelocity().rotateAroundX(Math.toRadians(random.nextDouble() * maxDegrees - maxDegrees / 2)).rotateAroundY(Math.toRadians(random.nextDouble() * maxDegrees - maxDegrees / 2)).rotateAroundZ(Math.toRadians(random.nextDouble() * maxDegrees - maxDegrees / 2)));
    }
}
