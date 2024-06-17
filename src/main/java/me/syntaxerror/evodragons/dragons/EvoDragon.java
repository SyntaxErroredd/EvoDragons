package me.syntaxerror.evodragons.dragons;

import com.google.common.base.CaseFormat;
import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.Util;
import me.syntaxerror.evodragons.attacks.AbstractAttack;
import me.syntaxerror.evodragons.dragons.loot.LootConfigurationFile;
import me.syntaxerror.evodragons.powerups.AbstractPowerUp;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class EvoDragon implements Listener{

    private final EnderDragon enderDragon;
    private final String dragonKey;
    private final String name;
    private  final BarColor barColor;
    private final ChatColor nameColor;
    private final double maxHealth;
    private final List<AbstractAttack> abstractAttacks = new ArrayList<>();
    private final List<AbstractPowerUp> abstractPowerUps = new ArrayList<>();

    private boolean dead = false;

    public EvoDragon(EnderDragon enderDragon){
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        EvoDragons.getInstance().getServer().getPluginManager().registerEvents(this, EvoDragons.getInstance());
        this.enderDragon = enderDragon;
        dragonKey = getEnderDragonKey(enderDragon);
        name = config.getString(getConfigPath() + "name");
        barColor = BarColor.valueOf(config.getString(getConfigPath() + "boss_bar_color"));
        nameColor = ChatColor.valueOf(config.getString(getConfigPath() + "name_color"));
        maxHealth = config.getDouble(getConfigPath() + "health");

        selectAttacks();
        selectPowerUps();
    }

    public EvoDragon(EnderDragon enderDragon, String dragonKey){
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        EvoDragons.getInstance().getServer().getPluginManager().registerEvents(this, EvoDragons.getInstance());
        this.enderDragon = enderDragon;
        this.dragonKey = dragonKey;
        name = config.getString(getConfigPath() + "name");
        barColor = BarColor.valueOf(config.getString(getConfigPath() + "boss_bar_color"));
        nameColor = ChatColor.valueOf(config.getString(getConfigPath() + "name_color"));
        maxHealth = config.getDouble(getConfigPath() + "health");

        prepareDragon();
        selectAttacks();
        selectPowerUps();
    }

    public EnderDragon getEnderDragon() {
        return enderDragon;
    }

    public String getDragonKey() {
        return dragonKey;
    }
    
    public String getConfigPath(){
        return "dragons." + dragonKey + ".";
    }

    private void prepareDragon(){
        // Modify Ender Dragon entity based on config stats
        enderDragon.setCustomName(nameColor + "" + ChatColor.BOLD + name);
        enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        enderDragon.setHealth(maxHealth);
        if(enderDragon.getBossBar() != null)
            enderDragon.getBossBar().setColor(barColor);
        sendSpawnTalk();
    }

    private void selectAttacks(){
        // For each attack listed in the config, add to the list of attacks possible if it is enabled in the config.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        for(String attackString : config.getConfigurationSection("attacks").getKeys(false)) {
            if(!config.getBoolean(getConfigPath() + "attacks." + attackString))
                continue;
            String className = AbstractAttack.class.getPackage().getName() + "." + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, attackString);
            try {
                Class<?> attackClass = Class.forName(className);
                Constructor<?> constructor = attackClass.getConstructor(EvoDragon.class);
                AbstractAttack abstractAttack = (AbstractAttack) constructor.newInstance(this);
                abstractAttacks.add(abstractAttack);
                if(Listener.class.isAssignableFrom(attackClass)){
                    EvoDragons.getInstance().getServer().getPluginManager().registerEvents((Listener) abstractAttack, EvoDragons.getInstance());
                }
            } catch(Exception ignored){
            }
        }
    }

    private void selectPowerUps(){
        // For each power up listed in the config, add to the list of power ups possible if it is enabled in the config.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        for(String powerUpString : config.getConfigurationSection("power_ups").getKeys(false)) {
            if(!config.getBoolean(getConfigPath() + "power_ups." + powerUpString))
                continue;
            String className = AbstractPowerUp.class.getPackage().getName() + "." + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, powerUpString);
            try {
                Class<?> powerUpClass = Class.forName(className);
                Constructor<?> constructor = powerUpClass.getConstructor(EvoDragon.class);
                AbstractPowerUp abstractPowerUp = (AbstractPowerUp) constructor.newInstance(this);
                abstractPowerUps.add(abstractPowerUp);
                EvoDragons.getInstance().getServer().getPluginManager().registerEvents(abstractPowerUp, EvoDragons.getInstance());
            } catch(Exception ignored){
            }
        }
    }

    private String getEnderDragonKey(EnderDragon enderDragon){
        // Returns the dragon's key in config based on the Ender Dragon if it was previously an EvoDragon.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        ConfigurationSection section = config.getConfigurationSection("dragons");
        if(section == null)
            return null;
        for(String dragonKey : section.getKeys(false)){
            if(enderDragon.getCustomName() != null && enderDragon.getCustomName().contains(config.getString("dragons." + dragonKey + ".name")))
                return dragonKey;
        }
        return null;
    }

    public boolean isDead() {
        return dead;
    }

    public void sendSpawnTalk(){
        // Sends all nearby players a dragon spawn quote.
        Random random = new Random();
        List<String> quotes = EvoDragons.getInstance().getConfig().getStringList(getConfigPath() + "spawn_quotes");
        for(Player player : getNearbyPlayers()){
            player.sendMessage(enderDragon.getCustomName() + ChatColor.WHITE + ": " + quotes.get(random.nextInt(quotes.size())));
        }
    }

    public void attack(){
        // Select a random attack and execute it.
        double chance = EvoDragons.getInstance().getConfig().getDouble(getConfigPath() + "attack_chance");
        if(abstractAttacks.isEmpty())
            return;
        Random random = new Random();
        if(chance == 0)
            return;
        if(random.nextDouble() > chance)
            return;
        abstractAttacks.get(random.nextInt(abstractAttacks.size())).attack();
    }

    public void talk(){
        // Sends a random talk quote to all nearby players.
        FileConfiguration config = EvoDragons.getInstance().getConfig();
        double chance = config.getDouble(getConfigPath() + "talk_chance");
        Random random = new Random();
        if(chance == 0)
            return;
        if(random.nextDouble() > chance)
            return;
        List<String> quotes = config.getStringList(getConfigPath() + "talk_quotes");
        for(Player player : getNearbyPlayers()){
            player.sendMessage(enderDragon.getCustomName() + ChatColor.WHITE + ": " + quotes.get(random.nextInt(quotes.size())));
        }
    }

    public void createPowerUps(){
        // Spawns a random PowerUp for each nearby player.
        double chance = EvoDragons.getInstance().getConfig().getDouble(getConfigPath() + "power_up_chance");
        Random random = new Random();
        if(chance == 0)
            return;
        if(random.nextDouble() > chance)
            return;
        for(Player player : getNearbyPlayers()) {
            abstractPowerUps.get(random.nextInt(abstractPowerUps.size())).spawnPowerUpNearPlayer(player);
        }
    }

    public void dragonDeath(){
        // Sends the death messages, drops loot and remove all entities associated with the dragon's attacks.
        dead = true;
        endDragon();
        Random random = new Random();
        List<String> quotes = EvoDragons.getInstance().getConfig().getStringList(getConfigPath() + "death_quotes");
        for(Player player : getNearbyPlayers()){
            player.sendMessage(enderDragon.getCustomName() + ChatColor.WHITE + ": " + quotes.get(random.nextInt(quotes.size())));
        }
        for(ItemStack itemStack : LootConfigurationFile.rollLoot(dragonKey)){
            enderDragon.getWorld().dropItemNaturally(enderDragon.getLocation(), itemStack);
        }
    }

    public void endDragon(){
        // Calls all endAttack and endPowerUp methods to remove all entities associated with the attacks and powerups respectively.
        for(AbstractAttack attack : abstractAttacks){
            try {
                attack.getClass().getMethod("endAttack").invoke(attack);
            }catch (Exception ignored){
            }
        }
        for(AbstractPowerUp powerUp : abstractPowerUps){
            powerUp.endPowerUp();
        }
    }

    public List<Player> getNearbyPlayers(){
        // Returns a list of nearby players to the ender dragon.
        // TODO check for creative and spectator modes.
        List<Player> nearbyPlayers = new ArrayList<>();
        for(Entity entity : enderDragon.getNearbyEntities(Util.DRAGON_RANGE, Util.DRAGON_RANGE, Util.DRAGON_RANGE)){
            if(!(entity instanceof Player))
                continue;
            nearbyPlayers.add((Player) entity);
        }
        return nearbyPlayers;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event){
        // Creates the death messages and stops the attacks for the dragon.
        if(!(event.getEntity() instanceof EnderDragon))
            return;
        if(!enderDragon.equals(event.getEntity()))
            return;
        dragonDeath();
    }

    @EventHandler
    public void onPhase(EnderDragonChangePhaseEvent event){
        // Manipulate the ender dragon's land at portal rate based on the config settings.
        // TODO onPhase
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        // Set bed explosion damage based on config settings.
        // TODO onDamage
    }
}
