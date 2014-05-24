package me.nonit.nicky.databases;

import me.nonit.nicky.Nicky;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SQL
{
    private final boolean supportsModification;
    private Connection connection;
    private Nicky plugin;

    public SQL( Nicky plugin, boolean supportsModification )
    {
        this.plugin = plugin;

        this.supportsModification = supportsModification;

        plugin.getServer().getScheduler().runTaskTimerAsynchronously( plugin, new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if( connection != null && ! connection.isClosed() )
                    {
                        connection.createStatement().execute( "/* ping */ SELECT 1" );
                    }
                }
                catch( SQLException e )
                {
                    connection = getNewConnection();
                }
            }
        }, 60 * 20, 60 * 20 );
    }

    protected abstract Connection getNewConnection();

    public boolean query( String sql ) throws SQLException
    {
        return connection.createStatement().execute( sql );
    }

    protected abstract String getName();

    public String getConfigName()
    {
        return getName().toLowerCase().replace(" ", "");
    }

    public ConfigurationSection getConfigSection()
    {
        return plugin.getConfig().getConfigurationSection(getConfigName());
    }

    public boolean checkConnection()
    {
        try
        {
            if( connection == null || connection.isClosed() )
            {
                connection = getNewConnection();

                if( connection == null || connection.isClosed() )
                {
                    return false;
                }

                query( "CREATE TABLE IF NOT EXISTS nicky (uuid varchar(36), nick varchar(64) NOT NULL)" );

                if( supportsModification )
                {
                    query( "ALTER TABLE nicky MODIFY nick varchar(64) NOT NULL" );
                }

                try
                {
                    query( "ALTER TABLE nicky MODIFY uuid varchar(36) NOT NULL" );
                }
                catch( Exception e ) {}
            }
        }
        catch( SQLException e )
        {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public String downloadNick( String uuid )
    {
        checkConnection();

        String nick = null;
        PreparedStatement statement;
        try
        {
            statement = connection.prepareStatement( "SELECT nick FROM nicky WHERE uuid == " + uuid );

            ResultSet set = statement.executeQuery();

            while( set.next() )
            {
                nick = set.getString( "nick" );
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return nick;
    }

    public void uploadNick( String uuid, String nick )
    {
        checkConnection();

        if( downloadNick( uuid ) != null )
        {
            deleteNick( uuid );
        }

        PreparedStatement statement;
        try
        {
            statement = connection.prepareStatement( "INSERT INTO nicky (uuid, nick) VALUES (" + uuid + "," + nick + ");" );

            statement.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void deleteNick( String uuid )
    {
        checkConnection();

        PreparedStatement statement;
        try
        {
            statement = connection.prepareStatement( "DELETE FROM nicky WHERE uuid == " + uuid );

            statement.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
