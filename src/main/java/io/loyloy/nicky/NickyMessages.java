package io.loyloy.nicky;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NickyMessages
{
    public final String PREFIX;
    public final String PLAYER_JOIN;
    public final String PLAYER_LEAVE;
    public final String ERROR_PLAYER_NOT_FOUND;
    public final String ERROR_SEARCH_TOO_SHORT;
    public final String ERROR_NICKNAME_TOO_SHORT;
    public final String ERROR_NICKNAME_TOO_LONG;
    public final String ERROR_NICKNAME_TAKEN;
    public final String ERROR_NICKNAME_BLACKLISTED;
    public final String ERROR_NICKNAME_INVALID;
    public final String ERROR_NICKNAME_COLOR_INVALID;
    public final String ERROR_NICKNAME_COLOR_NO_PERMISSION;
    public final String ERROR_CHANGE_OWN_PERMISSION;
    public final String ERROR_CHANGE_OTHER_PERMISSION;
    public final String ERROR_REALNAME_PERMISSION;
    public final String ERROR_DELETE_OWN_PERMISSION;
    public final String ERROR_DELETE_OTHER_PERMISSION;
    public final String ERROR_GENERIC_PERMISSION;
    public final String NICKNAME_CHANGED_OWN;
    public final String NICKNAME_CHANGED_OTHER;
    public final String NICKNAME_WAS_CHANGED;
    public final String NICKNAME_DELETED_OWN;
    public final String NICKNAME_DELETED_OTHER;
    public final String NICKNAME_WAS_DELETED;
    public final String REALNAME_NOBODY;
    public final String REALNAME_FOUND;
    public final String REALNAME_FOUND_ENTRY;
    public final String COMMAND_NICK_USAGE_ADMIN;
    public final String COMMAND_NICK_USAGE_PLAYER;
    public final String COMMAND_DELNICK_USAGE_ADMIN;
    public final String COMMAND_DELNICK_USAGE_PLAYER;
    public final String HELP_HEADER;
    public final String HELP_FOOTER;
    public final String HELP_COMMAND_NICK;
    public final String HELP_COMMAND_DELNICK;
    public final String HELP_COMMAND_REALNAME;
    public final String HELP_COMMAND_HELP;
    public final String HELP_COMMAND_RELOAD;
    
    public NickyMessages( FileConfiguration messages, FileConfiguration config )
    {
        // Prefix
        String configPrefix = ChatColor.translateAlternateColorCodes( '&', first( config.get( "nicky_prefix" ), messages.get( "prefix" ) ).toString() );
        PREFIX = ChatColor.stripColor( configPrefix ).isEmpty() ? "" : ChatColor.YELLOW + configPrefix + ChatColor.RESET + " ";
        
        // Join/Leave Messages
        PLAYER_JOIN = ChatColor.translateAlternateColorCodes( '&', first( config.get( "join_message" ), messages.get( "join" ) ).toString() );
        PLAYER_LEAVE = ChatColor.translateAlternateColorCodes( '&', first( config.get( "leave_message" ), messages.get( "leave" ) ).toString() );
        
        // Error Messages
        ERROR_PLAYER_NOT_FOUND = message (messages, "error_player_not_found" );
        ERROR_SEARCH_TOO_SHORT = message (messages, "error_search_too_short" );
        ERROR_NICKNAME_TOO_SHORT = message (messages, "error_nickname_too_short" );
        ERROR_NICKNAME_TOO_LONG = message (messages, "error_nickname_too_long" );
        ERROR_NICKNAME_TAKEN = message (messages, "error_nickname_taken" );
        ERROR_NICKNAME_BLACKLISTED = message (messages, "error_nickname_blacklisted" );
        ERROR_NICKNAME_INVALID = message (messages, "error_nickname_invalid" );
        ERROR_NICKNAME_COLOR_INVALID = message (messages, "error_nickname_color_invalid" );
        ERROR_NICKNAME_COLOR_NO_PERMISSION = message (messages, "error_nickname_color_no_permission" );
        ERROR_CHANGE_OWN_PERMISSION = message (messages, "error_change_own_permission" );
        ERROR_CHANGE_OTHER_PERMISSION = message (messages, "error_change_other_permission" );
        ERROR_DELETE_OWN_PERMISSION = message (messages, "error_delete_own_permission" );
        ERROR_DELETE_OTHER_PERMISSION = message (messages, "error_delete_other_permission" );
        ERROR_REALNAME_PERMISSION = message (messages, "error_realname_permission" );
        ERROR_GENERIC_PERMISSION = message (messages, "error_generic_permission" );
        
        // Command Messages
        COMMAND_NICK_USAGE_ADMIN = message (messages, "command_nick_usage_admin" );
        COMMAND_NICK_USAGE_PLAYER = message (messages, "command_nick_usage_player" );
        COMMAND_DELNICK_USAGE_ADMIN = message (messages, "command_delnick_usage_admin" );
        COMMAND_DELNICK_USAGE_PLAYER = message (messages, "command_delnick_usage_player" );
        NICKNAME_CHANGED_OWN = message (messages, "nickname_changed_own" );
        NICKNAME_CHANGED_OTHER = message (messages, "nickname_changed_other" );
        NICKNAME_WAS_CHANGED = message (messages, "nickname_was_changed" );
        NICKNAME_DELETED_OWN = message (messages, "nickname_deleted_own" );
        NICKNAME_DELETED_OTHER = message (messages, "nickname_deleted_other" );
        NICKNAME_WAS_DELETED = message (messages, "nickname_was_deleted" );
        REALNAME_NOBODY = message (messages, "realname_nobody" );
        REALNAME_FOUND = message (messages, "realname_found" );
        REALNAME_FOUND_ENTRY = message (messages, "realname_found_entry" );

        // Help Messages
        HELP_HEADER = message (messages, "help_header" );
        HELP_FOOTER = message (messages, "help_footer" );
        HELP_COMMAND_NICK = message (messages, "help_command_nick" );
        HELP_COMMAND_DELNICK = message (messages, "help_command_delnick" );
        HELP_COMMAND_REALNAME = message (messages, "help_command_realname" );
        HELP_COMMAND_HELP = message (messages, "help_command_help" );
        HELP_COMMAND_RELOAD = message (messages, "help_command_reload" );
    }
    
    private final String message( FileConfiguration config, String key ) {
        String code = ChatColor.translateAlternateColorCodes( '&', Objects.requireNonNull( config.getString( key ) ) );
        config.set(key, null); // DEBUG: Remove the key so it errors if we accidentally use it twice.
        return code;
    }

    /**
     * Returns the first non-null value.
     * @param messages The message strings/numbers/etc.
     * @return The first non-null value, or null if all are null.
     */
    static private final String first( Object... messages ) 
    {
        for  ( Object message : messages ) 
        {
            if ( message != null )
            {
                return message.toString();
            }
        }
        return null;
    }
    
}
