package io.loyloy.nicky.commands.subcommands;

import io.loyloy.nicky.Nicky;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class NickyReloadCommand extends NickySubCommand
{
    private final Nicky plugin;

    public NickyReloadCommand( Nicky plugin )
    {
        super( "reload", "nicky.reload" );

        this.plugin = plugin;
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        plugin.reloadNickyConfig();

        sender.sendMessage( Nicky.getPrefix() + "Configuration reloaded!" );
        return true;
    }
}