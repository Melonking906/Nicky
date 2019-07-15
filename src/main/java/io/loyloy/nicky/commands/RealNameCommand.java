package io.loyloy.nicky.commands;

import io.loyloy.nicky.Nick;
import io.loyloy.nicky.Nicky;
import io.loyloy.nicky.databases.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RealNameCommand implements CommandExecutor
{
    private HashMap<String, HashMap<String, String>> foundPlayers = new HashMap<>();
    private HashMap<String, String> onlinePlayers = new HashMap<>();
    private HashMap<String, String> offlinePlayers = new HashMap<>();

    private static final int DEFAULT_MIN_SEARCH_LENGTH = 3;

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

            String search = args[0];

            int minSearchLength = DEFAULT_MIN_SEARCH_LENGTH;
            if( DEFAULT_MIN_SEARCH_LENGTH < Nicky.getMinLength() )
            {
                minSearchLength = Nicky.getMinLength();
            }
            if( search.length() < minSearchLength )
            {
                sender.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Your search must be at least " + minSearchLength + " characters!" );
                return;
            }

            findPlayers( search );

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
                    sender.sendMessage( ChatColor.GRAY + "Players not on your server or offline:" );
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
        offlinePlayers.clear();
        onlinePlayers.clear();

        List<SQL.SearchedPlayer> searchedPlayers = Nick.searchGet( searchWord );

        if( searchedPlayers == null )
        {
            return;
        }
        for( SQL.SearchedPlayer searchedPlayer : searchedPlayers )
        {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer( UUID.fromString( searchedPlayer.getUuid() ) );

            String playersNick = searchedPlayer.getNick();

            if( offlinePlayer.isOnline() )
            {
                Nick nick = new Nick( offlinePlayer.getPlayer() );
                playersNick = nick.format( playersNick );

                onlinePlayers.put( playersNick, searchedPlayer.getName() );
            }
            else
            {
                playersNick = ChatColor.translateAlternateColorCodes( '&', playersNick );

                offlinePlayers.put( playersNick, searchedPlayer.getName() );
            }
        }

        foundPlayers = new HashMap<>();

        if( !onlinePlayers.isEmpty() || !offlinePlayers.isEmpty() )
        {
            foundPlayers.put( "online", onlinePlayers );
            foundPlayers.put( "offline", offlinePlayers );
        }
    }
}