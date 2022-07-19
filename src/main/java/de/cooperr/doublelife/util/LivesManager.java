package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;

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
    
    public void giveLife(OfflinePlayer player) {
        var team = plugin.getPlayerTeamManager().getTeamOfPlayer(player);
        team.setLives(team.getLives() + 1);
        
        plugin.getConfig().set("teams.team" + team.getTeamNumber() + ".lives", team.getLives());
        plugin.saveConfig();
    }
}
