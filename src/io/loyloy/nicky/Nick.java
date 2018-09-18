package io.loyloy.nicky;

import io.loyloy.nicky.databases.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class Nick
{
    private Player player;
    private OfflinePlayer offlinePlayer;
    private static SQL database = Nicky.getNickDatabase();
    private String uuid;

    public Nick( Player player )
    {
        this.player = player;
        this.offlinePlayer = Bukkit.getOfflinePlayer( player.getUniqueId() );
        this.uuid = player.getUniqueId().toString();
    }

    public Nick( OfflinePlayer offlinePlayer )
    {
        this.player = null;
        this.offlinePlayer = offlinePlayer;
        this.uuid = offlinePlayer.getUniqueId().toString();
        if( offlinePlayer.isOnline() )
        {
            player = offlinePlayer.getPlayer();
        }
    }

    public boolean load()
    {
        if( player == null ) { return false; }

        String nickname = get();

        if( nickname != null )
        {
            player.setDisplayName( nickname );

            if( Nicky.isTabsUsed() )
            {
                if( nickname.length() > 16 )
                {
                    player.setPlayerListName( nickname.substring( 0, 15 ) );
                }
                else
                {
                    player.setPlayerListName( nickname );
                }
            }

            return true;
        }
        return false;
    }

    public void unLoad()
    {
        if( player == null ) { return; }

        database.removeFromCache( uuid );
        player.setDisplayName( player.getName() );
    }

    public String get()
    {
        String nickname = database.downloadNick( uuid );

        if( nickname != null )
        {
            if( player != null && isBlacklisted( nickname ) && !player.hasPermission( "nicky.noblacklist" ) )
            {
                unSet();
                return null;
            }

            nickname = format( nickname );
        }

        return nickname;
    }

    public void set( String nick )
    {
        if( get() != null )
        {
            unSet();
        }

        //Strip unwanted chars here to avoid DB issues
        if( nick.length() > Nicky.getLength() )
        {
            nick = nick.substring( 0, Nicky.getLength() + 1 );
        }
        if( !Nicky.getCharacters().equals( "" ) )
        {
            nick = nick.replaceAll( Nicky.getCharacters(), "" );
        }

        database.uploadNick( uuid, nick, offlinePlayer.getName() );
        refresh();
    }

    public void unSet()
    {
        database.deleteNick( uuid );
        refresh();
    }

    public String format( String nickname )
    {
        if( player == null ) { return null; }

        if( nickname.length() > Nicky.getLength() )
        {
            nickname = nickname.substring( 0, Nicky.getLength() + 1 );
        }

        nickname = Nicky.translateColors( nickname, player );

        if( !Nicky.getNickPrefix().equals( "" ) )
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

    public void updatePlayerName()
    {
        if( player == null ) { return; }

        database.updatePlayerName( uuid, player.getName() );
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

    public static List<SQL.SearchedPlayer> searchGet( String search )
    {
        return database.searchNicks( search );
    }

    private void refresh()
    {
        unLoad();
        load();
    }

    public Player getPlayer() { return player; }
}