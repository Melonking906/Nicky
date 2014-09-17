package me.nonit.nicky.commands;

import me.nonit.nicky.Nick;
import me.nonit.nicky.Nicky;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RealNameCommand implements CommandExecutor
{
    private HashMap<String,HashMap<String,String>> foundPlayers;

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
                for( Map.Entry<String,HashMap<String,String>> entry : foundPlayers.entrySet() )
                {
                    for( Map.Entry<String,String> player : entry.getValue().entrySet() )
                    {
                        if( entry.getKey().equals( "offline" ) )
                        {
                            sender.sendMessage( ChatColor.GRAY + "Players not on this Server:" );
                        }
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
        HashMap<String,String> onlinePlayers = new HashMap<String,String>();
        HashMap<String,String> offlinePlayers = new HashMap<String,String>();

        for( Map.Entry<String,String> player : Nick.searchGet( searchWord ).entrySet() )
        {
            if( ChatColor.stripColor( player.getValue() ).contains( searchWord ) )
            {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer( UUID.fromString( player.getKey() ) );

                if( offlinePlayer.isOnline() )
                {
                    onlinePlayers.put( player.getValue(), offlinePlayer.getName() );
                }
                else
                {
                    offlinePlayers.put( player.getValue(), offlinePlayer.getName() );
                }
            }
        }

        foundPlayers = new HashMap<String,HashMap<String,String>>();

        if( ! onlinePlayers.isEmpty() || ! offlinePlayers.isEmpty() )
        {
            foundPlayers.put( "online", onlinePlayers );
            foundPlayers.put( "offline", offlinePlayers );
        }
    }
}