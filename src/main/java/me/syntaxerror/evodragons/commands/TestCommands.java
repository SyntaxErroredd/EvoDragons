package me.syntaxerror.evodragons.commands;

import me.syntaxerror.evodragons.EvoDragons;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TestCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;
            if(command.getName().equalsIgnoreCase("dragon")){
                ItemMeta itemMeta = player.getInventory().getItemInMainHand().getItemMeta();
                new BukkitRunnable() {
                    Location centerLoc = player.getLocation().add(0, 0.5, 0);
                    Vector iniVec = new Vector(1,0,0);
                    @Override
                    public void run() {
                        for(int rep = 0; rep < 3; rep++) {
                            for (int i = 0; i < 16; i++) {
                                double x = centerLoc.getX() + rep * Math.cos(Math.toRadians(360 / 16 * i));
                                double y = centerLoc.getY() + rep;
                                double z = centerLoc.getZ() + rep * Math.sin(Math.toRadians(360 / 16 * i));
                                Vector curVec = iniVec.clone().rotateAroundY(Math.toRadians(360 / 16 * i));
                                Location spawnLoc = new Location(player.getWorld(), x, y, z);
                                player.sendMessage(String.valueOf(spawnLoc.getX()));
                                player.sendMessage(String.valueOf(spawnLoc.getY()));
                                player.sendMessage(String.valueOf(spawnLoc.getZ()));
                                player.getWorld().spawnParticle(Particle.REDSTONE, spawnLoc, 1, new Particle.DustOptions(Color.RED, 5));
                            }
                            iniVec.add(new Vector(1, 0.25, 0));
                        }
                    }
                }.runTaskTimer(EvoDragons.getInstance(), 0L, 3L);
            }
        }
        return false;

        /*
        for(Player player : getNearbyPlayers()) {
            Util.playSound(player, Sound.ENTITY_ENDER_DRAGON_SHOOT);
            new BukkitRunnable() {
                Location centerLoc = player.getLocation().add(0, 0.5, 0);
                Vector iniVec = new Vector(0,0,0);
                int rep = 0;
                @Override
                public void run() {
                    player.damage(damage / 3, getEvoDragon().getEnderDragon());
                    iniVec.add(new Vector(0.5, 0.25, 0));
                    rep++;
                    player.getWorld().spawnParticle(Particle.DRAGON_BREATH, player.getLocation(), 100);
                    for (int i = 0; i < 16; i++) {
                        Vector curVec = iniVec.clone().rotateAroundY(Math.toRadians(360 / 16 * i));
                        Location spawnLoc = centerLoc.add(curVec);
                        FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(new Location(player.getWorld(), spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ()), blockMats[random.nextInt(blockMats.length)], (byte) 0);
                        fallingBlocks.add(fallingBlock);
                        fallingBlock.setDropItem(false);
                    }
                    if(rep == 3)
                        cancel();
                }
            }.runTaskTimer(EvoDragons.getInstance(), 0L, 3L);
         */
    }
}
