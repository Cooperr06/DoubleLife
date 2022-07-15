package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerJoinListener implements Listener {
    
    private final DoubleLife plugin;
    
    public PlayerJoinListener(DoubleLife plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        
        var player = event.getPlayer();
        
        if (plugin.getColorTeams().stream().noneMatch(team -> team.hasPlayer(player))) {
            plugin.getColorTeams().get(0).addPlayer(player);
        }
        
        plugin.getPlaytimeManager().startPlayerTimer(player);
        
        if (plugin.getServer().getOnlinePlayers().isEmpty()) {
            plugin.getInitializer().startCheckPlaytimeTask();
        }
        
        var scoreboardTeam = plugin.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
        
        if (scoreboardTeam == null) {
    
            var team = plugin.getPlayerTeamManager().getTeamOfPlayer(player);
            
            var scoreboardTeamName =
                team.getLives() == 3 ? "highlife" :
                    team.getLives() == 2 ? "midlife" :
                        team.getLives() == 1 ? "lowlife" :
                            team.getLives() == 0 ? "spectator" : "";
    
            scoreboardTeam = plugin.getServer().getScoreboardManager().getMainScoreboard().getTeam(scoreboardTeamName);
            assert scoreboardTeam != null;
            
            scoreboardTeam.addPlayer(player);
        }
    }
}
