package de.cooperr.threelives.listener;

import de.cooperr.threelives.ThreeLives;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;

public class PlayerJoinListener implements Listener {
    
    private final ThreeLives plugin;
    
    public PlayerJoinListener(ThreeLives plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        
        var player = event.getPlayer();
        var playersSection = plugin.getConfig().getConfigurationSection("players");
        assert playersSection != null;
        
        if (!playersSection.contains(String.valueOf(player.getUniqueId()))) {
            playersSection.createSection(String.valueOf(player.getUniqueId()), Map.of(
                "name", player.getName(),
                "lives", 3,
                "playtime", 0)
            );
        }
        
        plugin.getPlaytimeManager().startPlayerTimer(player);
    }
}
