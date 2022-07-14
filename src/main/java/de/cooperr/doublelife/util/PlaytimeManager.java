package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;

@RequiredArgsConstructor
public class PlaytimeManager {
    
    private final DoubleLife plugin;
    @Getter
    private final HashMap<Player, Timer> playerTimers = new HashMap<>();
    
    public void startPlayerTimer(Player player) {
        
        var timer = playerTimers.get(player) == null ? new Timer(plugin) : playerTimers.get(player);
        
        timer.start();
        playerTimers.put(player, timer);
    }
    
    public void stopPlayerTimer(Player player, boolean reset) {
        
        var timer = playerTimers.get(player);
        var pathToMember = "teams.team" + plugin.getPlayerTeamManager()
            .getTeamOfPlayer(player).getTeamNumber() + ".members." + player.getUniqueId();
        var currentTime = plugin.getConfig().getInt(pathToMember);
        
        timer.stop();
        
        if (currentTime + timer.getTime() == 120 * 60) {
            reset = true;
        }
        plugin.getConfig().set(pathToMember, reset ? 0 : currentTime + timer.getTime());
        plugin.saveConfig();
    }
}
