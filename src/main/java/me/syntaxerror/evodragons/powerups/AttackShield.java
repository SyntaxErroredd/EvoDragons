package me.syntaxerror.evodragons.powerups;

import me.syntaxerror.evodragons.dragons.EvoDragon;
import me.syntaxerror.evodragons.events.EvoDragonAttackPlayerEvent;
import org.bukkit.event.EventHandler;

public class AttackShield extends AbstractPowerUp{

    public AttackShield(EvoDragon evoDragon) {
        super(evoDragon, PowerUpType.ATTACK_SHIELD);
    }

    @EventHandler
    public void onAttack(EvoDragonAttackPlayerEvent event){
        if(!getPoweredUp().contains(event.getPlayer()))
            return;
        event.setCancelled(true);
    }
}
