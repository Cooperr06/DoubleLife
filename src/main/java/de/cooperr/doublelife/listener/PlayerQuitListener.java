package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    
    private final DoubleLife plugin;
    
    public PlayerQuitListener(DoubleLife plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        
        var player = event.getPlayer();
        var playerOffline = plugin.getServer().getOfflinePlayer(player.getUniqueId());
        
        if (plugin.getPlayerTeamManager().getTeamOfPlayer(playerOffline).getLives() == 0) {
            return;
        }
    
        plugin.getPlaytimeManager().stopPlayerTimer(playerOffline, false);
    
        if (plugin.getServer().getOnlinePlayers().size() == 1) {
            plugin.getCheckPlaytimeTask().cancel();
        }
    }
}
