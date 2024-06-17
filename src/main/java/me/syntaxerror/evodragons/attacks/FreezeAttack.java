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

public class FreezeAttack extends AbstractAttack implements Listener {

    private static final String ATTACK_STRING = "attacks.freeze_attack.";
    private static final BlockFace[] BLOCK_FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final Material[] frostMats = {Material.SNOW_BLOCK, Material.PACKED_ICE, Material.ICE, Material.BLUE_ICE};
    private static final int FREEZE_SIZE = 2;

    private final HashMap<Block, Material> originalMats = new HashMap<>();
    private final List<Player> frozen = new ArrayList<>();

    public FreezeAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.FREEZE);
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Freezes a diagonal around players.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        double damage = config.getDouble(ATTACK_STRING + "damage");
        int seconds = config.getInt(ATTACK_STRING + "seconds");
        Random random = new Random();
        player.damage(damage, getEvoDragon().getEnderDragon());
        List<Block> frozenBlocks = new ArrayList<>();
        List<Player> currentFrozen = new ArrayList<>();
        frozen.add(player);
        currentFrozen.add(player);
        Block playerBlock = player.getWorld().getBlockAt(player.getLocation().add(0, -1, 0));
        originalMats.put(playerBlock, playerBlock.getType());
        frozenBlocks.add(playerBlock);
        for(int j = 0; j < FREEZE_SIZE; j++){
            List<Block> newFrozenBlocks = new ArrayList<>();
            for(Block block : frozenBlocks) {
                for (BlockFace blockFace : BLOCK_FACES) {
                    Block relativeBlock = block.getRelative(blockFace);
                    if(originalMats.containsKey(relativeBlock) || newFrozenBlocks.contains(relativeBlock))
                        continue;
                    originalMats.put(relativeBlock, relativeBlock.getType());
                    newFrozenBlocks.add(relativeBlock);
                }
            }
            frozenBlocks.addAll(newFrozenBlocks);
        }
        for(Block block : frozenBlocks){
            block.setType(frostMats[random.nextInt(frostMats.length)]);
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Block block : frozenBlocks){
                    block.setType(originalMats.get(block));
                    originalMats.remove(block);
                }
                frozen.remove(player);
            }
        }.runTaskLater(EvoDragons.getInstance(), seconds * Util.TICKS_PER_SECOND);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // Cancel a frozen player's movement.
        if(!frozen.contains(event.getPlayer()))
            return;
        event.setCancelled(true);
    }

    public void endAttack(){
        // Reset all frost placed blocks.
        for(Block block : originalMats.keySet()){
            block.setType(originalMats.get(block));
        }
    }
}
