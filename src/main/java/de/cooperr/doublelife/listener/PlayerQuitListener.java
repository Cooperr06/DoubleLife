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
    
        plugin.getPlaytimeManager().stopPlayerTimer(player, false);
    
        if (plugin.getServer().getOnlinePlayers().size() == 1) {
            plugin.getCheckPlaytimeTask().cancel();
        }
    }
}
