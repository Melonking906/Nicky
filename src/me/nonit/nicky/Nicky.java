package me.nonit.nicky;

import me.nonit.nicky.commands.NickCommand;
import me.nonit.nicky.commands.UnNickCommand;
import me.nonit.nicky.databases.MySQL;
import me.nonit.nicky.databases.SQL;
import me.nonit.nicky.databases.SQLite;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class Nicky extends JavaPlugin
{
    private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Nicky" + ChatColor.GRAY + "]" + ChatColor.GREEN + " ";

    private final Set<SQL> databases;
    private SQL database;

    public Nicky()
    {
        databases = new HashSet<SQL>();
    }

    @Override
    public void onEnable()
    {
        databases.add(new MySQL(this));
        databases.add(new SQLite(this));

        this.saveDefaultConfig(); // Makes a config is one does not exist.

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents( new PlayerListener( this ), this );

        getCommand( "nick" ).setExecutor( new NickCommand( this ) );
        getCommand( "unnick" ).setExecutor( new UnNickCommand( this ) );
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
        getLogger().info( "[Nicky] " + message );
    }

    public static String getPrefix() { return PREFIX; }
}