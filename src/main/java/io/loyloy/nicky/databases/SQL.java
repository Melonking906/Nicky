package io.loyloy.nicky.databases;

import io.loyloy.nicky.Nicky;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class SQL
{
    static public final int NICKNAME_COLUMN_MAX = 64;
    
    private Connection connection;
    private HashMap<String, String> cache = new HashMap<>();

    protected Nicky plugin;

    public SQL( Nicky plugin )
    {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> keepAlive(), 20*60*60*7, 20*60*60*7);
    }

    protected abstract Connection getNewConnection();

    protected abstract String getName();
    
    protected abstract String getTable();

    public String getConfigName()
    {
        return getName().toLowerCase().replace(" ", "");
    }

    protected interface StatementInitializer {
        void initialize(PreparedStatement statement) throws SQLException;
    }
    
    private synchronized ArrayList<HashMap<String,String>> query( String sql, StatementInitializer initializer, boolean hasReturn )
    {
        if( ! checkConnection() )
        {
            plugin.getLogger().info( "Error with database" );
            return null;
        }
        
        try
        {
            PreparedStatement statement = connection.prepareStatement( sql.replace( "$table", this.getTable() ) );
            initializer.initialize(statement);
            
            if( ! hasReturn )
            {
                statement.execute();
                return null;
            }

            ResultSet set = statement.executeQuery();

            ResultSetMetaData md = set.getMetaData();
            int columns = md.getColumnCount();

            ArrayList<HashMap<String,String>> list = new ArrayList<>( 50 );

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

                updateTables();
            }
        }
        catch( SQLException e )
        {
            e.printStackTrace();

            return false;
        }

        return true;
    }
    
    private void keepAlive() {
        try {
            connection.isValid(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }              
    }
    
    private void updateTables()
    {
        query( 
                "CREATE TABLE IF NOT EXISTS $table (uuid varchar(36) NOT NULL, nick varchar(?) NOT NULL, nick_plain varchar(?) NOT NULL, name varchar(32) NOT NULL, PRIMARY KEY (uuid))",
                statement -> {
                    statement.setInt(1, NICKNAME_COLUMN_MAX);
                    statement.setInt(2, NICKNAME_COLUMN_MAX);
                },
                false
        );
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

        ArrayList<HashMap<String,String>> data = query("SELECT nick FROM $table WHERE uuid = ?;", 
                statement -> statement.setString(  1, uuid ), 
                true
        );
        
        if( data == null )
        {
            // Store null to avoid spammy queries.
            cache.put( uuid, null );

            return null;
        }

        nick = data.get( 0 ).get( "nick" );

        cache.put( uuid, nick );

        return nick;
    }

    public List<SearchedPlayer> searchNicks( String search )
    {
        List<SearchedPlayer> results = new ArrayList<>();
        
        ArrayList<HashMap<String, String>> data = query( 
                "SELECT uuid, nick, name FROM $table WHERE nick LIKE ?;",
                statement -> statement.setString(1, "%" + search + "%"),
                true
        );

        if( data == null )
        {
            return null;
        }

        for( HashMap<String,String> row : data )
        {
            results.add(new SearchedPlayer(row.get("uuid"), row.get("nick"), row.get("name")));
        }

        return results;
    }

    public static class SearchedPlayer
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

    public UUID getOwner( String nick )
    {

        ArrayList<HashMap<String,String>> data = query( "SELECT uuid FROM $table WHERE nick_plain = ?;",
                statement -> statement.setString( 1, ChatColor.stripColor( ChatColor.translateAlternateColorCodes( '&', nick ) ) ),
                true
        );

        if ( data == null ) return null;
        return UUID.fromString( data.get( 0 ).get( "uuid" ) );
    }

    public void removeFromCache( String uuid )
    {
        cache.remove( uuid );
    }

    public void uploadNick( String uuid, String nick, String name )
    {
        cache.put( uuid, nick );

        query( "INSERT INTO $table (uuid, nick, nick_plain, name) VALUES (?, ?, ?, ?);", 
                statement -> {
                    statement.setString( 1, uuid );
                    statement.setString( 2, nick );
                    statement.setString( 3, ChatColor.stripColor( ChatColor.translateAlternateColorCodes( '&', nick ) ) );
                    statement.setString( 4, name );
                },
                false
        );
    }

    public void deleteNick( String uuid )
    {
        cache.put( uuid, null );

        query( "DELETE FROM $table WHERE uuid = ?;", 
                statement -> statement.setString( 1, uuid ),
                false
        );
    }

    public void updatePlayerName( String uuid, String name )
    {
        query( "UPDATE $table SET name = ? WHERE uuid = ?;", 
                statement -> {
                    statement.setString( 1, name );
                    statement.setString( 2, uuid );
                },
                false
        );
    }
}
