package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LavaAttack extends AbstractAttack implements Listener {

    private static final String ATTACK_STRING = "attacks.lava_attack.";
    private static final long TICKS_PER_BLOCK = 5;
    private final List<Player> attacked = new ArrayList<>();
    private final List<Block> allLavaBlocks = new ArrayList<>();

    public LavaAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.LAVA);
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Creates a fountain of lava below players.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        double damage = config.getDouble(ATTACK_STRING + "damage");
        int height = config.getInt(ATTACK_STRING + "block_height");
        List<Block> lavaBlocks = new ArrayList<>();
        Location lavaLoc = player.getLocation();
        attacked.add(player);
        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run() {
                if(i == height){
                    for(Block block : lavaBlocks){
                        block.setType(Material.AIR);
                        allLavaBlocks.remove(block);
                    }
                    attacked.remove(player);
                    cancel();
                    return;
                }
                Block lavaBlock = player.getWorld().getBlockAt(lavaLoc);
                if(lavaBlock.getType() == Material.AIR){
                    lavaLoc.add(0, 1, 0);
                    lavaBlock.setType(Material.LAVA);
                    lavaBlocks.add(lavaBlock);
                    allLavaBlocks.add(lavaBlock);
                }
                player.damage(damage, getEvoDragon().getEnderDragon());
                player.teleport(lavaLoc);
                i++;
            }
        }.runTaskTimer(EvoDragons.getInstance(), 0L, TICKS_PER_BLOCK);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        // Remove all fire damage associated with the lava attack.
        if(!(event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)))
            return;
        if(!(event.getEntity() instanceof Player))
            return;
        if(!attacked.contains((Player) event.getEntity()))
            return;
        event.setCancelled(true);
    }

    public void endAttack(){
        // Reset all lava placed blocks.
        for(Block block : allLavaBlocks){
            block.setType(Material.AIR);
        }
    }
}
