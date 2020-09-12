package io.loyloy.nicky.commands;

import io.loyloy.nicky.Nick;
import io.loyloy.nicky.Nicky;
import io.loyloy.nicky.NickyCommandExecutor;
import io.loyloy.nicky.NickyMessages;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelNickCommand extends NickyCommandExecutor
{
    public DelNickCommand( Nicky plugin )
    {
        super( plugin );
    }

    protected void execute( Player sender, Command cmd, String commandLabel, String[] args )
    {
        final NickyMessages messages = Nicky.getMessages();
        
        // Run command as admin or player.
        if( sender.hasPermission( "nicky.del.other" ) && args.length > 0 )
        {
            runAsAdmin( sender, args );
        }
        else
        {
            runAsPlayer( sender, args );
        }
    }

    @Override
    protected void executeFromConsole(CommandSender sender, Command cmd, String label, String[] args) {
        runAsAdmin( sender, args );
    }

    void delNickname( CommandSender sender, OfflinePlayer target )
    {
        final NickyMessages messages = Nicky.getMessages();
        
        // Delete the target's nickname.
        Nick nick = new Nick( target );
        String oldNick = nick.get();
        if ( oldNick == null ) oldNick = target.getName();

        nick.unSet();

        // Notify the player(s).
        if ( sender == target )
        {
            sender.sendMessage(
                    messages.PREFIX +
                    messages.NICKNAME_DELETED_OWN
                            .replace("{username}", sender.getName())
                            .replace("{nickname}", oldNick)
            );
        } else {
            String senderNickname = sender.getName();
            if ( sender instanceof Player ) {
                Nick senderNick = new Nick( (Player) sender );
                String senderNickString = senderNick.get();
                if ( senderNickString != null ) {
                    senderNickname = senderNickString;
                }
            }

            if( target.isOnline() )
            {
                target.getPlayer().sendMessage(
                        messages.PREFIX +
                        messages.NICKNAME_WAS_DELETED
                                .replace("{nickname}", oldNick)
                                .replace("{admin}", sender.getName())
                                .replace("{admin_nickname}", senderNickname)
                );
            }

            sender.sendMessage(
                    messages.PREFIX +
                    messages.NICKNAME_DELETED_OTHER
                            .replace( "{receiver}", target.getName() )
                            .replace( "{receiver_nickname}", oldNick )
            );
        }
    }
    
    @SuppressWarnings("deprecation")
    public void runAsAdmin( CommandSender sender, String[] args )
    {
        final NickyMessages messages = Nicky.getMessages();
        OfflinePlayer target = plugin.getServer().getOfflinePlayer( args[0] );

        // Show no permission message if no perms.
        if( !(sender.hasPermission( "nicky.del.other" ) || sender.hasPermission("nicky.set.other")) )
        {
            sender.sendMessage(
                    messages.PREFIX +
                    messages.ERROR_DELETE_OTHER_PERMISSION
                            .replace( "{receiver}", args[0] )
            );
            return;
        }

        // Show usage message if invalid usage.
        if( args.length > 1 )
        {
            sender.sendMessage( messages.COMMAND_DELNICK_USAGE_ADMIN );
            return;
        }
        
        // Make sure the target player has played at least once before.
        if( !target.hasPlayedBefore() && new Nick(target).get() == null )
        {
            sender.sendMessage(
                    messages.PREFIX +
                    messages.ERROR_PLAYER_NOT_FOUND
                            .replace( "{player}", args[0] )
            );
            return;
        }

        delNickname( sender, target );
    }

    public void runAsPlayer( Player sender, String[] args )
    {
        final NickyMessages messages = Nicky.getMessages();
        
        // Show no permission message if no perms.
        if( !sender.hasPermission( "nicky.del" ) )
        {
            sender.sendMessage(
                    messages.PREFIX +
                    messages.ERROR_DELETE_OWN_PERMISSION
                        .replace( "{receiver}", sender.getName() )
            );
            return;
        }

        // Show usage message if invalid usage.
        if( args.length != 0 )
        {
            sender.sendMessage( messages.COMMAND_DELNICK_USAGE_PLAYER );
            return;
        }
        
        delNickname( sender, sender );
    }
}
