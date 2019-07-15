package io.loyloy.nicky.commands;

import io.loyloy.nicky.Nicky;
import io.loyloy.nicky.commands.subcommands.NickyHelpCommand;
import io.loyloy.nicky.commands.subcommands.NickyReloadCommand;
import io.loyloy.nicky.commands.subcommands.NickySubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class NickyCommand implements CommandExecutor
{
    private final List<NickySubCommand> commands;

    public NickyCommand( Nicky plugin )
    {
        commands = new ArrayList<>();

        commands.add( new NickyHelpCommand() );
        commands.add( new NickyReloadCommand( plugin ) );
    }

    public boolean onCommand( CommandSender sender, Command arg1, String arg2, String args[] )
    {
        String subCommand = "help";

        if( args.length > 0 )
        {
            subCommand = args[0];
        }

        for( NickySubCommand command : commands )
        {
            if( command.getName().equalsIgnoreCase( subCommand ) )
            {
                if( ! sender.hasPermission( command.getPermission() ) )
                {
                    sender.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Sorry you don't have permission to do that!" );
                    return true;
                }

                command.onCommand( sender, arg1, arg2, args );
            }
        }

        return true;
    }
}
