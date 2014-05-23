package me.nonit.nicky.databases;

import me.nonit.nicky.Nicky;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQL extends SQL
{
    public MySQL( Nicky plugin )
    {
        super(plugin, true);
    }

    protected Connection getNewConnection()
    {
        ConfigurationSection config = getConfigSection();

        try
        {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/" + config.getString("database");

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
}