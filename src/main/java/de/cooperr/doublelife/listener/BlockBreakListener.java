package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    
    public BlockBreakListener(DoubleLife plugin) {
        plugin.registerListener(this);
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        
        if (event.getBlock().getType() == Material.ENCHANTING_TABLE) {
            event.setCancelled(true);
        }
    }
}
