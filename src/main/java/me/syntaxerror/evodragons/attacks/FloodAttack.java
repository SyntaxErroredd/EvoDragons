package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FloodAttack extends AbstractAttack implements Listener {

    private static final String ATTACK_STRING = "attacks.flood_attack.";
    private static final BlockFace[] BLOCK_FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final int SECOND_FLOOD_TIMES = 2;
    private static final int FLOOD_SIZE = 2;

    private final List<Player> flooded = new ArrayList<>();
    private final List<Block> allFloodedBlocks = new ArrayList<>();

    public FloodAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.FLOOD);
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Floods a diagonal around players.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        double damage = config.getDouble(ATTACK_STRING + "damage");
        int seconds = config.getInt(ATTACK_STRING + "seconds");
        Random random = new Random();
        List<Block> floodedBlocks = new ArrayList<>();
        List<Player> currentFlooded = new ArrayList<>();
        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run() {
                if(i == seconds * SECOND_FLOOD_TIMES * 2) {
                    for(Block block : floodedBlocks){
                        block.setType(Material.AIR);
                        allFloodedBlocks.remove(block);
                    }
                    for(Player player : currentFlooded)
                        flooded.remove(player);
                    cancel();
                    return;
                }
                if(i == 0){
                    flooded.add(player);
                    currentFlooded.add(player);
                    Block playerBlock = player.getWorld().getBlockAt(player.getLocation());
                    floodedBlocks.add(playerBlock);
                    for(int j = 0; j < FLOOD_SIZE; j++){
                        List<Block> newFloodedBlocks = new ArrayList<>();
                        for(Block block : floodedBlocks) {
                            for (BlockFace blockFace : BLOCK_FACES) {
                                Block relativeBlock = block.getRelative(blockFace);
                                if(newFloodedBlocks.contains(relativeBlock) || relativeBlock.getType() != Material.AIR)
                                    continue;
                                newFloodedBlocks.add(relativeBlock);
                            }
                        }
                        floodedBlocks.addAll(newFloodedBlocks);
                    }
                    allFloodedBlocks.addAll(floodedBlocks);
                }

                if(i % 2 == 0){
                    for(Block block : floodedBlocks){
                        if(random.nextDouble() > 0.75)
                            continue;
                        block.setType(Material.WATER);
                    }
                    Util.playSound(player, Sound.ITEM_BUCKET_EMPTY);
                    player.damage(damage, getEvoDragon().getEnderDragon());
                }
                else{
                    for(Block block : floodedBlocks){
                        block.setType(Material.AIR);
                    }
                }
                i++;
            }
        }.runTaskTimer(EvoDragons.getInstance(), 0L, Util.TICKS_PER_SECOND / SECOND_FLOOD_TIMES / 2);
    }

    @EventHandler
    public void onFlow(BlockFromToEvent event) {
        // Prevent water from flowing over to other blocks.
        if(allFloodedBlocks.contains(event.getBlock()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // Cancel a flooded player's movement.
        if(!flooded.contains(event.getPlayer()))
            return;
        event.setCancelled(true);
    }

    public void endAttack(){
        // Reset all water placed blocks.
        for(Block block : allFloodedBlocks){
            block.setType(Material.AIR);
        }
    }
}
