package me.syntaxerror.evodragons;

import com.google.common.base.CaseFormat;
import me.syntaxerror.evodragons.attacks.AbstractAttack;
import me.syntaxerror.evodragons.attacks.AttackType;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Util {

    public static final String DRAGONS_CONFIG_PATH = "dragons";
    public static final long TICKS_PER_SECOND = 20L;
    public static final double DRAGON_RANGE = 1000.0;

    public static void playSound(Player player, Sound sound){
        player.getWorld().playSound(player.getLocation(), sound, SoundCategory.BLOCKS, 5, 5);
    }
}
