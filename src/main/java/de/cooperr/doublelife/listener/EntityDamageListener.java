package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import de.cooperr.doublelife.util.PlayerTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;

public class EntityDamageListener implements Listener {
    
    private final DoubleLife plugin;
    private final HashMap<PlayerTeam, Integer> cooldown = new HashMap<>();
    
    public EntityDamageListener(DoubleLife plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        
        var entity = event.getEntity();
        
        if (!(entity instanceof Player)) {
            return;
        }
        
        var player = (Player) entity;
        var otherMember = plugin.getPlayerTeamManager().getOtherMemberOfPlayer(plugin.getServer().getOfflinePlayer(player.getUniqueId())).getPlayer();
        var team = plugin.getPlayerTeamManager().getTeamOfPlayer(plugin.getServer().getOfflinePlayer(player.getUniqueId()));
        
        if (otherMember == null) {
            return;
        }
        
        cooldown.putIfAbsent(team, 0);
        
        if (cooldown.get(team) == 0) {
            
            if (otherMember.isOnline()) {
                cooldown.put(team, 1);
            }
            
            if (!(event.getFinalDamage() >= player.getHealth())) {
                otherMember.damage(event.getFinalDamage());
            }
            
        } else {
            cooldown.put(team, 0);
        }
    }
}
