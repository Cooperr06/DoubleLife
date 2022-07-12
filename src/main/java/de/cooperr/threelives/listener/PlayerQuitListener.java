package de.cooperr.threelives.listener;

import de.cooperr.threelives.ThreeLives;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    
    private final ThreeLives plugin;
    
    public PlayerQuitListener(ThreeLives plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        
        var player = event.getPlayer();
        var playersSection = plugin.getConfig().getConfigurationSection("players");
        assert playersSection != null;
    
        plugin.getPlaytimeManager().stopPlayerTimer(player);
    }
}
