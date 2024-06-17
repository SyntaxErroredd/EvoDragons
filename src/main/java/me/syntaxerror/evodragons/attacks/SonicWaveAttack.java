package me.syntaxerror.evodragons.attacks;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SonicWaveAttack extends AbstractAttack implements Listener {

    private static final String ATTACK_STRING = "attacks.sonic_wave_attack.";
    private static final Material[] blockMats = new Material[]{Material.OBSIDIAN, Material.MAGENTA_CONCRETE, Material.BLACK_CONCRETE, Material.PURPLE_CONCRETE};

    BiMap<ArmorStand, FallingBlock> standBlockLink = HashBiMap.create();

    public SonicWaveAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.SONIC_WAVE);

        new BukkitRunnable() {
            @Override
            public void run() {
                List<ArmorStand> toRemove = new ArrayList<>();
                for (ArmorStand armorStand : standBlockLink.keySet()) {
                    if (armorStand.isOnGround()) {
                        standBlockLink.get(armorStand).remove();
                        toRemove.add(armorStand);
                    }
                }
                for (ArmorStand armorStand : toRemove) {
                    standBlockLink.remove(armorStand);
                    armorStand.remove();
                }
            }
        }.runTaskTimer(EvoDragons.getInstance(), 0L, 1L);
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Creates a sonic wave for players.
        double damage = EvoDragons.getInstance().getConfig().getDouble(ATTACK_STRING + "damage");
        Random random = new Random();
        Util.playSound(player, Sound.ENTITY_ENDER_DRAGON_SHOOT);
        new BukkitRunnable() {
            Location centerLoc = player.getLocation().add(0, 0.5, 0);
            int rep = 0;

            @Override
            public void run() {
                player.damage(damage / 3, getEvoDragon().getEnderDragon());
                rep++;
                player.getWorld().spawnParticle(Particle.DRAGON_BREATH, player.getLocation(), 100);
                for (int i = 0; i < 16; i++) {
                    double x = centerLoc.getX() + rep * Math.cos(Math.toRadians(360 / 16 * i));
                    double y = centerLoc.getY() + rep - 1;
                    double z = centerLoc.getZ() + rep * Math.sin(Math.toRadians(360 / 16 * i));
                    Location spawnLoc = new Location(player.getWorld(), x, y, z);

                    // Check if the block at the location is air or other non-solid material
                    Block blockAtLocation = spawnLoc.getBlock();
                    if (blockAtLocation.isEmpty() || blockAtLocation.isLiquid()) {
                        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
                        armorStand.setVisible(false);
                        armorStand.setSmall(true);

                        FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(spawnLoc, blockMats[random.nextInt(blockMats.length)], (byte) 0);
                        fallingBlock.setDropItem(false);
                        fallingBlock.setHurtEntities(false);

                        armorStand.addPassenger(fallingBlock);
                        standBlockLink.put(armorStand, fallingBlock);
                    }
                }
                if (rep == 3)
                    cancel();
            }
        }.runTaskTimer(EvoDragons.getInstance(), 0L, 3L);
    }

    public void endAttack() {
        // Remove all falling blocks and armor stands related to the attack.
        for (ArmorStand armorStand : standBlockLink.keySet()) {
            standBlockLink.get(armorStand).remove();
            armorStand.remove();
        }
        standBlockLink.clear();
    }

    @EventHandler
    public void onChange(EntityChangeBlockEvent event) {
        // Prevent falling block from turning into normal blocks when contacting ground.
        if (event.getEntity() instanceof FallingBlock && standBlockLink.containsValue((FallingBlock) event.getEntity())) {
            event.setCancelled(true);
            ArmorStand armorStand = standBlockLink.inverse().get((FallingBlock) event.getEntity());
            standBlockLink.remove(armorStand);
            armorStand.remove();
            event.getEntity().remove();
        }
    }
}