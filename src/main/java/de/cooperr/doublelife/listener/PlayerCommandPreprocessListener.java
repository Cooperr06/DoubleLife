package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.Locale;

public class PlayerCommandPreprocessListener implements Listener {
    
    private final DoubleLife plugin;
    
    public PlayerCommandPreprocessListener(DoubleLife plugin) {
        this.plugin = plugin;
        plugin.registerListener(this);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        
        var player = event.getPlayer();
        
        if (player.isOp()) {
            return;
        }
        
        var command = event.getMessage().split(" ")[0].substring(1).toLowerCase(Locale.ROOT);
        
        if (Arrays.asList(plugin.getDisabledCommands()).contains(command)) {
            player.sendMessage(Component.text("I'm sorry, but you do not have permission to perform this command. " +
                "Please contact the server administrators if you believe that this is in error.", NamedTextColor.RED));
            
            event.setCancelled(true);
        }
    }
}
