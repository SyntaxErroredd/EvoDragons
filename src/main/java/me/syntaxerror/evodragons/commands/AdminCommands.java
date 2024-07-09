package me.syntaxerror.evodragons.commands;

import me.syntaxerror.evodragons.EvoDragons;
import me.syntaxerror.evodragons.dragons.loot.ChooseDragonLootInventory;
import me.syntaxerror.evodragons.dragons.loot.ChooseDragonTypeLootInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class AdminCommands implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return false;
        Player sender = (Player) commandSender;
        if(!sender.isOp())
            return false;
        if(!command.getName().equalsIgnoreCase("evodragons"))
            return false;
        if(!(strings.length > 0))
            return false;
        if(strings[0].equalsIgnoreCase("loot")){
            new ChooseDragonTypeLootInventory(sender);
            return true;
        }
        else if(strings[0].equalsIgnoreCase("reload")){
            EvoDragons.getInstance().reloadConfig();
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return null;
        Player sender = (Player) commandSender;
        if(!sender.isOp())
            return null;
        if(!command.getName().equalsIgnoreCase("evodragons"))
            return null;
        if(strings.length == 1)
            return Arrays.asList("reload", "loot");
        return null;
    }
}
