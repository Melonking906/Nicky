package io.loyloy.nicky;

import io.loyloy.nicky.databases.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
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

        //FORMAT EXISTING NICKNAMES, LEGACY SUPPORT - WILL BE REMOVED EVENTUALLY. 18/9/2018
        if( nickname != null )
        {
            nickname = format( nickname );
        }

        return nickname;
    }

    public void set( String nickname )
    {
        if( get() != null )
        {
            unSet();
        }

        // Safeguard against invalid nicknames.
        if ( !isValid( nickname ) )
        {
            throw new AssertionError( "Invalid nickname passed through checks" );
        }
        
        // Set nickname.
        nickname = formatWithFlags( nickname, false );
        database.uploadNick( uuid, nickname, offlinePlayer.getName() );
        refresh();
    }

    public void unSet()
    {
        database.deleteNick( uuid );
        refresh();
    }

    public String format( String nickname )
    {
        return formatWithFlags( nickname, true );
    }

    public String formatWithFlags( String nickname, boolean addPrefix )
    {
        if( nickname.length() > Nicky.getMaxLength() )
        {
            nickname = nickname.substring( 0, Nicky.getMaxLength() + 1 );
        }

        nickname = Utils.translateColors( nickname, offlinePlayer );

        if( addPrefix && !Nicky.getNickPrefix().equals( "" ) )
        {
            String prefix = ChatColor.translateAlternateColorCodes( '&', Nicky.getNickPrefix() );
            nickname = prefix + nickname;
        }

        if( !Nicky.getCharacters().equals( "" ) )
        {
            nickname = nickname.replaceAll( Nicky.getCharacters(), "" );
        }

        return nickname + ChatColor.RESET;
    }

    public static boolean isUsed( String nick )
    {
        if( Nicky.isUnique() )
        {
            return database.isUsed( nick );
        }
        return false;
    }

    public static boolean isBlacklisted( String nick )
    {
        nick = ChatColor.translateAlternateColorCodes( '&', nick );

        for( String word : Nicky.getBlacklist() )
        {
            if( ChatColor.stripColor( nick.toLowerCase() ).contains( word.toLowerCase() ) )
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
            Pattern invalidRegex = Pattern.compile(invalidCharacters);
            if ( invalidRegex.matcher(nick).find() ) {
                return false;
            }
        }
        
        return true;
    }

    public static List<SQL.SearchedPlayer> searchGet( String search )
    {
        return database.searchNicks( search );
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
