package me.syntaxerror.evodragons;

import me.syntaxerror.evodragons.commands.AdminCommands;
import me.syntaxerror.evodragons.commands.TestCommands;
import me.syntaxerror.evodragons.dragons.DragonBattle;
import me.syntaxerror.evodragons.dragons.DragonSpawn;
import me.syntaxerror.evodragons.dragons.loot.LootConfigurationFile;
import me.syntaxerror.evodragons.dragons.loot.ChooseDragonLootInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class EvoDragons extends JavaPlugin {

    private static final List<DragonBattle> dragonBattles = new ArrayList<>();
    private static EvoDragons instance;

    @Override
    public void onEnable() {
        // Plugin startup logic

        instance = this;

        this.getServer().getPluginManager().registerEvents(new DragonSpawn(), this);

        this.getCommand("dragon").setExecutor(new TestCommands());
        this.getCommand("evodragons").setExecutor(new AdminCommands());

        this.saveDefaultConfig();

        LootConfigurationFile.loadConfig();
        DragonSpawn.checkAllDragons();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        for(DragonBattle dragonBattle : dragonBattles){
            dragonBattle.endBattle();
        }
    }

    public static EvoDragons getInstance() {
        return instance;
    }

    public static void addDragonBattle(DragonBattle dragonBattle){
        dragonBattles.add(dragonBattle);
    }
}
