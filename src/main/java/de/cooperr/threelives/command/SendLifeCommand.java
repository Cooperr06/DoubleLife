package de.cooperr.threelives.command;

import de.cooperr.threelives.ThreeLives;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SendLifeCommand implements TabExecutor {
    
    private final ThreeLives plugin;
    
    public SendLifeCommand(ThreeLives plugin) {
        this.plugin = plugin;
        plugin.registerCommand("sendlife", this);
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        if (!(sender instanceof Player)) {
            sendErrorMessage(sender, "Du musst ein Spieler sein, um diesen Befehl auszuführen!");
            return true;
        }
        
        var player = (Player) sender;
        
        if (args.length != 1) {
            sendErrorMessage(player, "Benutze: /sendlife <target>");
            return true;
        }
        
        var target = plugin.getServer().getPlayer(args[0]);
        
        if (target == null) {
            sendErrorMessage(player, "Dieser Spieler existiert nicht!");
            return true;
        }
        
        var playerSection = plugin.getConfig().getConfigurationSection("players." + player.getUniqueId());
        assert playerSection != null;
        var targetSection = plugin.getConfig().getConfigurationSection("players." + target.getUniqueId());
        assert targetSection != null;
        
        if (playerSection.getInt("lives") < 1) {
            sendErrorMessage(player, "Du kannst keine Leben verschenken, da du keine hast!");
            return true;
        }
        
        plugin.getLivesManager().changeLife(player, '-');
        plugin.getLivesManager().changeLife(target, '+');
        
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 0);
        target.playSound(target.getLocation(), Sound.ITEM_TOTEM_USE, 1, 0);
        target.playEffect(EntityEffect.TOTEM_RESURRECT);
        
        player.sendMessage(Component.text("Du hast ", NamedTextColor.GOLD)
            .append(target.displayName())
            .append(Component.text(" ein Leben geschenkt!", NamedTextColor.GOLD)));
        target.sendMessage(player.displayName()
            .append(Component.text(" hat dir ein Leben geschenkt!", NamedTextColor.GOLD)));
        return true;
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    
        var tabCompletion = new ArrayList<String>();
    
        if (args.length == 0) {
            tabCompletion.addAll(plugin.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
            return tabCompletion;
        } else if (args.length == 1) {
        
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                if (args[0].equals(onlinePlayer.getName())) {
                    return null;
                }
            }
        
            tabCompletion.addAll(plugin.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
            tabCompletion.removeIf(s -> !s.startsWith(args[0]));
        
            return tabCompletion;
        }
        return null;
    }
    
    private void sendErrorMessage(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.DARK_RED));
    }
}
