package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    
    private final DoubleLife plugin;
    
    public PlayerJoinListener(DoubleLife plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        
        var player = event.getPlayer();
        var playerOffline = plugin.getServer().getOfflinePlayer(player.getUniqueId());
        var otherMember = plugin.getPlayerTeamManager().getOtherMemberOfPlayer(playerOffline);
        assert otherMember != null;
        
        var otherTeam = plugin.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(otherMember);
        if (otherTeam == null) {
            otherTeam = plugin.getColorTeams().get(0);
        }
        otherTeam.addPlayer(player);
        
        if (plugin.getPlayerTeamManager().getTeamOfPlayer(playerOffline).getTeamNumber() == 3) {
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            plugin.getPlaytimeManager().startPlayerTimer(playerOffline);
            
            if (plugin.getServer().getOnlinePlayers().size() == 1) {
                plugin.getInitializer().startCheckPlaytimeTask();
            }
        }
    }
}
