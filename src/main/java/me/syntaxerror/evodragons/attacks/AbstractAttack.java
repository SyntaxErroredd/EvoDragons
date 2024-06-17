package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import me.syntaxerror.evodragons.events.EvoDragonAttackPlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractAttack {
    private final EvoDragon evoDragon;
    private final AttackType attackType;

    public AbstractAttack(EvoDragon evoDragon, AttackType attackType){
        this.evoDragon = evoDragon;
        this.attackType = attackType;
    }

    public EvoDragon getEvoDragon() {
        return evoDragon;
    }

    public void attackAllPlayers(){
        for(Player player : getEvoDragon().getNearbyPlayers()){
            if(registerPlayerAttack(player))
                continue;
            attackPlayer(player);
        }
    }

    public boolean registerPlayerAttack(Player player){
        // Sends both the info quote and attack quote to the player based on the specified AttackType.
        // Also calls the EvoDragonAttackEvent on the player, and listens if it needs to be cancelled.
        EvoDragonAttackPlayerEvent event = new EvoDragonAttackPlayerEvent(player, attackType);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            sendAttackQuote(player);
            sendInfoQuote(player);
        }
        return event.isCancelled();
    }

    private void sendInfoQuote(Player player){
        // Sends an attack info quote to the player involved based on the EvoDragon and attack used.
        String attackString = "attacks." + attackType.name().toLowerCase() + "_attack.";
        String message = EvoDragons.getInstance().getConfig().getString(attackString + "info_quote");
        String name = evoDragon.getEnderDragon().getCustomName();
        double damage = EvoDragons.getInstance().getConfig().getDouble(attackString + "damage");
        double seconds = EvoDragons.getInstance().getConfig().getDouble(attackString + "seconds");
        String enderGuardName = EvoDragons.getInstance().getConfig().getString(attackString + "name");
        String enderSpiritName = EvoDragons.getInstance().getConfig().getString(attackString + "name");
        int enderSpiritNumber = EvoDragons.getInstance().getConfig().getInt(attackString + "number");
        if(message == null)
            return;
        if(message.contains("<dragon_name>") && name != null){
            message = message.replaceAll( "<dragon_name>", name + ChatColor.GRAY);
        }
        if(message.contains("<damage>")){
            message = message.replaceAll("<damage>", ChatColor.RED + String.valueOf(damage) + ChatColor.GRAY);
        }
        if(message.contains("<seconds>")){
            message = message.replaceAll("<seconds>", ChatColor.RED + String.valueOf(seconds) + ChatColor.GRAY);
        }
        if(message.contains("<ender_guard_name>") && enderGuardName != null){
            message = message.replaceAll( "<ender_guard_name>", ChatColor.DARK_PURPLE + enderGuardName + ChatColor.GRAY);
        }
        if(message.contains("<ender_spirit_name>") && enderSpiritName != null){
            message = message.replaceAll( "<ender_spirit_name>", ChatColor.DARK_PURPLE + enderSpiritName + ChatColor.GRAY);
        }
        if(message.contains("<ender_spirit_number>")){
            message = message.replaceAll( "<ender_spirit_number>", ChatColor.RED + String.valueOf(enderSpiritNumber) + ChatColor.GRAY);
        }
        player.sendMessage(message);
    }

    private void sendAttackQuote(Player player){
        // Sends a quote to the player when the dragon is attacking.
        String attackString = "attacks." + attackType.name().toLowerCase() + "_attack.";
        List<String> dragonTalk = new ArrayList<>(EvoDragons.getInstance().getConfig().getStringList(attackString + ".attack_quotes"));
        Random random = new Random();
        if (!dragonTalk.isEmpty())
            player.sendMessage(evoDragon.getEnderDragon().getCustomName() + ChatColor.WHITE + ": " + dragonTalk.get(random.nextInt(dragonTalk.size())));
    }

    public abstract void attack();

    public abstract void attackPlayer(Player player);
}
