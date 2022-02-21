package io.loyloy.nicky.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class NickySubCommand
{
    private final String name;
    private final String permission;

    public NickySubCommand( String name, String permission )
    {
        this.name = name;
        this.permission = permission;
    }

    public String getName()
    {
        return name;
    }

    public String getPermission()
    {
        return permission;
    }

    public abstract boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args);
}