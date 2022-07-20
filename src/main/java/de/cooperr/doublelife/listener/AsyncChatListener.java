package de.cooperr.doublelife.listener;

import de.cooperr.doublelife.DoubleLife;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AsyncChatListener implements Listener {
    
    public AsyncChatListener(DoubleLife plugin) {
        plugin.registerListener(this);
    }
    
    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        event.renderer((source, sourceDisplayName, message, viewer) ->
            sourceDisplayName
                .append(Component.text(" » ", NamedTextColor.DARK_GRAY))
                .append(message.color(NamedTextColor.WHITE)));
    }
}
