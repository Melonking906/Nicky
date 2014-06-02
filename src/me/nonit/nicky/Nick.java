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

    public boolean loadNick()
    {
        String nickname = getNick();

        if( nickname != null )
        {
            player.setDisplayName( nickname );

            refreshTagIfEnabled();

            return true;
        }

        return false;
    }

    public void unLoadNick()
    {
        database.removeFromCache( uuid );
        player.setDisplayName( player.getName() );

        refreshTagIfEnabled();
    }

    public String getNick()
    {
        return database.downloadNick( uuid );
    }

    public void setNick( String nick )
    {
        if( getNick() != null )
        {
            unSetNick();
        }

        database.uploadNick( uuid, nick );
    }

    public void unSetNick()
    {
        database.deleteNick( uuid );
    }

    public void refreshNick()
    {
        unLoadNick();
        loadNick();
    }

    private void refreshTagIfEnabled()
    {
        if( plugin.isTagAPIUsed() )
        {
            TagAPI.refreshPlayer( player );
        }
    }
}