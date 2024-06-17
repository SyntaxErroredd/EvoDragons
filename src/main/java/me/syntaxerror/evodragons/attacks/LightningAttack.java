package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.entity.Player;

public class LightningAttack extends AbstractAttack {

    private static final String ATTACK_STRING = "attacks.lightning_attack.";

    public LightningAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.LIGHTNING);
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Chooses either the new or legacy attack based on whether it is enabled in the config.
        legacyLightningAttack(player);
    }

    private void legacyLightningAttack(Player player){
        // Attack every player with lightning.
        double damage = EvoDragons.getInstance().getConfig().getDouble(ATTACK_STRING + "damage");
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.damage(damage, getEvoDragon().getEnderDragon());
    }
}
