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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoulderAttack extends AbstractAttack implements Listener {

    private static final String ATTACK_STRING = "attacks.boulder_attack.";
    private static final double BOULDER_DISPLACEMENT = 3;
    private static final Vector[] FALLING_BLOCK_TRANSLATIONS = {new Vector(0, 1, 0),
    new Vector(0, 1.5, 0),
    new Vector(0.4, 1.4, 0),
    new Vector(0.4, 1, 0.6)};

    private static final Vector[] DISPLACEMENT_VECTORS = {new Vector(BOULDER_DISPLACEMENT, 0, 0),
    new Vector(-BOULDER_DISPLACEMENT, 0, 0),
    new Vector(0, 0, BOULDER_DISPLACEMENT),
    new Vector(0, 0, -BOULDER_DISPLACEMENT)};

    private final BiMap<ArmorStand, FallingBlock> standBlockLink = HashBiMap.create();

    public BoulderAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.BOULDER);
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Spawns 4 boulders that rise slowly then home toward the player.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        double damage = config.getDouble(ATTACK_STRING + "damage");
        Location playerLoc = player.getLocation();
        List<ArmorStand> armorStands = new ArrayList<>();
        for(Vector vector : DISPLACEMENT_VECTORS){
            createBoulder(playerLoc.clone().add(vector), armorStands);
        }

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (armorStands.isEmpty()) {
                    cancel();
                    return;
                }
                if (i % 10 == 0) {
                    for (ArmorStand armorStand : armorStands) {
                        if (armorStand.isDead())
                            continue;
                        armorStand.getWorld().spawnParticle(Particle.FLAME, armorStand.getLocation(), 3);
                    }
                }
                if (i < 60) {
                    for (ArmorStand armorStand : armorStands) {
                        if (armorStand.isDead())
                            continue;
                        armorStand.eject();
                        armorStand.teleport(armorStand.getLocation().add(0, 0.1, 0));
                        armorStand.addPassenger(standBlockLink.get(armorStand));
                    }
                } else {
                    List<ArmorStand> toRemove = new ArrayList<>();
                    for (ArmorStand armorStand : armorStands) {
                        if (armorStand.isDead())
                            continue;
                        Location armorStandLoc = armorStand.getLocation();
                        if(!armorStandLoc.getWorld().equals(player.getWorld()))
                            continue;
                        armorStand.eject();
                        armorStand.teleport(armorStandLoc.add(player.getEyeLocation().subtract(armorStandLoc).toVector().normalize()));
                        armorStand.addPassenger(standBlockLink.get(armorStand));
                        if (armorStandLoc.distanceSquared(player.getEyeLocation()) <= 1) {
                            player.damage(damage / 16, getEvoDragon().getEnderDragon());
                            player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getEyeLocation(), 5);
                            standBlockLink.get(armorStand).remove();
                            armorStand.remove();
                            toRemove.add(armorStand);
                            standBlockLink.remove(armorStand);
                        }
                    }
                    armorStands.removeAll(toRemove);
                }
                i++;
            }
        }.runTaskTimer(EvoDragons.getInstance(), 0L, 1L);
    }

    private void createBoulder(Location centerLoc, List<ArmorStand> armorStands){
        // Spawns a boulder, each made up of four falling blocks.
        for(Vector vector : FALLING_BLOCK_TRANSLATIONS){
            createBoulderFallingBlock(centerLoc.clone().add(vector), armorStands);
        }
    }

    private void createBoulderFallingBlock(Location location, List<ArmorStand> armorStands){
        // Spawns a falling falling with its armor stand.
        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, Material.END_STONE, (byte)0);
        fallingBlock.setDropItem(false);
        fallingBlock.setGravity(false);
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.addPassenger(fallingBlock);
        armorStands.add(armorStand);
        standBlockLink.put(armorStand, fallingBlock);
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
