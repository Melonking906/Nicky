package io.loyloy.nicky.commands;

import io.loyloy.nicky.Nick;
import io.loyloy.nicky.Nicky;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelNickCommand implements CommandExecutor
{
    private Nicky plugin;

    public DelNickCommand( Nicky plugin )
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if( ! (sender instanceof Player) )
        {
            runAsConsole( args );
        }
        else if( args.length >= 1 )
        {
            runAsAdmin( sender, args );
        }
        else
        {
            runAsPlayer( sender );
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    public void runAsConsole( String[] args )
    {
        if( args.length >= 1 )
        {
            OfflinePlayer receiver = plugin.getServer().getOfflinePlayer( args[0] );

            if( !receiver.hasPlayedBefore() )
            {
                plugin.log( "Could not find '" + args[0] + "', did you get the name right?");
                return;
            }

            Nick nick = new Nick( receiver );

            nick.unSet();

            if( receiver.isOnline() )
            {
                receiver.getPlayer().sendMessage( Nicky.getPrefix() + "Your nickname has been deleted by console." );
            }

            plugin.log( receiver.getName() + "'s nickname has been deleted!" );
        }
        else
        {
            plugin.log( "Usage: /delnick <name>" );
        }
    }

    @SuppressWarnings("deprecation")
    public void runAsAdmin( CommandSender sender, String[] args )
    {
        OfflinePlayer receiver = plugin.getServer().getOfflinePlayer( args[0] );

        if( !receiver.hasPlayedBefore() )
        {
            sender.sendMessage( Nicky.getPrefix() + "Could not find " + ChatColor.YELLOW + args[0] + ChatColor.GREEN + ", did you get the name right?");
            return;
        }

        if( sender.hasPermission( "nicky.del.other" ) )
        {
            Nick nick = new Nick( receiver );

            nick.unSet();

            if( receiver.isOnline() )
            {
                receiver.getPlayer().sendMessage( Nicky.getPrefix() + "Your nickname has been deleted by " + ChatColor.YELLOW + sender.getName() );
            }

            sender.sendMessage( Nicky.getPrefix() + ChatColor.YELLOW + receiver.getName() + ChatColor.GREEN + "'s nickname has been deleted!" );
        }
        else
        {
            sender.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Sorry, you don't have permission to delete other players nicks." );
        }
    }

    public void runAsPlayer( CommandSender sender )
    {
        if( sender.hasPermission( "nicky.del" ) )
        {
            Nick nick = new Nick( (Player) sender );

            nick.unSet();

            sender.sendMessage( Nicky.getPrefix() + "Your nickname has been deleted." );
        }
        else
        {
            sender.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Sorry, you don't have permission to delete a nick." );
        }
    }
}