package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionAttack extends AbstractAttack{

    private static final String ATTACK_STRING = "attacks.potion_attack.";

    public PotionAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.POTION);
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Gives blindness, confusion, weakness and slowness to players.
        double seconds = EvoDragons.getInstance().getConfig().getDouble(ATTACK_STRING + "seconds");
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) (seconds * Util.TICKS_PER_SECOND), 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int) (seconds * Util.TICKS_PER_SECOND), 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, (int) (seconds * Util.TICKS_PER_SECOND), 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (seconds * Util.TICKS_PER_SECOND), 1));
    }
}
