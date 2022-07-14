package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import lombok.Getter;
import org.bukkit.entity.Player;

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
    }
    
    public PlayerTeam getTeamOfPlayer(Player player) {
        return playerTeams.stream().filter(playerTeam -> playerTeam.contains(player))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Player not in any team"));
    }
    
    public Integer getTeamNumberOfTeam(PlayerTeam playerTeam) {
        return playerTeams.stream().filter(team -> team.equals(playerTeam))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Team not registered")).getTeamNumber();
    }
    
    public PlayerTeam getTeamOfTeamNumber(Integer teamNumber) {
        return playerTeams.stream().filter(playerTeam -> playerTeam.getTeamNumber() == teamNumber)
            .findFirst().orElseThrow(() -> new IllegalArgumentException("No team found with number " + teamNumber));
    }
    
    public Player getOtherMemberOfPlayer(Player player) {
        return Arrays.stream(playerTeams.stream().filter(playerTeam -> playerTeam.contains(player))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Player not in any team")).getPlayers())
            .filter(teamMember -> !teamMember.getUniqueId().equals(player.getUniqueId())).findFirst().orElse(null);
    }
}
