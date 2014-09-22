package me.nonit.nicky.commands;

import me.nonit.nicky.Nick;
import me.nonit.nicky.Nicky;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor
{
    Nicky plugin;

    public NickCommand( Nicky plugin )
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if( ! (sender instanceof Player) )
        {
            runAsConsole( args );
        }
        else if( args.length >= 2 )
        {
            runAsAdmin( sender, args );
        }
        else
        {
            runAsPlayer( sender, args );
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    private void runAsConsole( String[] args )
    {
        if( args.length >= 2  )
        {
            Player receiver = plugin.getServer().getPlayer( args[0] );

            if( receiver == null )
            {
                plugin.log( "Could not find '" + args[0] + "', are you sure they are online?");
                return;
            }

            String nickname = args[1];

            if( nickname.equals( receiver.getName() ) )
            {
                new DelNickCommand( plugin ).runAsConsole( args );
                return;
            }

            if( nickname.length() < Nicky.getMinLength() )
            {
                plugin.log( "Nicks must be at least " + Nicky.getMinLength() + " characters." );
                return;
            }

            if( Nick.isBlacklisted( nickname ) )
            {
                plugin.log( "Sorry but " + nickname + " contains a blacklisted word :(" );
                return;
            }

            Nick nick = new Nick( receiver );

            if( Nick.isUsed( nickname ) )
            {
                plugin.log( "Sorry the nick " + nickname + "  is already in use :(" );
                return;
            }

            nick.set( nickname );
            nickname = nick.get();

            receiver.sendMessage( Nicky.getPrefix() + "Your nickname has been set to " + ChatColor.YELLOW + nickname + ChatColor.GREEN + " by console!" );
            plugin.log( receiver.getName() + "'s nick has been set to " + nickname );
        }
        else
        {
            plugin.log( "Usage: /nick <player> <nickname>" );
        }
    }

    @SuppressWarnings("deprecation")
    private void runAsAdmin( CommandSender sender, String[] args )
    {
        Player receiver = plugin.getServer().getPlayer( args[0] );

        if( receiver == null )
        {
            sender.sendMessage( Nicky.getPrefix() + "Could not find " + ChatColor.YELLOW + args[0] + ChatColor.GREEN + ", are you sure they are online?");
            return;
        }

        String nickname = args[1];

        if( sender.hasPermission( "nicky.set.other" ) )
        {
            if( nickname.equals( receiver.getName() ) )
            {
                new DelNickCommand( plugin ).runAsAdmin( receiver, args );
                return;
            }

            if( nickname.length() < Nicky.getMinLength() )
            {
                sender.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Nicks must be at least " + ChatColor.YELLOW + Nicky.getMinLength() + ChatColor.GREEN + " characters!" );
                return;
            }

            Nick nick = new Nick( receiver );

            if( Nick.isBlacklisted( nickname ) )
            {
                sender.sendMessage( Nicky.getPrefix() + "Sorry but " + ChatColor.YELLOW + nick.format( nickname ) + ChatColor.GREEN + " contains a blacklisted word :(" );
                return;
            }

            if( Nick.isUsed( nickname ) )
            {
                sender.sendMessage( Nicky.getPrefix() + "Sorry the nick " + ChatColor.YELLOW + nick.format( nickname ) + ChatColor.GREEN + "  is already in use :(" );
                return;
            }

            nick.set( nickname );
            nickname = nick.get();

            receiver.sendMessage( Nicky.getPrefix() + "Your nickname has been set to " + ChatColor.YELLOW + nickname + ChatColor.GREEN + " by " + ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + "!" );
            sender.sendMessage( Nicky.getPrefix() + "You have set " + ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + "'s nickname to " + ChatColor.YELLOW + nickname + ChatColor.GREEN + "." );
        }
        else
        {
            sender.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Sorry, you don't have permission to set other players nicks." );
        }
    }

    private void runAsPlayer( CommandSender sender, String[] args )
    {
        Player player = (Player) sender;

        if( sender.hasPermission( "nicky.set" ) )
        {
            if( args.length >= 1 )
            {
                String nickname = args[0];

                if( nickname.equals( sender.getName() ) )
                {
                    new DelNickCommand( plugin ).runAsPlayer( sender );
                    return;
                }

                if( nickname.length() < Nicky.getMinLength() )
                {
                    player.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Your nick must be at least " + ChatColor.YELLOW + Nicky.getMinLength() + ChatColor.GREEN + " characters!" );
                    return;
                }

                Nick nick = new Nick( player );

                if( Nick.isBlacklisted( nickname ) )
                {
                    player.sendMessage( Nicky.getPrefix() + "Sorry but " + ChatColor.YELLOW + nick.format( nickname ) + ChatColor.GREEN + " contains a blacklisted word :(" );
                    return;
                }

                if( Nick.isUsed( nickname ) )
                {
                    player.sendMessage( Nicky.getPrefix() + "Sorry the nick " + ChatColor.YELLOW + nick.format( nickname ) + ChatColor.GREEN + " is already in use :(" );
                    return;
                }

                nick.set( nickname );
                nickname = nick.get();

                player.sendMessage( Nicky.getPrefix() + "Your nickname has been set to " + ChatColor.YELLOW + nickname + ChatColor.GREEN + " !" );
            }
            else
            {
                player.sendMessage( Nicky.getPrefix() + "To set a nick do " + ChatColor.YELLOW + "/nick <nickname>" );
            }
        }
        else
        {
            player.sendMessage( Nicky.getPrefix() + ChatColor.RED + "Sorry, you don't have permission to set a nick." );
        }
    }
}