package io.loyloy.nicky.commands;

import io.loyloy.nicky.Nick;
import io.loyloy.nicky.Nicky;
import io.loyloy.nicky.NickyMessages;
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
        final NickyMessages messages = Nicky.getMessages();
        if( sender.hasPermission( "nicky.realname" ) )
        {
            if( args.length < 1 )
            {
                sender.sendMessage( messages.HELP_COMMAND_REALNAME );
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
                sender.sendMessage(
                        messages.PREFIX +
                        messages.ERROR_SEARCH_TOO_SHORT
                                .replace( "{min}", String.valueOf( Nicky.getMinLength() ) )
                                .replace( "{max}", String.valueOf( Nicky.getMaxLength() ) )
                );
                return;
            }

            findPlayers( search );

            if( foundPlayers.isEmpty() )
            {
                sender.sendMessage( 
                        messages.PREFIX +
                        messages.REALNAME_NOBODY
                            .replace( "{query}", search )
                );
            }
            else
            {
                sender.sendMessage(
                        messages.PREFIX +
                        messages.REALNAME_FOUND
                                .replace( "{query}", search )
                );

                for( Map.Entry<String, String> player : foundPlayers.get( "online" ).entrySet() )
                {
                    sender.sendMessage( 
                            messages.PREFIX +
                            messages.REALNAME_FOUND_ENTRY
                                    .replace("{nickname}", player.getKey())
                                    .replace("{username}", player.getValue())
                    );
                }
                if( ! foundPlayers.get( "offline" ).isEmpty() )
                {
                    sender.sendMessage(
                            messages.PREFIX +
                            messages.REALNAME_FOUND_OFFLINE
                            .replace( "{query}", search )
                    );
                    for( Map.Entry<String, String> player : foundPlayers.get( "offline" ).entrySet() )
                    {
                        sender.sendMessage(
                                messages.PREFIX +
                                        messages.REALNAME_FOUND_ENTRY
                                                .replace("{nickname}", player.getKey())
                                                .replace("{username}", player.getValue())
                        );
                    }
                }
            }
        }
        else
        {
            sender.sendMessage( messages.PREFIX + messages.ERROR_REALNAME_PERMISSION );
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
                playersNick = nick.formatForServer( playersNick );

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
