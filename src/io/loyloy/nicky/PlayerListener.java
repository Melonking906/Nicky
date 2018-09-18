package io.loyloy.nicky;

import org.bukkit.Bukkit;
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

        if( Nicky.useJoinLeave() )
        {
            event.setJoinMessage( null );
            String message = Nicky.getJoinMessage().replace( "{nickname}", getNicknameOrName( nick ) );
            Bukkit.broadcastMessage( message );
        }

        nick.load();
        nick.updatePlayerName();
    }

    @EventHandler
    public void onExit( PlayerQuitEvent event )
    {
        Nick nick = new Nick( event.getPlayer() );

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
        if( name != null )
        {
            name = nick.format( name );
        }
        else
        {
            name = nick.getPlayer().getDisplayName();
        }
        return name;
    }
}