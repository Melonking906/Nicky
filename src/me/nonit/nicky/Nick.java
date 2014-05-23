package me.nonit.nicky;

import me.nonit.nicky.databases.SQL;
import org.bukkit.entity.Player;

public class Nick
{
    private Nicky plugin;
    private Player player;
    private SQL database;
    private String uuid;

    public Nick(Nicky plugin, Player player, SQL database)
    {
        this.plugin = plugin;
        this.player = player;
        this.database = database;

        this.uuid = player.getUniqueId().toString();
    }

    private String getNick()
    {
        return database.downloadNick( uuid );
    }

    public boolean setNick()
    {
        String nick = getNick();

        if( nick != null )
        {
            player.setDisplayName( nick );
            return true;
        }

        return false;
    }

    public void unsetNick()
    {
        player.setDisplayName( player.getName() );

        database.deleteNick( uuid );
    }
}