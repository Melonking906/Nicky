package io.loyloy.nicky.commands.subcommands;

import io.loyloy.nicky.Nicky;
import io.loyloy.nicky.NickyMessages;
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
        final NickyMessages messages = Nicky.getMessages();
        
        sender.sendMessage( messages.HELP_HEADER );
        if( sender.hasPermission( "nicky.set" ) )
        {
            sender.sendMessage( messages.HELP_COMMAND_NICK );
        }
        if( sender.hasPermission( "nicky.del" ) )
        {
            sender.sendMessage( messages.HELP_COMMAND_DELNICK );
        }
        if( sender.hasPermission( "nicky.realname" ) )
        {
            sender.sendMessage( messages.HELP_COMMAND_REALNAME );
        }
        if( sender.hasPermission( "nicky.help" ) )
        {
            sender.sendMessage( messages.HELP_COMMAND_HELP );
        }
        if( sender.hasPermission( "nicky.reload" ) )
        {
            sender.sendMessage( messages.HELP_COMMAND_RELOAD );
        }
        sender.sendMessage( messages.HELP_FOOTER );

        return true;
    }
}
