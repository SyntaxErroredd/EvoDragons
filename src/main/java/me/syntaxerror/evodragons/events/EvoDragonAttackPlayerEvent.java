package me.syntaxerror.evodragons.events;

import me.syntaxerror.evodragons.attacks.AttackType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EvoDragonAttackPlayerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final AttackType attackType;
    private boolean cancelled = false;

    public EvoDragonAttackPlayerEvent(Player player, AttackType attackType){
        this.player = player;
        this.attackType = attackType;
    }

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
