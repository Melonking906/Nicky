package io.loyloy.nicky.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import io.loyloy.nicky.Nick;
import io.loyloy.nicky.Nicky;
import io.loyloy.nicky.NickyCommandExecutor;
import io.loyloy.nicky.NickyMessages;
import io.loyloy.nicky.databases.SQL;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand extends NickyCommandExecutor
{

    public NickCommand( Nicky plugin )
    {
        super( plugin );
    }

    /**
     * Checks a nickname to see if its valid.
     * @param nickname The nickname to check.
     * @param player The player trying to set the nickname.
     * @return null on valid nickname, or a string if there's a problem.
     */
    private String nicknameProblems( String nickname, CommandSender player )
    {
        final NickyMessages messages = Nicky.getMessages();
        String strippedNickname = ChatColor.stripColor( ChatColor.translateAlternateColorCodes( '&', nickname ) );
        
        if( strippedNickname.length() < Nicky.getMinLength() )
        {
            return messages.PREFIX +
                    messages.ERROR_NICKNAME_TOO_SHORT
                            .replace("{min}", String.valueOf( Nicky.getMinLength() ) )
                            .replace( "{max}", String.valueOf( Nicky.getMaxLength() ) );
        }
        
        if( strippedNickname.length() > Nicky.getMaxLength() || nickname.length() > SQL.NICKNAME_COLUMN_MAX )
        {
            return messages.PREFIX +
                    messages.ERROR_NICKNAME_TOO_LONG
                            .replace( "{min}", String.valueOf( Nicky.getMinLength() ) )
                            .replace( "{max}", String.valueOf( Math.min( Nicky.getMaxLength(), SQL.NICKNAME_COLUMN_MAX ) ) );
        }
        
        // Check for disallowed colors.
        Set<Character> colors = new HashSet<>();
        int index = -1;
        while ((index = nickname.indexOf('&', index + 1)) != -1)
        {
            if (index == nickname.length() - 1) break;
            char code = nickname.charAt(index + 1);
            if ( colors.contains(code) ) continue;
            
            // Check for an invalid color.
            // I'm not sure if Bukkit will throw or return null, so we cover both bases here.
            try {
                if ( ChatColor.getByChar(code) == null ) throw new ClassCastException();
            } catch ( ClassCastException ex ) {
                return messages.PREFIX +
                        messages.ERROR_NICKNAME_COLOR_INVALID
                                .replace( "{code}", String.valueOf( code ) );
            }
            
            // Check if the color is allowed. 
            if ( player != null && !player.hasPermission("nicky.color." + code) )
            {
                return messages.PREFIX + 
                        messages.ERROR_NICKNAME_COLOR_NO_PERMISSION
                                .replace( "{code}", String.valueOf( code ) );
            }

            // Add the color to skip checks for next time.
            colors.add( code );
        }
        
        // Check for color limit.
        if ( Nicky.useColorLimit() )
        {
            int colorsUsed = colors.size();
            int colorsMax = ChatColor.values().length;
            boolean allowed = false;
            for ( int i = colorsUsed; i <= colorsMax; i++ )
            {
                if ( player.hasPermission( "nicky.limit.color." + i ) )
                {
                    allowed = true;
                    break;
                }
            }
            
            if ( !allowed )
            {
                return messages.PREFIX +
                        messages.ERROR_NICKNAME_COLOR_TOO_MANY;
            }
        }
        
        // Check for invalid characters.
        if ( !Nick.isValid( strippedNickname ) )
        {
            return messages.PREFIX + messages.ERROR_NICKNAME_INVALID;
        }
        
        return null;
    }

    @Override
    public void execute( Player sender, Command cmd, String commandLabel, String[] args )
    {
        if( sender.hasPermission( "nicky.set.other" ) && args.length >= 2 )
        {
            runAsAdmin( sender, args );
        }
        else
        {
            runAsPlayer( sender, args );
        }
    }

    @Override
    protected void executeFromConsole( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        runAsAdmin( sender, args );
    }
    
    private void setNickname( CommandSender sender, OfflinePlayer target, String nickname )
    {
        final NickyMessages messages = Nicky.getMessages();
        final String nicknamePlain = ChatColor.stripColor( ChatColor.translateAlternateColorCodes( '&', nickname ) );
        
        // If the nickname is the same as the player name, run /delnick.
        if( nickname.equals( target.getName() ) )
        {
            (new DelNickCommand( plugin )).delNickname( sender, target );
            return;
        }

        // If there's problems using that nickname, let the player know.
        String problems = nicknameProblems( nickname, sender );
        if ( problems != null ) {
            sender.sendMessage( problems );
            return;
        }

        // Check if the nick is blacklisted.
        Nick nick = new Nick( target );
        if( Nick.isBlacklisted( nickname ) && !sender.hasPermission( "nicky.noblacklist" ) )
        {
            sender.sendMessage(
                    messages.PREFIX +
                    messages.ERROR_NICKNAME_BLACKLISTED
                            .replace( "{nickname}", nicknamePlain )
            );
            return;
        }

        // Check if the nick is used.
        if( Nicky.isUnique() )
        {
            UUID owner = Nick.getOwner( nickname );
            if ( owner != null && !target.getUniqueId().equals(owner) )
            {
                sender.sendMessage(
                        messages.PREFIX +
                        messages.ERROR_NICKNAME_TAKEN
                                .replace( "{nickname}", nicknamePlain )
                );
                return;
            }
        }

        // Set the nickname.
        nick.set( nickname );
        nickname = nick.get();

        // Notify the player(s).
        if( target == sender )
        {
            sender.sendMessage(
                    messages.PREFIX +
                    messages.NICKNAME_CHANGED_OWN
                            .replace( "{username}", sender.getName() )
                            .replace( "{nickname}", nickname )
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
                                messages.NICKNAME_WAS_CHANGED
                                        .replace("{nickname}", nickname)
                                        .replace("{admin}", sender.getName())
                                        .replace("{admin_nickname}", senderNickname)
                );
            }

            sender.sendMessage(
                    messages.PREFIX +
                        messages.NICKNAME_CHANGED_OTHER
                                .replace( "{receiver}", target.getName() )
                                .replace( "{receiver_nickname}", nickname )
            );
        }
    }

    @SuppressWarnings("deprecation")
    private void runAsAdmin( CommandSender sender, String[] args )
    {
        final NickyMessages messages = Nicky.getMessages();
        
        OfflinePlayer receiver = plugin.getServer().getOfflinePlayer( args[0] );
        String nickname = args[1];

        // Show no permission message if no perms.
        if( !sender.hasPermission( "nicky.set.other" ) )
        {
            sender.sendMessage(
                    messages.PREFIX +
                    messages.ERROR_CHANGE_OTHER_PERMISSION
                        .replace( "{receiver}", args[0] )
            );
            return;
        }
        
        // Show usage message if invalid usage.
        if( args.length > 2 )
        {
            sender.sendMessage( messages.COMMAND_NICK_USAGE_ADMIN );
            return;
        }

        // Make sure the target player has played at least once before.
        if( !receiver.hasPlayedBefore() )
        {
            sender.sendMessage(
                    messages.PREFIX +
                    messages.ERROR_PLAYER_NOT_FOUND.replace( "{player}", args[0] )
            );
            return;
        }
        
        setNickname( sender, receiver, nickname );
    }

    private void runAsPlayer( Player sender, String[] args )
    {
        final NickyMessages messages = Nicky.getMessages();

        // Show no permission message if no perms.
        if( !sender.hasPermission( "nicky.set" ) )
        {
            sender.sendMessage( messages.PREFIX + messages.ERROR_CHANGE_OWN_PERMISSION );
            return;
        }

        // Show usage message if invalid usage.
        if( args.length != 1 )
        {
            sender.sendMessage( messages.COMMAND_NICK_USAGE_PLAYER );
            return;
        }

        setNickname( sender, sender, args[0] );
    }
}
