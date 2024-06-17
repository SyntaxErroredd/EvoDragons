package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.entity.Player;

public class FireAttack extends AbstractAttack{

    private static final String ATTACK_STRING = "attacks.fire_attack.";

    public FireAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.FIRE);
    }

    @Override
    public void attack() {
        attackAllPlayers();
    }

    @Override
    public void attackPlayer(Player player) {
        // Set all players on fire.
        double seconds = EvoDragons.getInstance().getConfig().getDouble(ATTACK_STRING + "seconds");
        player.setFireTicks((int) (seconds * Util.TICKS_PER_SECOND));
    }
}
