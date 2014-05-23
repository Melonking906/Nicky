package me.nonit.nicky.commands;

import me.nonit.nicky.Nick;
import me.nonit.nicky.Nicky;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnNickCommand implements CommandExecutor
{
    Nicky plugin;

    public UnNickCommand( Nicky plugin )
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player) sender;

        if( sender.hasPermission( "nicky.unset" ) )
        {
            Nick nick = new Nick( plugin, player );

            nick.unLoadNick();

            player.sendMessage( Nicky.getPrefix() + "Your nickname has been deleted." );
        }
        else
        {
            player.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Sorry, you don't have permission to unset a nick." );
        }

        return true;
    }
}