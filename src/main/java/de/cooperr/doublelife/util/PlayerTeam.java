package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.UUID;

@Getter
@Setter
public class PlayerTeam {
    
    private final OfflinePlayer[] players = new OfflinePlayer[2];
    
    private final DoubleLife plugin;
    private final int teamNumber;
    
    private int lives;
    
    public PlayerTeam(DoubleLife plugin, int teamNumber) {
        this.plugin = plugin;
        this.teamNumber = teamNumber;
    }
    
    public void init(ConfigurationSection section) {
        lives = section.getInt("lives");
        
        var memberSection = section.getConfigurationSection("members");
        assert memberSection != null;
        var teamMembers = memberSection.getKeys(false).toArray(String[]::new);
        
        players[0] = plugin.getServer().getOfflinePlayer(UUID.fromString(teamMembers[0]));
        players[1] = plugin.getServer().getOfflinePlayer(UUID.fromString(teamMembers[1]));
    }
    
    public boolean contains(OfflinePlayer player) {
        return Arrays.stream(players).filter(teamMember -> teamMember.getUniqueId()
            .equals(player.getUniqueId())).findFirst().orElse(null) != null;
    }
    
    public boolean equals(PlayerTeam playerTeam) {
        
        if (playerTeam.getTeamNumber() != teamNumber) {
            return false;
        }
        
        for (var player : playerTeam.getPlayers()) {
            for (var teamMember : players) {
                if (!player.getUniqueId().equals(teamMember.getUniqueId())) {
                    return false;
                }
            }
        }
        
        return players[0].getUniqueId().equals(playerTeam.getPlayers()[0].getUniqueId()) &&
            players[1].getUniqueId().equals(playerTeam.getPlayers()[1].getUniqueId());
    }
}
