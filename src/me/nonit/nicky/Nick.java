package me.nonit.nicky;

import me.nonit.nicky.databases.SQL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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

            if( Nicky.isUpdateTab() )
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
            if( player.hasPermission( "nicky.color" ) )
            {
                nickname = ChatColor.translateAlternateColorCodes( '&', nickname );
            }

            if( Nicky.isPrefixNicks() )
            {
                nickname = "~" + nickname;
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
        if( Nicky.isUniqueNicks() )
        {
            return database.isUsed( nick );
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