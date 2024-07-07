package me.syntaxerror.evodragons;

import com.google.common.base.CaseFormat;
import me.syntaxerror.evodragons.attacks.AbstractAttack;
import me.syntaxerror.evodragons.attacks.AttackType;
import me.syntaxerror.evodragons.dragons.EvoDragon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Util {

    public static final String DRAGONS_CONFIG_PATH = "dragons";
    public static final long TICKS_PER_SECOND = 20L;
    public static final double DRAGON_RANGE = 1000.0;
    public static final ItemStack ARROW_NEXT = new ItemStack(Material.ARROW, 1);
    public static final ItemStack ARROW_PREVIOUS = new ItemStack(Material.ARROW, 1);

    public static void init(){
        createArrowNext();
        createArrowPrevious();
    }

    public static void playSound(Player player, Sound sound){
        player.getWorld().playSound(player.getLocation(), sound, SoundCategory.BLOCKS, 5, 5);
    }

    private static void createArrowNext(){
        ItemMeta meta = ARROW_NEXT.getItemMeta();
        meta.setDisplayName("Next Page");
        ARROW_NEXT.setItemMeta(meta);
    }

    private static void createArrowPrevious(){
        ItemMeta meta = ARROW_PREVIOUS.getItemMeta();
        meta.setDisplayName("Previous Page");
        ARROW_PREVIOUS.setItemMeta(meta);
    }
}
