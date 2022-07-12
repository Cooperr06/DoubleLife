package de.cooperr.threelives.listener;

import de.cooperr.threelives.ThreeLives;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    
    private final ThreeLives plugin;
    
    public PlayerDeathListener(ThreeLives plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        
        var player = event.getPlayer();
        var playerSection = plugin.getConfig().getConfigurationSection("players." + player.getUniqueId());
        assert playerSection != null;
        
        var playerTeam = plugin.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
        assert playerTeam != null;
        var playerTeamIndex = plugin.getTeams().indexOf(playerTeam);
        
        if (playerTeam.getName().equals("spectator")) {
            return;
        }
        
        playerSection.set("lives", playerSection.getInt("lives") - 1);
        plugin.saveConfig();
        
        var playerLives = playerSection.getInt("lives");
        
        event.deathMessage(player.displayName().append(Component.text(" ist gestorben! (" +
            playerLives + " Leben verbleibend)", NamedTextColor.RED)));
        
        playerTeam.removePlayer(player);
        plugin.getTeams().get(playerTeamIndex + 1).addPlayer(player);
    }
}
