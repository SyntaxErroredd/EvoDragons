package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.ChatColor;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class EndermanAttack extends AbstractAttack{

    private static final String ATTACK_STRING = "attacks.enderman_attack.";

    public EndermanAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.ENDERMAN);
    }

    @Override
    public void attack() {
        // Command nearby endermen to attack the nearby players.
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        List<Enderman> endermen = player.getNearbyEntities(10, 10, 10).stream()
                .filter(entity -> entity instanceof Enderman)
                .map(entity -> (Enderman) entity)
                .collect(Collectors.toList());
        if(endermen.isEmpty()){
            player.sendMessage(getEvoDragon().getEnderDragon().getCustomName() + ChatColor.WHITE + ": " +
                    EvoDragons.getInstance().getConfig().getString(ATTACK_STRING + "no_endermen_nearby_quote"));
            return;
        }
        for(Enderman enderman : endermen){
            enderman.setTarget(player);
            player.sendMessage(enderman.getName() + ChatColor.WHITE + ": " +
                    EvoDragons.getInstance().getConfig().getString(ATTACK_STRING + "enderman_attack_quote"));
        }
    }
}
