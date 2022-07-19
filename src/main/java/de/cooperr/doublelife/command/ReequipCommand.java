package de.cooperr.doublelife.command;

import de.cooperr.doublelife.DoubleLife;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReequipCommand implements CommandExecutor {
    
    private final DoubleLife plugin;
    
    public ReequipCommand(DoubleLife plugin) {
        this.plugin = plugin;
        plugin.registerCommand("reequip", this);
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        if (sender instanceof Player) {
            sender.sendMessage(Component.text("Du darfst kein Spieler sein, um diesen Befehl auszuführen!", NamedTextColor.DARK_RED));
            return true;
        }
        
        if (args.length != 1) {
            sender.sendMessage(Component.text("Benutze: /reequip <player>", NamedTextColor.DARK_RED));
            return true;
        }
        
        var player = plugin.getServer().getPlayer(args[0]);
        if (player == null || !player.isOnline()) {
            sender.sendMessage(Component.text("Dieser Spieler ist nicht online oder existiert nicht!", NamedTextColor.DARK_RED));
            return true;
        }
        
        var pathToPlayer = "teams.team" + plugin.getPlayerTeamManager().getTeamOfPlayer(plugin.getServer()
            .getOfflinePlayer(player.getUniqueId())).getTeamNumber() + ".members." + player.getUniqueId();
        var lastExp = plugin.getConfig().getString(pathToPlayer + ".last-exp");
        assert lastExp != null;
        
        var experienceSplit = lastExp.split("/");
        
        player.getInventory().setContents(plugin.getBase64().read(plugin.getConfig().getString(pathToPlayer + ".last-inventory")));
        player.setLevel(Integer.parseInt(experienceSplit[0]));
        player.setExp(Float.parseFloat(experienceSplit[1]));
        
        plugin.getServer().broadcast(player.displayName().append(Component.text("'s letztes Inventar wurde wiederhergestellt!", NamedTextColor.GOLD, TextDecoration.BOLD)));
        return true;
    }
}
