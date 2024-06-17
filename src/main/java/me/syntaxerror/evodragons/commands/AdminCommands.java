package me.syntaxerror.evodragons.commands;

import me.syntaxerror.evodragons.dragons.loot.ChooseDragonLootInventory;
import me.syntaxerror.evodragons.dragons.loot.ChooseDragonTypeLootInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands implements CommandExecutor {
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
        }
        return false;
    }
}
