package io.loyloy.nicky.databases;

import io.loyloy.nicky.Nicky;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLite extends SQL
{
    private final Nicky plugin;

    public SQLite( Nicky plugin )
    {
        super(plugin);

        this.plugin = plugin;
    }

    protected Connection getNewConnection()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");

            return DriverManager.getConnection( "jdbc:sqlite:" + new File( plugin.getDataFolder(), "nicknames.db" ).getAbsolutePath() );
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public String getName()
    {
        return "SQLite";
    }
}
