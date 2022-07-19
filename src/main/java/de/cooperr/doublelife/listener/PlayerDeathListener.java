package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import de.cooperr.doublelife.util.PlayerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;

public class PlayerDeathListener implements Listener {
    
    private final DoubleLife plugin;
    private final HashMap<PlayerTeam, Integer> cooldown = new HashMap<>();
    
    public PlayerDeathListener(DoubleLife plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        
        var player = event.getPlayer();
        var otherMember = plugin.getPlayerTeamManager().getOtherMemberOfPlayer(player);
        var playerTeam = plugin.getPlayerTeamManager().getTeamOfPlayer(plugin.getServer().getOfflinePlayer(player.getUniqueId()));
        
        cooldown.putIfAbsent(playerTeam, 0);
        
        var lives = plugin.getPlayerTeamManager().getTeamOfPlayer(plugin.getServer().getOfflinePlayer(player.getUniqueId())).getLives();
        
        if (cooldown.get(playerTeam) == 1) {
            cooldown.put(playerTeam, 0);
        } else {
            
            if (otherMember.isOnline()) {
                
                lives = plugin.getLivesManager().takeLife(plugin.getServer().getOfflinePlayer(player.getUniqueId()));
                
                var otherOnline = otherMember.getPlayer();
                assert otherOnline != null;
                
                cooldown.put(playerTeam, 1);
                otherOnline.setHealth(0);
            }
        }
        
        if (lives == 0) {
            
            event.deathMessage(player.displayName().color(NamedTextColor.DARK_RED).append(Component.text(" ist ausgeschieden!", NamedTextColor.DARK_RED)));
            
            player.setGameMode(GameMode.SPECTATOR);
            plugin.getPlaytimeManager().stopPlayerTimer(plugin.getServer().getOfflinePlayer(player.getUniqueId()), true);
            
        } else {
            event.deathMessage(player.displayName().append(Component.text(" ist gestorben! (" + lives +
                " Leben verbleibend)", NamedTextColor.RED)));
        }
        
        for (var i = 0; i < plugin.getColorTeams().size(); i++) {
            
            var team = plugin.getColorTeams().get(i);
            
            if (team.hasPlayer(player) && !team.getName().equals("spectator")) {
                plugin.getColorTeams().get(i + 1).addPlayer(player);
                break;
            }
        }
        
        player.getWorld().strikeLightningEffect(player.getLocation());
        
        if (!player.getInventory().isEmpty()) {
            
            var pathToPlayer = "teams.team" + playerTeam.getTeamNumber() + ".members." + player.getUniqueId();
            
            plugin.getConfig().set(pathToPlayer + ".last-inventory", plugin.getBase64().write(player.getInventory().getContents()));
            plugin.getConfig().set(pathToPlayer + ".last-exp", player.getTotalExperience());
            plugin.saveConfig();
        }
    }
}
