package io.loyloy.nicky.databases;

import io.loyloy.nicky.Nicky;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends SQL
{
    public MySQL( Nicky plugin )
    {
        super(plugin);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> keepAlive(), 20*60*60*7, 20*60*60*7);
    }

    protected Connection getNewConnection()
    {
        Configuration config = plugin.getConfig();
        
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/" + config.getString("database") + "?useSSL=" + config.getString("useSSL");

            return DriverManager.getConnection( url, config.getString( "user" ), config.getString( "password" ) );
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public String getName()
    {
        return "MySQL";
    }

    @Override
    protected String getTable()
    {
        return plugin.getConfig().getString( "table_name" );
    }
    
    private void keepAlive() {
        try {
            getConnection().isValid(0);
        } catch (SQLException e) {
        	setConnection(getNewConnection());
        }              
    }
    
}
