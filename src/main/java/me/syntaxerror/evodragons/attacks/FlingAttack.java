package me.syntaxerror.evodragons.attacks;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FlingAttack extends AbstractAttack implements Listener {

    private static final String ATTACK_STRING = "attacks.fling_attack.";

    private final List<Player> flung = new ArrayList<>();

    public FlingAttack(EvoDragon evoDragon) {
        super(evoDragon, AttackType.FLING);
    }

    @Override
    public void attack() {
        attackAllPlayers();
        new BukkitRunnable(){
            @Override
            public void run() {
                flung.removeIf(player -> player.isOnGround() || player.getWorld().getBlockAt(player.getLocation()).isLiquid());
                if(flung.isEmpty())
                    cancel();
            }
        }.runTaskTimer(EvoDragons.getInstance(), 10L, 1L);
    }

    @Override
    public void attackPlayer(Player player) {
        // Flings players into the air.
        double power = EvoDragons.getInstance().getConfig().getDouble(ATTACK_STRING + "power");
        player.setVelocity(new Vector(0, 1, 0).multiply(power));
        player.setGliding(false);
        flung.add(player);
    }

    @EventHandler
    public void onElytra(EntityToggleGlideEvent event){
        // Disable elytra if toggled in config.
        if(EvoDragons.getInstance().getConfig().getBoolean(ATTACK_STRING + "allow_elytra"))
            return;
        if(!(event.getEntity() instanceof Player))
            return;
        if(!flung.contains((Player) event.getEntity()))
            return;
        event.setCancelled(true);
        event.getEntity().sendMessage(ChatColor.RED + EvoDragons.getInstance().getConfig().getString(ATTACK_STRING + "elytra_attempt_quote"));
    }
}
