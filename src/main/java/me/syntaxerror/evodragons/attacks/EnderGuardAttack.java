package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnderGuardAttack extends AbstractAttack implements Listener {

    private static final String ATTACK_STRING = "attacks.ender_guard_attack.";
    private final List<WitherSkeleton> guards = new ArrayList<>();

    public EnderGuardAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.ENDER_GUARD);
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Chooses either the new or legacy attack based on whether it is enabled in the config.
        legacyEnderGuardAttack(player);
    }

    private void legacyEnderGuardAttack(Player player){
        // Spawns an Ender Guard around each player.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        double health = config.getDouble(ATTACK_STRING + "health");
        String name = config.getString(ATTACK_STRING + "name");
        Random random = new Random();
        Location randomLoc = player.getLocation().add((random.nextInt(6) - 3), 0.0D, (random.nextInt(6) - 3));
        WitherSkeleton witherSkeleton = player.getWorld().spawn(randomLoc, WitherSkeleton.class);
        witherSkeleton.getEquipment().setHelmet(new ItemStack(Material.DRAGON_HEAD));
        witherSkeleton.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
        witherSkeleton.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
        witherSkeleton.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
        witherSkeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        witherSkeleton.setHealth(health);
        witherSkeleton.setCustomName(ChatColor.DARK_PURPLE + name);
        witherSkeleton.setCustomNameVisible(true);
        witherSkeleton.setTarget(player);
        guards.add(witherSkeleton);
        Util.playSound(player, Sound.ENTITY_WITHER_SKELETON_DEATH);

        new BukkitRunnable(){
            @Override
            public void run() {
                if(witherSkeleton.isDead())
                    cancel();

                if(EvoDragons.getInstance().getConfig().getBoolean(ATTACK_STRING + "teleport") && player.getWorld().equals(witherSkeleton.getWorld()) && witherSkeleton.getLocation().distanceSquared(player.getLocation()) > 100){
                    witherSkeleton.teleport(player.getLocation());
                    player.sendMessage(witherSkeleton.getCustomName() + ChatColor.WHITE + ": " + EvoDragons.getInstance().getConfig().getString(ATTACK_STRING + "teleport_quote"));
                }
            }
        }.runTaskTimer(EvoDragons.getInstance(), 0L, 20L);
    }

    @EventHandler
    public void onDrop(EntityDeathEvent event){
        // Prevents the Ender Guard from dropping anything.
        if(!(event.getEntity() instanceof WitherSkeleton))
            return;
        if(!guards.contains((WitherSkeleton) event.getEntity()))
            return;
        event.getDrops().clear();
        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.ENDER_PEARL, 1));
        guards.remove((WitherSkeleton) event.getEntity());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        // Changes the Ender Guard's damage as by config.
        if(!(event.getDamager() instanceof WitherSkeleton))
            return;
        if(!guards.contains((WitherSkeleton) event.getDamager()))
            return;
        event.setDamage(EvoDragons.getInstance().getConfig().getDouble(ATTACK_STRING + "damage"));
    }

    public void endAttack(){
        // Removes all Ender Guards.
        for(WitherSkeleton witherSkeleton : guards){
            witherSkeleton.remove();
        }
    }
}
