package me.nonit.nicky.commands;

import me.nonit.nicky.Nick;
import me.nonit.nicky.Nicky;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor
{
    Nicky plugin;

    public NickCommand( Nicky plugin )
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        Player player = (Player) sender;

        if( sender.hasPermission( "nicky.set" ) )
        {
            String nickname = args[0];

            if( sender.hasPermission( "nicky.color" ) )
            {
                nickname = ChatColor.translateAlternateColorCodes( '&', args[0] );
            }

            Nick nick = new Nick( plugin, player );

            nick.setNick( nickname );
            nick.loadNick();

            player.sendMessage( Nicky.getPrefix() + "Your nickname has been set to " + ChatColor.YELLOW + nickname + ChatColor.GREEN + " !" );
        }
        else
        {
            player.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Sorry, you don't have permission to set a nick." );
        }

        return true;
    }
}
