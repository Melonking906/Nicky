package me.nonit.nicky;

import me.nonit.nicky.commands.DelNickCommand;
import me.nonit.nicky.commands.NickCommand;
import me.nonit.nicky.commands.RealNameCommand;
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
import java.util.List;
import java.util.Set;

public class Nicky extends JavaPlugin
{
    private static final String PREFIX = ChatColor.YELLOW + "[Nicky]" + ChatColor.GREEN + " ";

    private final Set<SQL> databases;
    private static SQL DATABASE;

    private static boolean TAGAPI = false;
    private static boolean TABS;
    private static boolean UNIQUE;
    private static String NICK_PREFIX;
    private static int LENGTH;
    private static String CHARACTERS;
    private static List<String> BLACKLIST;

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

        if( pm.isPluginEnabled( "TagAPI" ) && getConfig().getBoolean( "tagapi" ) )
        {
            pm.registerEvents( new TagAPIListener( this ), this );
            log( "TagAPI link enabled." );
            TAGAPI = true;
        }

        reloadConfig();

        getCommand( "nick" ).setExecutor( new NickCommand( this ) );
        getCommand( "delnick" ).setExecutor( new DelNickCommand( this ) );
        getCommand( "realname" ).setExecutor( new RealNameCommand( this ) );

        if( ! DATABASE.checkConnection() )
        {
            log( "Error with DATABASE" );
            pm.disablePlugin( this );
        }

        for( Player player : Bukkit.getServer().getOnlinePlayers() )
        {
            Nick nick = new Nick( player );

            nick.load();
        }

        loadMetrics();
    }

    @Override
    public void onDisable()
    {
        DATABASE.disconnect();
    }

    public void reloadConfig()
    {
        try
        {
            TABS = getConfig().getBoolean( "tab" );
            UNIQUE = getConfig().getBoolean( "unique" );

            NICK_PREFIX = getConfig().get( "prefix" ).toString();
            LENGTH = Integer.parseInt( getConfig().get( "length" ).toString() );
            CHARACTERS = getConfig().get( "characters" ).toString();

            BLACKLIST = getConfig().getStringList( "blacklist" );
        }
        catch( Exception e )
        {
            log( "Warning - You have an error in your config." );
        }
    }

    private void loadMetrics()
    {
        try
        {
            Metrics metrics = new Metrics(this);

            Metrics.Graph graphDatabaseType = metrics.createGraph( "Database Type" );

            graphDatabaseType.addPlotter( new Metrics.Plotter( DATABASE.getConfigName() ) {
                @Override
                public int getValue() {
                    return 1;
                }
            } );

            Metrics.Graph graphTagAPI = metrics.createGraph( "TagAPI" );

            String graphTagAPIValue = "No";
            if( TAGAPI )
            {
                graphTagAPIValue = "Yes";
            }

            graphTagAPI.addPlotter( new Metrics.Plotter( graphTagAPIValue ) {
                @Override
                public int getValue() {
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

        DATABASE = null;

        for ( SQL database : databases )
        {
            if ( type.equalsIgnoreCase( database.getConfigName() ) )
            {
                DATABASE = database;

                log( "Database set to " + database.getConfigName() + "." );

                break;
            }
        }

        if ( DATABASE == null)
        {
            log( "Database type does not exist!" );

            return false;
        }

        return true;
    }

    public void log( String message ) {
        getLogger().info( message );
    }

    public static SQL getNickDatabase()
    {
        return DATABASE;
    }

    public static String getPrefix() { return PREFIX; }

    public static boolean isTagAPIUsed()
    {
        return TAGAPI;
    }

    public static boolean isTabsUsed() { return TABS; }

    public static boolean isUnique() { return UNIQUE; }

    public static String getNickPrefix() { return NICK_PREFIX; }

    public static List<String> getBlacklist() { return BLACKLIST; }

    public static int getLength() { return LENGTH; }

    public static String getCharacters() { return CHARACTERS; }
}