package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class PlayerTeamManager {
    
    private final DoubleLife plugin;
    private final List<PlayerTeam> playerTeams = new ArrayList<>();
    
    public PlayerTeamManager(DoubleLife plugin) {
        this.plugin = plugin;
        init();
    }
    
    private void init() {
    
        var teamsSection = plugin.getConfig().getConfigurationSection("teams");
        assert teamsSection != null;
    
        plugin.setTeamsSize(teamsSection.getKeys(false).size());
    
        for (var i = 1; i <= plugin.getTeamsSize(); i++) {
            
            var teamSection = teamsSection.getConfigurationSection("team" + i);
            assert teamSection != null;
            
            playerTeams.add(i - 1, new PlayerTeam(plugin, i));
            playerTeams.get(i - 1).init(teamSection);
        }
    }
    
    public PlayerTeam getTeamOfPlayer(OfflinePlayer player) {
        return playerTeams.stream().filter(playerTeam -> playerTeam.contains(player))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Player not in any team"));
    }
    
    public OfflinePlayer getOtherMemberOfPlayer(OfflinePlayer player) {
        return Arrays.stream(playerTeams.stream().filter(playerTeam -> playerTeam.contains(player))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Player not in any team")).getPlayers())
            .filter(teamMember -> !teamMember.getUniqueId().equals(player.getUniqueId()))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("No other team member found"));
    }
}
