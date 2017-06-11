package io.loyloy.nicky;

import io.loyloy.nicky.databases.SQL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class Nick
{
    private Player player;
    private static SQL database = Nicky.getNickDatabase();
    private String uuid;

    public Nick( Player player )
    {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
    }

    public boolean load()
    {
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
        database.removeFromCache( uuid );
        player.setDisplayName( player.getName() );
    }

    public String get()
    {
        String nickname = database.downloadNick( uuid );

        if( nickname != null )
        {
            if( isBlacklisted( nickname ) )
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

        database.uploadNick( uuid, nick, player.getName() );
        refresh();
    }

    public void unSet()
    {
        database.deleteNick( uuid );
        refresh();
    }

    public String format( String nickname )
    {
        if( nickname.length() > Nicky.getLength() )
        {
            nickname = nickname.substring( 0, Nicky.getLength() + 1 );
        }

        nickname = Nicky.translateColors( nickname, player );

        if( !Nicky.getCharacters().equals( "" ) )
        {
            nickname = nickname.replaceAll( Nicky.getCharacters(), "" );
        }

        if( !Nicky.getNickPrefix().equals( "" ) )
        {
            String prefix = ChatColor.translateAlternateColorCodes( '&', Nicky.getNickPrefix() );
            nickname = prefix + nickname;
        }

        return nickname + ChatColor.RESET;
    }

    public void updatePlayerName()
    {
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
}