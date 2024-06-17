package me.syntaxerror.evodragons.powerups;

import org.bukkit.Material;

public enum PowerUpType {

    ARMOR_RESISTANCE(Material.NETHERITE_CHESTPLATE),
    ATTACK_SHIELD(Material.SHIELD),
    HOMING_ARROWS(Material.ENDER_EYE),
    MELEE_STRENGTH(Material.NETHERITE_SWORD),
    PROJECTILE_POWER(Material.BOW);

    private Material material;

    PowerUpType(Material material){
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }
}
