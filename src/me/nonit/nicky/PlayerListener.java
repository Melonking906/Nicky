package me.nonit.nicky;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener
{
    Nicks nicks = new Nicks();

    public void onJoin( PlayerJoinEvent event )
    {
        Player player = event.getPlayer();

        if( nicks.hasNick( player ) )
        {
            nicks.setNick( player, nicks.getNick( player ) );
        }
    }
}