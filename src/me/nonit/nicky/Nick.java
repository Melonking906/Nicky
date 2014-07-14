package me.nonit.nicky;

import me.nonit.nicky.databases.SQL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

public class Nick
{
    private Nicky plugin;
    private Player player;
    private SQL database;
    private String uuid;

    public Nick(Nicky plugin, Player player)
    {
        this.plugin = plugin;
        this.player = player;
        database = plugin.getNickDatabase();

        this.uuid = player.getUniqueId().toString();
    }

    public boolean load()
    {
        String nickname = get();

        if( nickname != null )
        {
            player.setDisplayName( nickname );

            if( plugin.isUpdateTab() )
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

            if( plugin.isPrefixNicks() )
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
        if( plugin.isUniqueNicks() )
        {
            return database.isUsed( nick );
        }
        return false;
    }

    private void refresh()
    {
        unLoad();
        load();

        if( plugin.isTagAPIUsed() )
        {
            TagAPI.refreshPlayer( player );
        }
    }
}