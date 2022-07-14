package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    
    private final DoubleLife plugin;
    
    public PlayerDeathListener(DoubleLife plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        
        var player = event.getPlayer();
        var playerSection = plugin.getConfig().getConfigurationSection("players." + player.getUniqueId());
        assert playerSection != null;
        
        var playerLives = playerSection.getInt("lives");
        
        plugin.getLivesManager().changeLife(player, '-');
        
        event.deathMessage(player.displayName().append(Component.text(" ist " + (playerLives != 0 ? "gestorben! (" +
            playerLives + " Leben verbleibend)" : "ausgeschieden!"), NamedTextColor.RED)));
        player.getWorld().strikeLightningEffect(player.getLocation());
    }
}
