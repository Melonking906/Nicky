package me.nonit.nicky;

import me.nonit.nicky.databases.SQL;
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
        return database.downloadNick( uuid );
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