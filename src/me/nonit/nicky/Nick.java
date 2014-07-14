package me.nonit.nicky;

import me.nonit.nicky.databases.SQL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.kitteh.tag.TagAPI;

public class Nick
{
    private Player player;
    private SQL database;
    private String uuid;

    public Nick(Player player)
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
                player.setPlayerListName( nickname );
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
            if( ! Nicky.getCharacters().equals( "" ) )
            {
                nickname = nickname.replaceAll( Nicky.getCharacters(), "" );
            }

            nickname = nickname.substring( 0, Nicky.getLength() );

            if( isBlacklisted( nickname ) )
            {
                unSet();
                return null;
            }

            if( player.hasPermission( "nicky.color" ) )
            {
                nickname = ChatColor.translateAlternateColorCodes( '&', nickname );
            }

            if( ! Nicky.getNickPrefix().equals( "" ) )
            {
                String prefix = ChatColor.translateAlternateColorCodes( '&', Nicky.getNickPrefix() );
                nickname = prefix + nickname;
            }

            nickname = nickname + ChatColor.RESET;
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

    public boolean isUsed( String nick )
    {
        if( Nicky.isUnique() )
        {
            return database.isUsed( nick );
        }
        return false;
    }

    public static boolean isBlacklisted( String nick )
    {
        for( String word : Nicky.getBlacklist() )
        {
            if( ChatColor.stripColor( nick.toLowerCase() ).contains( word.toLowerCase() ) );
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