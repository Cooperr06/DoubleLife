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
        var playersSection = plugin.getConfig().getConfigurationSection("players");
        assert playersSection != null;
        
        if (!playersSection.contains(String.valueOf(player.getUniqueId()))) {
            playersSection.createSection(String.valueOf(player.getUniqueId()), Map.of(
                "name", player.getName(),
                "lives", -1,
                "playtime", 0)
            );
            var lives = showLivesGenerator(player);
            
            var playerSection = plugin.getConfig().getConfigurationSection("players." + player.getUniqueId());
            assert playerSection != null;
            
            playerSection.set("lives", lives);
            plugin.saveConfig();
        }
        
        plugin.getPlaytimeManager().startPlayerTimer(player);
    }
    
    private int showLivesGenerator(Player player) {
        var task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            
            var random = ThreadLocalRandom.current().nextInt(4, 7);
            var component = Component.text(random,
                random == 6 ? NamedTextColor.GREEN :
                    random == 5 ? NamedTextColor.YELLOW :
                        random == 4 ? NamedTextColor.RED : null);
            
            var title = Title.title(component, Component.text(""),
                Title.Times.times(Duration.ZERO, Duration.ofMillis(400), Duration.ZERO));
            
            player.showTitle(title);
            
        }, 50, 8);
        plugin.getServer().getScheduler().runTaskLater(plugin, task::cancel, 8 * 20);
        
        var lives = ThreadLocalRandom.current().nextInt(4, 7);
        
        var title = Title.title(Component.text(lives), Component.text(""),
            Title.Times.times(Duration.ZERO, Duration.ofMillis(400), Duration.ZERO));
        
        return lives;
    }
}
