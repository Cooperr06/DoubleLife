package de.cooperr.doublelife.command;

import de.cooperr.doublelife.DoubleLife;
import de.cooperr.doublelife.util.Timer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TimeCommand implements CommandExecutor {
    
    private final DoubleLife plugin;
    
    public TimeCommand(DoubleLife plugin) {
        this.plugin = plugin;
        plugin.registerCommand("time", this);
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Du musst ein Spieler sein, um diesen Befehl auszuführen!", NamedTextColor.DARK_RED));
            return true;
        }
        
        var player = (Player) sender;
        
        if (args.length != 0) {
            player.sendMessage(Component.text("Verwende: /time"));
            return true;
        }
        
        var playerOffline = plugin.getServer().getOfflinePlayer(player.getUniqueId());
        var configTime = plugin.getConfig().getInt("teams.team" + plugin.getPlayerTeamManager().getTeamOfPlayer(playerOffline)
            .getTeamNumber() + ".members." + player.getUniqueId() + ".time");
        var timerTime = plugin.getPlaytimeManager().getPlayerTimers().get(playerOffline).getTime();
        var remainingTime = 60 * 60 * 2 - (timerTime + configTime);
        
        if (plugin.getPlayerTeamManager().getTeamOfPlayer(playerOffline).getLives() == 0) {
            player.sendMessage(Component.text("Du darfst unbegrenzt spielen!", NamedTextColor.RED));
            return true;
        }
        
        if (remainingTime > 30) {
            player.sendMessage(Component.text("Du hast noch " + Timer.formatTime(remainingTime) + " Zeit, bis du gekickt wirst!", NamedTextColor.RED));
        } else {
            player.sendMessage(Component.text("Du wirst in weniger als 30s gekickt!", NamedTextColor.RED));
        }
        
        return true;
    }
}
