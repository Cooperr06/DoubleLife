package de.cooperr.threelives.util;

import de.cooperr.threelives.ThreeLives;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class LivesManager {
    
    private final ThreeLives plugin;
    
    public LivesManager(ThreeLives plugin) {
        this.plugin = plugin;
    }
    
    public void changeLife(Player player, char action) {
        var playerSection = plugin.getConfig().getConfigurationSection("players." + player.getUniqueId());
        assert playerSection != null;
        
        playerSection.set("lives", playerSection.getInt("lives") + (action == '+' ? 1 : action == '-' ? -1 : 0));
        plugin.saveConfig();
        
        if (playerSection.getInt("lives") == 0) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    
        var playerTeam = plugin.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
        assert playerTeam != null;
    
        playerTeam.removePlayer(player);
        plugin.getTeams().get(plugin.getTeams().indexOf(playerTeam) + (action == '+' ? -1 : action == '-' ? 1 : 0)).addPlayer(player);
    }
}
