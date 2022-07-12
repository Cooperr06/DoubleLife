package de.cooperr.threelives.util;

import de.cooperr.threelives.ThreeLives;
import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

public class Timer {
    
    private final ThreeLives plugin;
    @Getter
    private long time;
    private BukkitTask task;
    
    public Timer(ThreeLives plugin) {
        this.plugin = plugin;
    }
    
    public void start() {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> time++, 0, 20);
    }
    
    public void stop() {
        task.cancel();
        time = 0;
    }
}
