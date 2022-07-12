package de.cooperr.threelives.util;

import de.cooperr.threelives.ThreeLives;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlaytimeManager {
    
    private final ThreeLives plugin;
    private final HashMap<Player, Timer> playerTimers = new HashMap<>();
    
    public PlaytimeManager(ThreeLives plugin) {
        this.plugin = plugin;
    }
    
    public void startPlayerTimer(Player player) {
        
        var timer = new Timer(plugin);
        
        timer.start();
        playerTimers.put(player, timer);
    }
    
    public void stopPlayerTimer(Player player) {
        
        var timer = playerTimers.get(player);
        var playerSection = plugin.getConfig().getConfigurationSection("players." + player.getUniqueId());
        assert playerSection != null;
        
        timer.stop();
        
        playerSection.set("playtime", playerSection.getInt("playtime") + timer.getTime());
        plugin.saveConfig();
    }
}
