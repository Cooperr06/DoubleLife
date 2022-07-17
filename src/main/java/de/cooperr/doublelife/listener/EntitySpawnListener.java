package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntitySpawnListener implements Listener {
    
    public EntitySpawnListener(DoubleLife plugin) {
        plugin.registerListener(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        
        if (event.getEntityType() == EntityType.WITCH) {
            event.setCancelled(true);
        }
    }
}
