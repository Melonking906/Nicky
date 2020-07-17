package io.loyloy.nicky;

import io.loyloy.nicky.commands.DelNickCommand;
import io.loyloy.nicky.commands.NickCommand;
import io.loyloy.nicky.commands.NickyCommand;
import io.loyloy.nicky.commands.RealNameCommand;
import io.loyloy.nicky.databases.MySQL;
import io.loyloy.nicky.databases.SQL;
import io.loyloy.nicky.databases.SQLite;
import io.loyloy.nicky.papi.NickyExpansion;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Nicky extends JavaPlugin
{
    private static String PREFIX;
    private static JavaPlugin plugin;

    private final Set<SQL> databases;
    private static SQL DATABASE;

    private static boolean TABS;
    private static boolean UNIQUE;
    private static String NICK_PREFIX;
    private static int LENGTH;
    private static int MIN_LENGTH;
    private static String CHARACTERS;
    private static List<String> BLACKLIST;

    private static boolean USE_JOIN_LEAVE;
    private static String JOIN_MESSAGE;
    private static String LEAVE_MESSAGE;

    private static Permission VAULT_PERMS = null;
    private static HashMap<UUID, String> nicknames = new HashMap();

    public Nicky()
    {
        databases = new HashSet();
    }

    @Override
    public void onEnable()
    {
        databases.add( new MySQL( this ) );
        databases.add( new SQLite( this ) );

        setupConfig();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents( new PlayerListener(), this );

        BLACKLIST = new ArrayList();
        reloadNickyConfig();

        getCommand( "nick" ).setExecutor( new NickCommand( this ) );
        getCommand( "delnick" ).setExecutor( new DelNickCommand( this ) );
        getCommand( "realname" ).setExecutor( new RealNameCommand() );
        getCommand( "nicky" ).setExecutor( new NickyCommand( this ) );

        if( !setupPermissions() )
        {
            log( "Error connecting to Vault, make sure its installed!" );
            pm.disablePlugin( this );
            return;
        }
        if( !setupDatabase() )
        {
            log( "Error with database, are your details correct?" );
            pm.disablePlugin( this );
            return;
        }
        if( Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new NickyExpansion(this).register();
        }
    }

    @Override
    public void onDisable()
    {
        DATABASE.disconnect();
    }

    public void reloadNickyConfig()
    {
        super.reloadConfig();

        FileConfiguration config = getConfig();

        try
        {
            PREFIX = ChatColor.YELLOW + ChatColor.translateAlternateColorCodes( '&', config.get( "nicky_prefix" ).toString() ) + ChatColor.GREEN + " ";

            // Database info not set in this class.

            TABS = config.getBoolean( "tab" );
            UNIQUE = config.getBoolean( "unique" );
            NICK_PREFIX = config.get( "prefix" ).toString();
            LENGTH = Integer.parseInt( config.get( "length" ).toString() );
            MIN_LENGTH = Integer.parseInt( config.get( "min_length" ).toString() );
            CHARACTERS = config.get( "characters" ).toString();

            BLACKLIST.clear();
            BLACKLIST = config.getStringList( "blacklist" );

            USE_JOIN_LEAVE = config.getBoolean( "enable_join_leave" );
            JOIN_MESSAGE = ChatColor.translateAlternateColorCodes( '&', config.getString( "join_message" ));
            LEAVE_MESSAGE = ChatColor.translateAlternateColorCodes( '&',config.getString( "leave_message" ));
        }
        catch( Exception e )
        {
            log( "Warning - You have an error in your config." );
        }

        for( Player player : Bukkit.getServer().getOnlinePlayers() )
        {
            Nick nick = new Nick( player );

            nick.load();
        }
    }

    private void setupConfig()
    {
        saveDefaultConfig();

        FileConfiguration config = getConfig();

        // Update header.
        config.options().copyHeader();

        if( ! config.isSet( "nicky_prefix" ) )
        {
            config.set( "nicky_prefix", "[Nicky]" );
        }

        // Database config
        if( ! config.isSet( "type" ) )
        {
            config.set( "type", "sqlite" );
        }
        if( ! config.isSet( "host" ) )
        {
            config.set( "host", "localhost" );
        }
        if( ! config.isSet( "port" ) )
        {
            config.set( "port", "3306" );
        }
        if( ! config.isSet( "user" ) )
        {
            config.set( "user", "root" );
        }
        if( ! config.isSet( "password" ) )
        {
            config.set( "password", "password" );
        }
        if( ! config.isSet( "database" ) )
        {
            config.set( "database", "nicky" );
        }

        // Settings
        if( ! config.isSet( "tab" ) )
        {
            config.set( "tab", true );
        }
        if( ! config.isSet( "unique" ) )
        {
            config.set( "unique", true );
        }
        if( ! config.isSet( "prefix" ) )
        {
            config.set( "prefix", "&e~" );
        }
        if( ! config.isSet( "length" ) )
        {
            config.set( "length", 20 );
        }
        if( ! config.isSet( "min_length" ) )
        {
            config.set( "min_length", 3 );
        }
        if( ! config.isSet( "characters" ) )
        {
            config.set( "characters", "[^a-zA-Z0-9_§]" );
        }
        if( ! config.isSet( "blacklist" ) )
        {
            List<String> listOfStrings = Arrays.asList( "Melonking", "Admin" );
            config.set( "blacklist", listOfStrings );
        }

        // Join Leave
        if( ! config.isSet( "enable_join_leave" ) )
        {
            config.set( "enable_join_leave", true );
        }
        if( ! config.isSet( "join_message" ) )
        {
            config.set( "join_message", "&e{nickname} &ahas connected!" );
        }
        if( ! config.isSet( "leave_message" ) )
        {
            config.set( "leave_message", "&e{nickname} &chas disconnected!" );
        }

        saveConfig();
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
        }

        return DATABASE.checkConnection();
    }

    public static boolean removeNickname(UUID uuid) {
        if (!nicknames.containsKey(uuid)) {
            return false;
        }
        nicknames.remove(uuid);
        return true;
    }

    public static void setNickname(UUID uuid, String nickname) {
        nicknames.put(uuid, nickname);
    }

    public static String getNickname(UUID uuid) {
        if (nicknames.containsKey(uuid)) {
            return nicknames.get(uuid);
        } else {
            return plugin.getServer().getPlayer(uuid).getDisplayName();
        }
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null)
        {
            VAULT_PERMS = permissionProvider.getProvider();
        }
        return (VAULT_PERMS != null);
    }

    public static Permission getVaultPerms() { return VAULT_PERMS; }

    public static SQL getNickDatabase() { return DATABASE; }

    public static String getPrefix() { return PREFIX; }

    public static boolean isTabsUsed() { return TABS; }

    public static boolean isUnique() { return UNIQUE; }

    public static String getNickPrefix() { return NICK_PREFIX; }

    public static List<String> getBlacklist() { return BLACKLIST; }

    public static int getLength() { return LENGTH; }

    public static int getMinLength() { return MIN_LENGTH; }

    public static String getCharacters() { return CHARACTERS; }

    public static boolean useJoinLeave() { return USE_JOIN_LEAVE; }

    public static String getJoinMessage() { return JOIN_MESSAGE; }

    public static String getLeaveMessage() { return LEAVE_MESSAGE; }

    public void log( String message )
    {
        getLogger().info( message );
    }
}