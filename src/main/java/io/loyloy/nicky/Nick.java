package io.loyloy.nicky;

import io.loyloy.nicky.databases.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nick
{
    private static final SQL database = Nicky.getNickDatabase();

    private OfflinePlayer offlinePlayer;
    private String uuid;

    public Nick( Player player )
    {
        this.offlinePlayer = Bukkit.getOfflinePlayer( player.getUniqueId() );
        this.uuid = offlinePlayer.getUniqueId().toString();
    }

    public Nick( OfflinePlayer offlinePlayer )
    {
        this.offlinePlayer = offlinePlayer;
        this.uuid = offlinePlayer.getUniqueId().toString();
    }

    //Requires player to be online
    public boolean load()
    {
        if( !offlinePlayer.isOnline() )
        {
            return false;
        }

        Player player = offlinePlayer.getPlayer();
        String nickname = get();

        if( nickname != null )
        {
            //Strip blacklisted nicknames on load
            if( isBlacklisted( nickname ) && !player.hasPermission( "nicky.noblacklist" ) )
            {
                unSet();
                return false;
            }

            Nicky.setNickname(player.getUniqueId(), nickname);

            return true;
        }
        return false;
    }

    //Requires player to be online
    public boolean unLoad()
    {
        if( !offlinePlayer.isOnline() )
        {
            return false;
        }

        Player player = offlinePlayer.getPlayer();

        database.removeFromCache( uuid );
        Nicky.removeNickname(player.getUniqueId());

        return true;
    }

    public String get()
    {
        String nickname = database.downloadNick( uuid );

        if( nickname != null )
        {
            nickname = formatForServer( nickname );
        }

        return nickname;
    }

    public void set( String nickname )
    {
        if( get() != null )
        {
            unSet();
        }
        
        // Replace chat color codes with ampersand.
        nickname = nickname.replace( ChatColor.COLOR_CHAR, '&' );

        // Safeguard against invalid nicknames.
        if ( !isValid( nickname ) )
        {
            throw new AssertionError( "Invalid nickname passed through checks" );
        }

        // Set nickname.
        nickname = formatForDatabase( nickname );
        database.uploadNick( uuid, nickname, offlinePlayer.getName() );
        refresh();
    }

    public void unSet()
    {
        database.deleteNick( uuid );
        refresh();
    }

    /**
     * Formats a nickname for database lookup/storage.
     * Use this when comparing nicknames to the stored one.
     * 
     * @param nickname The nickname to format.
     * @return The formatted nickname.
     */
    public static String formatForDatabase( String nickname )
    {
        if( nickname.length() > Nicky.getMaxLength() )
        {
            nickname = nickname.substring( 0, Nicky.getMaxLength() + 1 );
        }
        
        return stripInvalid(nickname);
    }

    /**
     * Formats a nickname for server usage.
     * Use this when setting a player's displayname.
     *
     * @param nickname The nickname to format.
     * @return The formatted nickname.
     */
    public static String formatForServer( String nickname )
    {
        StringBuilder builder = new StringBuilder();

        
        // Add prefix.
        if( !Nicky.getNickPrefix().isEmpty() )
        {
            String prefix = ChatColor.translateAlternateColorCodes( '&', Nicky.getNickPrefix() );
            builder.append(prefix);
        }
        
        // Add nickname.
        builder.append( ChatColor.translateAlternateColorCodes( '&', stripInvalid( nickname ) ) );
        
        // Add reset character.
        builder.append( ChatColor.RESET.toString() );
        return builder.toString();
    }

    /**
     * Checks if a nickname is in use by any player.
     * This considers color codes to be equivalent.
     * 
     * @param nick The nickname.
     * @return True if the nickname is used AND the unique configuration value is true.
     */
    public static boolean isUsed( String nick )
    {
        if( Nicky.isUnique() )
        {
            return getOwner( nick ) != null;
        }
        return false;
    }

    /**
     * Gets the UUID of the player who is using a nickname.
     * This considers color codes to be equivalent.
     *
     * @param nick The nickname.
     * @return The player UUID, or null if nobody owns the nickname.
     */
    public static UUID getOwner( String nick )
    {
        String nickPlain = ChatColor.stripColor( ChatColor.translateAlternateColorCodes( '&', formatForDatabase( nick ) ) );
        return database.getOwner( nickPlain );
    }

    public static boolean isBlacklisted( String nick )
    {
        String strippedNick = ChatColor.stripColor( ChatColor.translateAlternateColorCodes( '&', nick ) )
            .toLowerCase();

        for( String word : Nicky.getBlacklist() )
        {
            if( strippedNick.contains( word.toLowerCase() ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the nickname is valid.
     * This checks for valid characters and length.
     * 
     * @param nick The nickname to check.
     * @return True if valid, false otherwise.
     */
    public static boolean isValid( String nick )
    {
        if ( nick.length() < Nicky.getMinLength() || nick.length() > Nicky.getMaxLength() || nick.length() > SQL.NICKNAME_COLUMN_MAX ) {
            return false;
        }
        
        String invalidCharacters = Nicky.getCharacters();
        if ( !invalidCharacters.isEmpty() )
        {
            Pattern invalidRegex = Pattern.compile( invalidCharacters );
            Matcher matcher = invalidRegex.matcher( nick );
            while ( matcher.find() ) {
                if ( nick.charAt(matcher.start()) == '&' ) {
                    continue;
                }
                
                return false;
            }
        }
        
        return true;
    }

    /**
     * @deprecated Use {@link io.loyloy.nicky.NickQuery} instead. 
     */
    @Deprecated
    public static List<SQL.SearchedPlayer> searchGet( String search )
    {
        return database.searchNicks( search );
    }

    /**
     * Strips invalid characters from a player's nickname.
     * @param nickname The nickname.
     * @return The stripped nickname.
     */
    private static String stripInvalid( String nickname )
    {
        String allowedRegex = Nicky.getCharacters();
        if( allowedRegex.isEmpty() ) return nickname;
        
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile( Nicky.getCharacters() );
        for ( char c : nickname.toCharArray() )
        {
            if ( c == '&' || !pattern.matcher( String.valueOf( c ) ).matches() ) {
                sb.append( c );
            }
        }

        return sb.toString();
    }

    private void refresh()
    {
        unLoad();
        load();
    }

    public OfflinePlayer getOfflinePlayer()
    {
        return offlinePlayer;
    }
}
