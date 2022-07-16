package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {
    
    private final DoubleLife plugin;
    
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
        
        if (otherMember == null) {
            return;
        }
        
        otherMember.damage(event.getFinalDamage());
    }
}
