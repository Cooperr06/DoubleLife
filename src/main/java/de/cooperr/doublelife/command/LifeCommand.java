package de.cooperr.doublelife.command;

import de.cooperr.doublelife.DoubleLife;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LifeCommand implements CommandExecutor {
    
    private final DoubleLife plugin;
    
    public LifeCommand(DoubleLife plugin) {
        this.plugin = plugin;
        plugin.registerCommand("life", this);
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        if (sender instanceof Player) {
            sender.sendMessage(Component.text("Du darfst kein Spieler sein, um diesen Befehl auszuführen!", NamedTextColor.DARK_RED));
            return true;
        }
        
        if (args.length != 2) {
            sender.sendMessage(Component.text("Benutze: /life <action> <nummer>", NamedTextColor.DARK_RED));
            return true;
        }
        
        var number = 0;
        try {
            number = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Benutze: /life <action> <nummer>", NamedTextColor.DARK_RED));
            return true;
        }
        
        var teamMember = plugin.getPlayerTeamManager().getTeamOfTeamNumber(number).getPlayers()[0];
        var otherTeamMember = plugin.getPlayerTeamManager().getTeamOfTeamNumber(number).getPlayers()[1];
        var lives = plugin.getLivesManager().giveLife(teamMember);
        
        switch (args[0]) {
            
            case "give" -> {
                
                for (var i = 0; i < plugin.getColorTeams().size(); i++) {
                    
                    var team = plugin.getColorTeams().get(i);
                    
                    if (team.hasPlayer(teamMember) && !team.getName().equals("highlife")) {
                        plugin.getColorTeams().get(i - 1).addPlayer(teamMember);
                        plugin.getColorTeams().get(i - 1).addPlayer(otherTeamMember);
                        break;
                    }
                }
                
                plugin.getServer().broadcast(Component.text(teamMember.getName() + " und " + otherTeamMember.getName() +
                    " wurde ein Leben geschenkt!", NamedTextColor.DARK_GREEN));
                return true;
            }
            
            case "take" -> {
                
                for (var i = 0; i < plugin.getColorTeams().size(); i++) {
                    
                    var team = plugin.getColorTeams().get(i);
                    
                    if (team.hasPlayer(teamMember) && !team.getName().equals("spectator")) {
                        plugin.getColorTeams().get(i + 1).addPlayer(teamMember);
                        plugin.getColorTeams().get(i + 1).addPlayer(otherTeamMember);
                        break;
                    }
                }
                
                plugin.getServer().broadcast(Component.text(teamMember.getName() + " und " + otherTeamMember.getName() +
                    " wurde ein Leben geraubt!", NamedTextColor.DARK_RED));
                return true;
            }
            
            default -> {
                sender.sendMessage(Component.text("Benutze: /life <action> <nummer>", NamedTextColor.DARK_RED));
                return true;
            }
        }
    }
}
