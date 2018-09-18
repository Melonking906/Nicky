package io.loyloy.nicky;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
    @EventHandler
    public void onJoin( PlayerJoinEvent event )
    {
        final Player player = event.getPlayer();
        final Nick nick = new Nick( player );

        if( Nicky.useJoinLeave() )
        {
            event.setJoinMessage( null );
            String message = Nicky.getJoinMessage().replace( "{nickname}", getNicknameOrName( nick ) );
            Bukkit.broadcastMessage( message );
        }

        nick.load();

        Nicky.getNickDatabase().updatePlayerName( player.getUniqueId().toString(), player.getName() );
    }

    @EventHandler
    public void onExit( PlayerQuitEvent event )
    {
        final Player player = event.getPlayer();
        final Nick nick = new Nick( player );

        if( Nicky.useJoinLeave() )
        {
            event.setQuitMessage( null );
            String message = Nicky.getLeaveMessage().replace( "{nickname}", getNicknameOrName( nick ) );
            Bukkit.broadcastMessage( message );
        }

        nick.unLoad();
    }

    private String getNicknameOrName( Nick nick )
    {
        String name = nick.get();
        if( name == null )
        {
            name = nick.getOfflinePlayer().getName();
        }
        return name;
    }
}