package me.nonit.nicky;

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

import java.util.HashSet;
import java.util.Set;

public class Nicky extends JavaPlugin
{
    private static final String PREFIX = ChatColor.YELLOW + "[Nicky]" + ChatColor.GREEN + " ";

    private final Set<SQL> databases;
    private SQL database;

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

    private boolean setupDatabase()
    {
        String type = getConfig().getString("type");

        database = null;

        for ( SQL database : databases )
        {
            if ( type.equalsIgnoreCase( database.getConfigName() ) )
            {
                this.database = database;

                log( "Database set to " + database.getConfigName() );

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