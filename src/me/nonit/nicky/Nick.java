package me.nonit.nicky;

import me.nonit.nicky.databases.SQL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

public class Nick
{
    private Player player;
    private static SQL database;
    private String uuid;

    public Nick( Player player )
    {
        this.player = player;
        database = Nicky.getNickDatabase();

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
                    player.setPlayerListName( nickname.substring( 0, 16 ) );
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

        database.uploadNick( uuid, nick );
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

        if( player.hasPermission( "nicky.color.normal" ) )
        {
            nickname = Nicky.translateNormalColorCodes( nickname );
        }

        if( player.hasPermission( "nicky.color.extra" ) )
        {
            nickname = Nicky.translateExtraColorCodes( nickname );
        }

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

    private void refresh()
    {
        unLoad();
        load();

        if( Nicky.isTagAPIUsed() )
        {
            TagAPI.refreshPlayer( player );
        }
    }
}