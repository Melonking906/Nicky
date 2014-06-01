package me.nonit.nicky;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
    private Nicky plugin;

    public PlayerListener( Nicky plugin )
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin( PlayerJoinEvent event )
    {
        Nick nick = new Nick( plugin, event.getPlayer() );

        nick.loadNick();
    }

    @EventHandler
    public void onExit( PlayerQuitEvent event )
    {
        Nick nick = new Nick( plugin, event.getPlayer() );

        nick.unLoadNick();
    }
}