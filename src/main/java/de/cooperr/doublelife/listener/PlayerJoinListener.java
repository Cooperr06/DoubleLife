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
        
        var lives = plugin.getPlayerTeamManager().getTeamOfPlayer(playerOffline).getLives();
        
        switch (lives) {
            case 3 -> plugin.getColorTeams().get(0).addPlayer(player);
            case 2 -> plugin.getColorTeams().get(1).addPlayer(player);
            case 1 -> plugin.getColorTeams().get(2).addPlayer(player);
            case 0 -> plugin.getColorTeams().get(3).addPlayer(player);
        }
        
        if (lives == 0) {
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            plugin.getPlaytimeManager().startPlayerTimer(playerOffline);
            
            if (plugin.getServer().getOnlinePlayers().size() == 1) {
                plugin.getInitializer().startCheckPlaytimeTask();
            }
        }
    }
}
