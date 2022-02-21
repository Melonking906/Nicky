package io.loyloy.nicky.commands;

import io.loyloy.nicky.*;
import io.loyloy.nicky.databases.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

public class RealNameCommand extends NickyCommandExecutor
{
    private static final int DEFAULT_MIN_SEARCH_LENGTH = 1;

    public RealNameCommand( Nicky plugin )
    {
        super( plugin );
    }

    @Override
    protected void execute(Player sender, Command cmd, String label, String[] args) {
        run( sender, args );
    }

    @Override
    protected void executeFromConsole(CommandSender sender, Command cmd, String label, String[] args) {
        run( sender, args );
    }

    private void run(CommandSender sender, String[] args )
    {
        final NickyMessages messages = Nicky.getMessages();

        // Show no permission message if no perms.
        if( !sender.hasPermission( "nicky.realname" ) ) {
            sender.sendMessage( messages.PREFIX + messages.ERROR_REALNAME_PERMISSION );
            return;
        }

        // Show usage message if invalid usage.
        if( args.length != 1 )
        {
            sender.sendMessage( messages.HELP_COMMAND_REALNAME );
            return;
        }

        // Search.
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

        try {
            Set<NickQuery.Result> results = NickQuery.find( search ).get();
            
            // Send a message if the result is empty.
            if( results.isEmpty() )
            {
                sender.sendMessage(
                        messages.PREFIX +
                        messages.REALNAME_NOBODY
                                .replace( "{query}", search )
                );
                return;
            }
            
            // Send a message with the results.
            sender.sendMessage(
                    messages.PREFIX +
                    messages.REALNAME_FOUND
                            .replace( "{query}", search )
            );

            // Online players first.
            for( NickQuery.Result result : results )
            {
                if ( !result.getPlayer().isOnline() ) continue;
                sender.sendMessage(
                        messages.PREFIX +
                        messages.REALNAME_FOUND_ENTRY
                                .replace("{nickname}", result.getPlainNickname())
                                .replace("{username}", result.getUsername())
                );
            }
            
            // Offline players next.
            for( NickQuery.Result result : results )
            {
                if ( result.getPlayer().isOnline() ) continue;
                sender.sendMessage(
                        messages.PREFIX +
                        messages.REALNAME_FOUND_ENTRY
                                .replace("{nickname}", result.getPlainNickname())
                                .replace("{username}", result.getUsername())
                );
            }
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        }
    }
    
}
