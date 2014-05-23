package me.nonit.nicky;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener
{
    Nicky plugin;

    public PlayerListener( Nicky plugin )
    {
        this.plugin = plugin;
    }

    public void onJoin( PlayerJoinEvent event )
    {
        Nick nick = new Nick( plugin, event.getPlayer() );

        nick.loadNick();
    }
}