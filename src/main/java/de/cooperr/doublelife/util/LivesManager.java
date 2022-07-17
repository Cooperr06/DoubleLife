package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class LivesManager {
    
    private final DoubleLife plugin;
    
    public int takeLife(OfflinePlayer player) {
        var team = plugin.getPlayerTeamManager().getTeamOfPlayer(player);
        team.setLives(team.getLives() - 1);
        
        plugin.getConfig().set("teams.team" + team.getTeamNumber() + ".lives", team.getLives());
        plugin.saveConfig();
        
        return team.getLives();
    }
    
    public int giveLife(OfflinePlayer player) {
        var team = plugin.getPlayerTeamManager().getTeamOfPlayer(player);
        team.setLives(team.getLives() + 1);
    
        plugin.getConfig().set("teams.team" + team.getTeamNumber() + ".lives", team.getLives());
        plugin.saveConfig();
    
        return team.getLives();
    }
}
