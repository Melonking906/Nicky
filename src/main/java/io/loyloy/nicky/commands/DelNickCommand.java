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
        final NickyMessages messages = Nicky.getMessages();
        if( args.length >= 1 )
        {
            OfflinePlayer receiver = plugin.getServer().getOfflinePlayer( args[0] );

            if( !receiver.hasPlayedBefore() )
            {
                plugin.log( ChatColor.stripColor( 
                        messages.PREFIX +
                        messages.ERROR_PLAYER_NOT_FOUND.replace( "{player}", args[0]) 
                ) );
                return;
            }

            Nick nick = new Nick( receiver );
            String oldNick = nick.get();
            if ( oldNick == null ) oldNick = receiver.getName();
            
            nick.unSet();

            if( receiver.isOnline() )
            {
                receiver.getPlayer().sendMessage(
                        messages.PREFIX +
                        messages.NICKNAME_WAS_DELETED
                                .replace( "{player}", receiver.getName() )
                                .replace( "{nickname}", oldNick )
                                .replace( "{admin}", "console" )
                                .replace( "{admin_nickname}", "console" )
                );
            }

            plugin.log( ChatColor.stripColor(
                    messages.PREFIX +
                    messages.NICKNAME_DELETED_OTHER
                        .replace( "{receiver}", receiver.getName() )
                        .replace( "{receiver_nickname}", oldNick )
            ) );
        }
        else
        {
            plugin.log( ChatColor.stripColor( messages.COMMAND_DELNICK_USAGE_ADMIN )  );
        }
    }

    @SuppressWarnings("deprecation")
    public void runAsAdmin( CommandSender sender, String[] args )
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

        if( sender.hasPermission( "nicky.del.other" ) )
        {
            Nick nick = new Nick( receiver );
            String oldNick = nick.get();
            if ( oldNick == null ) oldNick = receiver.getName();

            nick.unSet();

            if( receiver.isOnline() )
            {
                receiver.getPlayer().sendMessage(
                        messages.PREFIX +
                        messages.NICKNAME_WAS_DELETED
                            .replace("{username}", receiver.getName())
                            .replace("{nickname}", oldNick)
                            .replace("{admin}", sender.getName())
                            .replace("{admin_nickname}", senderNickname)
                );
            }

            sender.sendMessage(
                    messages.PREFIX +
                    messages.NICKNAME_CHANGED_OTHER
                            .replace( "{receiver}", receiver.getName() )
                            .replace( "{receiver_nickname}", oldNick )
            );
        }
        else
        {
            sender.sendMessage(
                    messages.PREFIX +
                    messages.ERROR_DELETE_OTHER_PERMISSION
                            .replace( "{receiver}", receiver.getName() )
            );
        }
    }

    public void runAsPlayer( CommandSender sender )
    {
        final NickyMessages messages = Nicky.getMessages();
        if( sender.hasPermission( "nicky.del" ) )
        {
            Nick nick = new Nick( (Player) sender );
            String oldNick = nick.get();
            if ( oldNick == null ) oldNick = sender.getName();

            nick.unSet();

            sender.sendMessage(
                    messages.PREFIX +
                    messages.NICKNAME_DELETED_OWN
                        .replace("{username}", sender.getName())
                        .replace("{nickname}", oldNick)
            );
        }
        else
        {
            sender.sendMessage( messages.PREFIX + messages.ERROR_DELETE_OWN_PERMISSION );
        }
    }
}
