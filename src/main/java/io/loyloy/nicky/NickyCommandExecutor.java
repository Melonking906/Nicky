package io.loyloy.nicky;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * An async command executor.
 * This stops database operations from blocking the main thread.
 */
public abstract class NickyCommandExecutor implements CommandExecutor
{
    protected final Nicky plugin;

    public NickyCommandExecutor( Nicky plugin )
    {
        this.plugin = plugin;
    }
    
    /**
     * The function called when someone runs the command.
     * 
     * @param sender The command sender.
     * @param cmd The command.
     * @param label The command label.
     * @param args The command arguments.
     */
    protected abstract void execute( Player sender, Command cmd, String label, String[] args );

    /**
     * The function called when a command block or console runs the command.
     *
     * @param sender The command sender.
     * @param cmd The command.
     * @param label The command label.
     * @param args The command arguments.
     */
    protected abstract void executeFromConsole( CommandSender sender, Command cmd, String label, String[] args );

    /**
     * Runs a task on the main thread.
     * Whenever a player interacts with the Bukkit API, this should be used.
     * @param runnable The runnable.
     */
    protected void sync( Runnable runnable ) {
        this.plugin.getServer().getScheduler().runTask( this.plugin, runnable );
    }

    public final boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        this.plugin.getServer().getScheduler().runTaskAsynchronously( this.plugin, () -> {
            try {
                if (!(sender instanceof Player)) {
                    this.executeFromConsole(sender, cmd, commandLabel, args);
                    return;
                }
                this.execute((Player) sender, cmd, commandLabel, args);
            } catch ( Exception ex ) {
                sender.sendMessage( ChatColor.RED + "Failed to execute " + commandLabel + ". This is a bug." );
                ex.printStackTrace();
            }
        });
        return true;
    }

}
