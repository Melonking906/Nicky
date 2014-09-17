package me.nonit.nicky.databases;

import me.nonit.nicky.Nicky;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SQL
{
    private Connection connection;
    private HashMap<String, String> cache = new HashMap<String, String>();

    protected Nicky plugin;

    public SQL( Nicky plugin )
    {
        this.plugin = plugin;

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

    protected abstract String getName();

    public String getConfigName()
    {
        return getName().toLowerCase().replace(" ", "");
    }

    private ArrayList<HashMap<String,String>> query( String sql, boolean hasReturn )
    {
        if( !checkConnection() )
        {
            plugin.getLogger().info( "Error with database" );
            return null;
        }

        PreparedStatement statement;
        try
        {
            statement = connection.prepareStatement( sql );

            if( ! hasReturn )
            {
                statement.execute();
                return null;
            }

            ResultSet set = statement.executeQuery();

            ResultSetMetaData md = set.getMetaData();
            int columns = md.getColumnCount();

            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>( 50 );

            while( set.next() )
            {
                HashMap<String,String> row = new HashMap<String,String>( columns );
                for( int i = 1; i <= columns; ++i )
                {
                    row.put( md.getColumnName( i ), set.getObject( i ).toString() );
                }
                list.add( row );
            }

            if( list.isEmpty() )
            {
                return null;
            }

            return list;
        }
        catch( SQLException e )
        {
            e.printStackTrace();
        }

        return null;
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

                query( "CREATE TABLE IF NOT EXISTS nicky (uuid varchar(36) NOT NULL, nick varchar(64) NOT NULL, PRIMARY KEY (uuid))", false );
            }
        }
        catch( SQLException e )
        {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public void disconnect()
    {
        cache.clear();

        try
        {
            if (connection != null)
            {
                connection.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public String downloadNick( String uuid )
    {
        String nick;

        if( cache.containsKey( uuid ) )
        {
            return cache.get( uuid );
        }

        ArrayList<HashMap<String,String>> data = query( "SELECT nick FROM nicky WHERE uuid = '" + uuid + "';", true );

        nick = data.get( 0 ).get( uuid );

        cache.put( uuid, nick );

        return nick;
    }

    public HashMap<String,String> searchNicks( String search )
    {
        HashMap<String,String> nicknames = null;

        String[] characters = search.split( "(?!^)" );
        String sqlSearch = "";
        for( String character : characters )
        {
            sqlSearch = "%" + character;
        }
        sqlSearch = sqlSearch + "%";

        ArrayList<HashMap<String,String>> data = query( "SELECT uuid, nick FROM nicky WHERE nick LIKE '" + sqlSearch + "';", true );

        for( Map.Entry<String,String> player : data.get( 0 ).entrySet() )
        {
            if( player.getKey() != null && player.getValue() != null )
            {
                nicknames.put( player.getKey(), player.getValue() );
            }
        }

        return nicknames;
    }

    public boolean isUsed( String nick )
    {
        boolean result;

        ArrayList<HashMap<String,String>> data = query( "SELECT nick FROM nicky WHERE nick = '" + nick + "';", true );

        result = data.get( 0 ).isEmpty();

        return result == false;
    }

    public void removeFromCache( String uuid )
    {
        if( cache.containsKey( uuid ) )
        {
            cache.remove( uuid );
        }
    }

    public void uploadNick( String uuid, String nick )
    {
        query( "INSERT INTO nicky (uuid, nick) VALUES ('" + uuid + "','" + nick + "');", false );
    }

    public void deleteNick( String uuid )
    {
        query( "DELETE FROM nicky WHERE uuid = '" + uuid + "';", false );
    }
}
