package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
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
        var otherMember = plugin.getPlayerTeamManager().getOtherMemberOfPlayer(player);
        var lives = plugin.getLivesManager().removeLife(player);
        
        if (lives == 0) {
            
            event.deathMessage(player.displayName().append(Component.text(" ist ausgeschieden!", NamedTextColor.RED)));
            plugin.getServer().broadcast(Component.text(otherMember.getName() + " ist ausgeschieden!", NamedTextColor.DARK_RED));
            
            player.setGameMode(GameMode.SPECTATOR);
            
        } else {
            event.deathMessage(player.displayName().append(Component.text(" ist gestorben! (" + lives +
                " Leben verbleibend)", NamedTextColor.RED)));
        }
    
        for (var i = 0; i < plugin.getColorTeams().size(); i++) {
        
            var team = plugin.getColorTeams().get(i);
        
            if (team.hasPlayer(player)) {
            
                var lowerTeam = plugin.getColorTeams().get(i + 1);
                
                lowerTeam.addPlayer(player);
                
                if (otherMember.isOnline()) {
                    var onlineOtherMember = otherMember.getPlayer();
                    assert onlineOtherMember != null;
    
                    lowerTeam.addPlayer(otherMember);
                    onlineOtherMember.getWorld().strikeLightningEffect(player.getLocation());
                    
                    if (lowerTeam.getName().equals("spectator")) {
                        onlineOtherMember.setGameMode(GameMode.SPECTATOR);
                    }
                }
                break;
            }
        }
        
        player.getWorld().strikeLightningEffect(player.getLocation());
    }
}
