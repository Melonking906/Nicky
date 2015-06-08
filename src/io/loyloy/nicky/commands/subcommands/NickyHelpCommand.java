package io.loyloy.nicky.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class NickyHelpCommand extends NickySubCommand
{
    public NickyHelpCommand()
    {
        super( "help", "nicky.help" );
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        sender.sendMessage( ChatColor.YELLOW + "---------- [Nicky Help] ----------" );
        if( sender.hasPermission( "nicky.set" ) )
        {
            sender.sendMessage( ChatColor.GREEN + "/nick <nick> " + ChatColor.GRAY + "- Sets your nickname." );
        }
        if( sender.hasPermission( "nicky.del" ) )
        {
            sender.sendMessage( ChatColor.GREEN + "/delnick" + ChatColor.GRAY + "- Deletes your nickname." );
        }
        if( sender.hasPermission( "nicky.realname" ) )
        {
            sender.sendMessage( ChatColor.GREEN + "/realname <search> " + ChatColor.GRAY + "- Lookup who owns a nick." );
        }
        if( sender.hasPermission( "nicky.help" ) )
        {
            sender.sendMessage( ChatColor.GREEN + "/nicky help " + ChatColor.GRAY + "- Loads this page." );
        }
        if( sender.hasPermission( "nicky.reload" ) )
        {
            sender.sendMessage( ChatColor.GREEN + "/nicky reload " + ChatColor.GRAY + "- Reloads nicky config." );
        }
        sender.sendMessage( ChatColor.YELLOW + "-------------------------------" );

        return true;
    }
}
