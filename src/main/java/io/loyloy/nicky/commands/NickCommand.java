package io.loyloy.nicky.commands;

import io.loyloy.nicky.Nick;
import io.loyloy.nicky.Nicky;
import io.loyloy.nicky.NickyMessages;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor
{
    private Nicky plugin;

    public NickCommand( Nicky plugin )
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if( ! (sender instanceof Player) )
        {
            runAsConsole( args );
        }
        else if( args.length >= 2 )
        {
            runAsAdmin( sender, args );
        }
        else
        {
            runAsPlayer( sender, args );
        }

        return true;
    }

    private void runAsConsole( String[] args )
    {
        final NickyMessages messages = Nicky.getMessages();
        if( args.length >= 2  )
        {
            OfflinePlayer receiver = plugin.getServer().getOfflinePlayer( args[0] );

            if( !receiver.hasPlayedBefore() )
            {
                plugin.log( ChatColor.stripColor(
                        messages.PREFIX + 
                        messages.ERROR_PLAYER_NOT_FOUND.replace( "{player}", args[0] )
                ) );
                return;
            }

            String nickname = args[1].trim();

            if( nickname.equals( receiver.getName() ) )
            {
                new DelNickCommand( plugin ).runAsConsole( args );
                return;
            }

            String strippedNickname = ChatColor.stripColor( ChatColor.translateAlternateColorCodes( '&', nickname ) );
            if( strippedNickname.length() < Nicky.getMinLength() )
            {
                plugin.log( ChatColor.stripColor(
                    messages.PREFIX +
                        messages.ERROR_NICKNAME_TOO_SHORT
                                .replace( "{min}", String.valueOf( Nicky.getMinLength() ) )
                                .replace( "{max}", String.valueOf( Nicky.getMaxLength() ) )
                ) );
                return;
            }

            Nick nick = new Nick( receiver );

            if( Nick.isUsed( nickname ) )
            {
                plugin.log( ChatColor.stripColor(
                        messages.PREFIX +
                        messages.ERROR_NICKNAME_TAKEN
                                .replace( "{nickname}", nickname )
                ) );
                return;
            }

            nick.set( nickname );
            nickname = nick.get();

            if( receiver.isOnline() )
            {
                receiver.getPlayer().sendMessage(
                        messages.PREFIX +
                        messages.NICKNAME_WAS_CHANGED
                                .replace( "{nickname}", nickname )
                                .replace( "{admin}", "console" )
                                .replace( "{admin_nickname}", "console" )
                );
            }
            plugin.log( ChatColor.stripColor(
                    messages.PREFIX +
                    messages.NICKNAME_CHANGED_OTHER
                            .replace( "{receiver}", receiver.getName() )
                            .replace( "{receiver_nickname}", nickname )
            ) );
        }
        else
        {
            plugin.log( ChatColor.stripColor( messages.COMMAND_NICK_USAGE_PLAYER ) );
        }
    }

    private void runAsAdmin( CommandSender sender, String[] args )
    {
        final NickyMessages messages = Nicky.getMessages();
        OfflinePlayer receiver = plugin.getServer().getOfflinePlayer( args[0] );
        
        String senderNickname = sender.getName();
        if ( sender instanceof Player ) {
            Nick nick = new Nick( (Player) sender );
            String nickString = nick.get();
            if ( nickString != null ) {
                senderNickname = nickString;
            }
        }
        
        if( !receiver.hasPlayedBefore() )
        {
            sender.sendMessage(
                    messages.PREFIX +
                            messages.ERROR_PLAYER_NOT_FOUND.replace( "{player}", args[0] )
            );
            return;
        }

        String nickname = args[1];

        if( sender.hasPermission( "nicky.set.other" ) )
        {
            if( nickname.equals( receiver.getName() ) )
            {
                new DelNickCommand( plugin ).runAsAdmin( sender, args );
                return;
            }

            String strippedNickname = ChatColor.stripColor( ChatColor.translateAlternateColorCodes( '&', nickname ) );
            if( strippedNickname.length() < Nicky.getMinLength() )
            {
                sender.sendMessage(
                        messages.PREFIX +
                        messages.ERROR_NICKNAME_TOO_SHORT
                            .replace( "{min}", String.valueOf( Nicky.getMinLength() ) ) 
                            .replace( "{max}", String.valueOf( Nicky.getMaxLength() ) )
                );
                return;
            }
            // TODO: Max length warning.

            Nick nick = new Nick( receiver );

            if( Nick.isBlacklisted( nickname ) && !sender.hasPermission( "nicky.noblacklist" ) )
            {
                sender.sendMessage(
                        messages.PREFIX +
                        messages.ERROR_NICKNAME_BLACKLISTED
                                .replace( "{nickname}", nickname )
                );
                return;
            }

            if( Nick.isUsed( nickname ) )
            {
                sender.sendMessage(
                        messages.PREFIX +
                        messages.ERROR_NICKNAME_TAKEN
                                .replace( "{nickname}", nickname )
                );
                return;
            }

            nick.set( nickname );
            nickname = nick.get();

            if( receiver.isOnline() )
            {
                receiver.getPlayer().sendMessage(
                        messages.PREFIX +
                        messages.NICKNAME_WAS_CHANGED
                            .replace("{nickname}", nickname)
                            .replace("{admin}", sender.getName())
                            .replace("{admin_nickname}", senderNickname)
                );
            }

            sender.sendMessage(
                    messages.PREFIX +
                    messages.NICKNAME_CHANGED_OTHER
                            .replace( "{receiver}", receiver.getName() )
                            .replace( "{receiver_nickname}", nickname )
            );
        }
        else
        {
            sender.sendMessage(
                    messages.PREFIX +
                    messages.ERROR_CHANGE_OTHER_PERMISSION
                            .replace( "{receiver}", receiver.getName() )
            );
        }
    }

    private void runAsPlayer( CommandSender sender, String[] args )
    {
        final NickyMessages messages = Nicky.getMessages();
        Player player = (Player) sender;

        if( sender.hasPermission( "nicky.set" ) )
        {
            if( args.length >= 1 )
            {
                String nickname = args[0];

                if( nickname.equals( sender.getName() ) )
                {
                    new DelNickCommand( plugin ).runAsPlayer( sender );
                    return;
                }

                String strippedNickname = ChatColor.stripColor( ChatColor.translateAlternateColorCodes( '&', nickname ) );
                if( strippedNickname.length() < Nicky.getMinLength() )
                {
                    player.sendMessage(
                            messages.PREFIX +
                            messages.ERROR_NICKNAME_TOO_SHORT
                                    .replace( "{min}", String.valueOf( Nicky.getMinLength() ) )
                                    .replace( "{max}", String.valueOf( Nicky.getMaxLength() ) )
                    );
                    return;
                }
                // TODO: Max length warning

                Nick nick = new Nick( player );

                if( Nick.isBlacklisted( nickname ) && !player.hasPermission( "nicky.noblacklist" ) )
                {
                    player.sendMessage(
                            messages.PREFIX +
                            messages.ERROR_NICKNAME_BLACKLISTED
                                    .replace( "{nickname}", nickname )
                    );
                    return;
                }

                if( Nick.isUsed( nickname ) )
                {
                    player.sendMessage(
                            messages.PREFIX +
                            messages.ERROR_NICKNAME_TAKEN
                                    .replace( "{nickname}", nickname )
                    );
                    return;
                }

                nick.set( nickname );
                nickname = nick.get();

                player.sendMessage(
                        messages.PREFIX +
                        messages.NICKNAME_CHANGED_OWN
                                .replace( "{username}", player.getName() )
                                .replace( "{nickname}", nickname )
                );
            }
            else
            {
                player.sendMessage( messages.PREFIX + messages.COMMAND_NICK_USAGE_PLAYER );
            }
        }
        else
        {
            player.sendMessage( messages.PREFIX + messages.ERROR_CHANGE_OWN_PERMISSION );
        }
    }
}
