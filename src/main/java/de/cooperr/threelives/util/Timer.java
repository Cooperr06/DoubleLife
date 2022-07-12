package de.cooperr.threelives.util;

import de.cooperr.threelives.ThreeLives;
import lombok.Getter;

public class Timer {
    
    private final ThreeLives plugin;
    @Getter
    private long time;
    private int taskId;
    
    public Timer(ThreeLives plugin) {
        this.plugin = plugin;
    }
    
    public void start() {
        taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> time++, 20, 20);
    }
    
    public void stop() {
        plugin.getServer().getScheduler().cancelTask(taskId);
    }
}
