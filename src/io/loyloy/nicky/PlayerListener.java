package io.loyloy.nicky;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
    @EventHandler
    public void onJoin( PlayerJoinEvent event )
    {
        Nick nick = new Nick( event.getPlayer() );

        nick.updatePlayerName();
        nick.load();
    }

    @EventHandler
    public void onExit( PlayerQuitEvent event )
    {
        Nick nick = new Nick( event.getPlayer() );

        nick.unLoad();
    }
}