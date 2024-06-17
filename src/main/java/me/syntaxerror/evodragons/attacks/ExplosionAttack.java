package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExplosionAttack extends AbstractAttack implements Listener {

    private final List<FallingBlock> fallingBlocks = new ArrayList<>();

    private static final String ATTACK_STRING = "attacks.explosion_attack.";
    private static final Material[] blockMats = new Material[] {Material.OBSIDIAN, Material.MAGENTA_CONCRETE, Material.BLACK_CONCRETE, Material.PURPLE_CONCRETE};

    public ExplosionAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.EXPLOSION);
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Creates an explosion around players.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        double damage = config.getDouble(ATTACK_STRING + "damage");
        double knockback = config.getDouble(ATTACK_STRING + "knockback");
        Random random = new Random();
        Location playerLoc = player.getLocation();
        player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, playerLoc, 10);
        Util.playSound(player, Sound.ENTITY_GENERIC_EXPLODE);
        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if(!(entity instanceof LivingEntity))
                continue;
            LivingEntity livingEntity = (LivingEntity) entity;
            double distance = player.getLocation().distanceSquared(livingEntity.getLocation());
            livingEntity.setVelocity(livingEntity.getLocation().subtract(player.getLocation()).toVector().add(new Vector(0, knockback, 0)).normalize().multiply(knockback / Math.sqrt(distance)));
        }
        player.setVelocity(new Vector(0, knockback, 0));
        player.damage(damage, getEvoDragon().getEnderDragon());
        for (int i = 0; i < 16; i++) {
            FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(player.getLocation().add(0.0, 0.5, 0.0), blockMats[random.nextInt(blockMats.length)], (byte) 0);
            fallingBlock.setDropItem(false);
            fallingBlock.setVelocity(player.getLocation().getDirection().normalize().multiply(random.nextInt(5) + 1).rotateAroundX((random.nextInt(5) + 1) * 10).rotateAroundY((i * 20)).rotateAroundZ((random.nextInt(5) + 1) * 10));
            fallingBlocks.add(fallingBlock);
        };
    }

    public void endAttack(){
        // Remove all falling blocks on end.
        for(FallingBlock fallingBlock : fallingBlocks){
            fallingBlock.remove();
        }
    }

    @EventHandler
    public void onChange(EntityChangeBlockEvent event) {
        // Prevent falling block from turning into normal blocks when contacting ground.
        if (event.getEntity() instanceof FallingBlock && fallingBlocks.contains((FallingBlock) event.getEntity())) {
            event.setCancelled(true);
            event.getEntity().remove();
            fallingBlocks.remove((FallingBlock) event.getEntity());
        }
    }
}
