package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

public class Timer {
    
    private final DoubleLife plugin;
    @Getter
    private long time;
    private BukkitTask task;
    
    public Timer(DoubleLife plugin) {
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
