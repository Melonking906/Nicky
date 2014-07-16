package me.nonit.nicky.commands;

import me.nonit.nicky.Nick;
import me.nonit.nicky.Nicky;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RealNameCommand implements CommandExecutor
{
    private Nicky plugin;
    private HashMap<String,String> players;

    public RealNameCommand( Nicky plugin )
    {
        this.plugin = plugin;
    }

    public boolean onCommand( CommandSender sender, Command command, String s, String[] args )
    {
        if( ! (sender instanceof Player) )
        {
            runAsConsole( args );
        }
        else
        {
            runAsPlayer( sender, args );
        }

        return true;
    }

    private void runAsConsole( String[] args )
    {
        if( args.length < 1 )
        {
            plugin.log( "To check a nickname do /realname <search>" );
            return;
        }

        findPlayers( args[0] );

        if( players.isEmpty() )
        {
            plugin.log( "No one has a nickname containing: " + args[0] );
        }
        else
        {
            plugin.log( "Players with a nickname containing: " + args[0] );
            for( Map.Entry<String, String> entry : players.entrySet() )
            {
                plugin.log( entry.getKey() + " -> " + entry.getValue() );
            }
        }
    }

    private void runAsPlayer( CommandSender sender, String[] args )
    {
        if( sender.hasPermission( "nicky.realname" ) )
        {
            if( args.length < 1 )
            {
                sender.sendMessage( Nicky.getPrefix() + "To check a nickname do " + ChatColor.YELLOW + "/realname <search>" );
                return;
            }

            findPlayers( args[0] );

            if( players.isEmpty() )
            {
                sender.sendMessage( ChatColor.GREEN + "No one has a nickname containing: " + ChatColor.YELLOW + args[0] );
            }
            else
            {
                sender.sendMessage( ChatColor.GREEN + "Players with a nickname containing: " + ChatColor.YELLOW + args[0] );
                for( Map.Entry<String,String> entry : players.entrySet() )
                {
                    sender.sendMessage( ChatColor.YELLOW + entry.getKey() + ChatColor.GRAY + " -> " + ChatColor.YELLOW + entry.getValue() );
                }
            }
        }
        else
        {
            sender.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Sorry, you don't have permission to check real names." );
        }
    }

    private HashMap<String,String> findPlayers( String searchWord )
    {
        players = new HashMap<String,String>();

        for( Player player : Bukkit.getOnlinePlayers() )
        {
            String nickname = new Nick( player ).get();
            if( nickname != null )
            {
                String realname = player.getName();

                if( ChatColor.stripColor( nickname ).contains( searchWord ) )
                {
                    players.put( nickname, realname );
                }
            }
        }

        return players;
    }
}