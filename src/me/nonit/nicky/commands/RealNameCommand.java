package me.nonit.nicky.commands;

import me.nonit.nicky.Nick;
import me.nonit.nicky.Nicky;
import me.nonit.nicky.databases.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class RealNameCommand implements CommandExecutor
{
    private HashMap<String, HashMap<String, String>> foundPlayers;

    public RealNameCommand()
    {
    }

    public boolean onCommand( CommandSender sender, Command command, String s, String[] args )
    {
        runAsPlayer( sender, args );

        return true;
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

            if( foundPlayers.isEmpty() )
            {
                sender.sendMessage( ChatColor.GREEN + "No one has a nickname containing: " + ChatColor.YELLOW + args[0] );
            }
            else
            {
                sender.sendMessage( ChatColor.GREEN + "Players with a nickname containing: " + ChatColor.YELLOW + args[0] );

                for( Map.Entry<String, String> player : foundPlayers.get( "online" ).entrySet() )
                {
                    sender.sendMessage( ChatColor.YELLOW + player.getKey() + ChatColor.GRAY + " -> " + ChatColor.YELLOW + player.getValue() );
                }
                if( ! foundPlayers.get( "offline" ).isEmpty() )
                {
                    sender.sendMessage( ChatColor.GRAY + "Players not on this Server:" );
                    for( Map.Entry<String, String> player : foundPlayers.get( "offline" ).entrySet() )
                    {
                        sender.sendMessage( ChatColor.YELLOW + player.getKey() + ChatColor.GRAY + " -> " + ChatColor.YELLOW + player.getValue() );
                    }
                }
            }
        }
        else
        {
            sender.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Sorry, you don't have permission to check real names." );
        }
    }

    private void findPlayers( String searchWord )
    {
        HashMap<String, String> onlinePlayers = new HashMap<String, String>();
        HashMap<String, String> offlinePlayers = new HashMap<String, String>();

        for( SQL.SearchedPlayer searchedPlayer : Nick.searchGet( searchWord ) )
        {
            String playersNick = ChatColor.translateAlternateColorCodes( '&', searchedPlayer.getNick() );

            if( ChatColor.stripColor( playersNick ).contains( searchWord ) )
            {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer( UUID.fromString( searchedPlayer.getUuid() ) );

                if( offlinePlayer.isOnline() )
                {
                    onlinePlayers.put( playersNick, searchedPlayer.getName() );
                }
                else
                {
                    offlinePlayers.put( playersNick, searchedPlayer.getName() ) ;
                }
            }
        }

        foundPlayers = new HashMap<String, HashMap<String, String>>();

        if( !onlinePlayers.isEmpty() || !offlinePlayers.isEmpty() )
        {
            foundPlayers.put( "online", onlinePlayers );
            foundPlayers.put( "offline", offlinePlayers );
        }
    }
}