package me.nonit.nicky;

import me.nonit.nicky.Metrics.Graph;
import me.nonit.nicky.Metrics.Plotter;
import me.nonit.nicky.commands.DelNickCommand;
import me.nonit.nicky.commands.NickCommand;
import me.nonit.nicky.databases.MySQL;
import me.nonit.nicky.databases.SQL;
import me.nonit.nicky.databases.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Nicky extends JavaPlugin
{
    private static final String PREFIX = ChatColor.YELLOW + "[Nicky]" + ChatColor.GREEN + " ";

    private final Set<SQL> databases;
    private SQL database;
    private boolean usesTagAPI = false;

    public Nicky()
    {
        databases = new HashSet<SQL>();
    }

    @Override
    public void onEnable()
    {
        databases.add( new MySQL( this ) );
        databases.add( new SQLite( this ) );

        this.saveDefaultConfig(); // Makes a config is one does not exist.

        setupDatabase();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents( new PlayerListener( this ), this );

        if( pm.isPluginEnabled( "TagAPI" ) && getConfig().get( "tagapi" ).equals( "true" ) )
        {
            pm.registerEvents( new TagAPIListener( this ), this );
            log( "TagAPI support enabled." );
            usesTagAPI = true;
        }

        getCommand( "nick" ).setExecutor( new NickCommand( this ) );
        getCommand( "delnick" ).setExecutor( new DelNickCommand( this ) );

        if( ! database.checkConnection() )
        {
            log( "Error with database" );
            pm.disablePlugin( this );
        }

        for( Player player : Bukkit.getServer().getOnlinePlayers() )
        {
            Nick nick = new Nick( this, player );

            nick.loadNick();
        }

        loadMetrics();
    }

    @Override
    public void onDisable()
    {
        database.disconnect();
    }

    public SQL getNickDatabase()
    {
        return database;
    }

    private void loadMetrics()
    {
        try
        {
            Metrics metrics = new Metrics(this);

            Graph graphDatabaseType = metrics.createGraph( "Database Type" );

            graphDatabaseType.addPlotter( new Plotter( database.getConfigName() )
            {
                @Override
                public int getValue()
                {
                    return 1;
                }
            } );

            Graph graphTagAPI = metrics.createGraph( "TagAPI" );

            String graphTagAPIValue = "No";
            if( usesTagAPI )
            {
                graphTagAPIValue = "Yes";
            }

            graphTagAPI.addPlotter( new Plotter( graphTagAPIValue )
            {
                @Override
                public int getValue()
                {
                    return 1;
                }
            } );

            metrics.start();
        }
        catch (IOException e)
        {
            // Failed to submit the stats :-(
        }
    }

    private boolean setupDatabase()
    {
        String type = getConfig().getString("type");

        database = null;

        for ( SQL database : databases )
        {
            if ( type.equalsIgnoreCase( database.getConfigName() ) )
            {
                this.database = database;

                log( "Database set to " + database.getConfigName() + "." );

                break;
            }
        }

        if (database == null)
        {
            log( "Database type does not exist!" );

            return false;
        }

        return true;
    }

    public void log( String message ) {
        getLogger().info( message );
    }

    public static String getPrefix() { return PREFIX; }
}