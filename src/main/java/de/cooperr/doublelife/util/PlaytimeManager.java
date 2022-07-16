package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class PlaytimeManager {
    
    private final DoubleLife plugin;
    @Getter
    private final ConcurrentHashMap<OfflinePlayer, Timer> playerTimers = new ConcurrentHashMap<>();
    
    public void startPlayerTimer(OfflinePlayer player) {
        
        var timer = playerTimers.get(player) == null ? new Timer(plugin) : playerTimers.get(player);
        
        timer.start();
        playerTimers.put(player, timer);
    }
    
    public void stopPlayerTimer(OfflinePlayer player, boolean reset) {
        
        var timer = playerTimers.get(player);
        var pathToMember = "teams.team" + plugin.getPlayerTeamManager()
            .getTeamOfPlayer(player).getTeamNumber() + ".members." + player.getUniqueId();
        var currentTime = plugin.getConfig().getInt(pathToMember);
        
        plugin.getConfig().set(pathToMember, reset ? 0 : currentTime + timer.getTime());
        plugin.saveConfig();
    
        timer.stop();
    }
}
