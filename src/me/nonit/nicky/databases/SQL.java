package me.nonit.nicky.databases;

import me.nonit.nicky.Nicky;

import java.sql.*;
import java.util.*;

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

        updateTables();
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
            }
        }
        catch( SQLException e )
        {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    private void updateTables()
    {
        int version;

        query( "CREATE TABLE IF NOT EXISTS nicky (uuid varchar(36) NOT NULL, nick varchar(64) NOT NULL, name varchar(32) NOT NULL, PRIMARY KEY (uuid))", false );
        query( "CREATE TABLE IF NOT EXISTS nicky_version (version int(2) NOT NULL)", false );

        ArrayList<HashMap<String,String>> results;
        results = query( "SELECT version FROM nicky_version", true );
        if( results == null )
        {
            query( "INSERT INTO nicky_version (version) VALUES (1);", false );
            version = 1;
        }
        else
        {
            version = Integer.parseInt( results.get( 0 ).get( "version" ) );
        }

        if( version < 2 )
        {
            query( "ALTER TABLE nicky ADD name varchar(32) NOT NULL", false );
            query( "DELETE FROM nicky_version", false );
            query( "INSERT INTO nicky_version (version) VALUES (2);", false );
            version = 2;
        }
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
        if( data == null )
        {
            return null;
        }

        nick = data.get( 0 ).get( "nick" );

        cache.put( uuid, nick );

        return nick;
    }

    public List<SearchedPlayer> searchNicks( String search )
    {
        List<SearchedPlayer> results = new ArrayList<SearchedPlayer>();

        String[] characters = search.split( "(?!^)" );
        String sqlSearch = "";
        for( String character : characters )
        {
            sqlSearch = "%" + character;
        }
        sqlSearch = sqlSearch + "%";

        ArrayList<HashMap<String,String>> data = query( "SELECT uuid, nick, name FROM nicky WHERE nick LIKE '" + sqlSearch + "';", true );
        if( data == null )
        {
            return null;
        }

        for( HashMap<String,String> row : data )
        {
            results.add( new SearchedPlayer( row.get( "uuid" ), row.get( "nick" ), row.get( "name" ) ) );
        }

        return results;
    }

    public class SearchedPlayer
    {
        private String uuid;
        private String nick;
        private String name;

        public SearchedPlayer( String uuid, String nick, String name  )
        {
            this.uuid = uuid;
            this.nick = nick;
            this.name = name;
        }

        public String getUuid()
        {
            return uuid;
        }

        public String getNick()
        {
            return nick;
        }

        public String getName()
        {
            return name;
        }
    }

    public boolean isUsed( String nick )
    {
        ArrayList<HashMap<String,String>> data = query( "SELECT nick FROM nicky WHERE nick = '" + nick + "';", true );

        return data != null;
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

    public void updatePlayerName( String uuid, String name )
    {
        query( "UPDATE nicky SET name = '" + name + "' WHERE uuid = '" + uuid + "';", false );
    }
}
