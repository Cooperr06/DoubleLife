package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public class Timer {
    
    private final DoubleLife plugin;
    @Getter
    private long time;
    private BukkitTask task;
    
    public void start() {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> time++, 20, 20);
    }
    
    public void stop() {
        task.cancel();
        time = 0;
    }
    
    public static String formatTime(long time) {
        var seconds = time;
        var minutes = 0;
        var hours = 0;
        
        while (seconds >= 60) {
            minutes++;
            seconds -= 60;
        }
        while (minutes >= 60) {
            hours++;
            minutes -= 60;
        }
        
        return (hours == 0 && minutes != 0 ? "" : hours + "h") + " " +
            (minutes == 0 && seconds != 0 ? "" : minutes + "m") + " " +
            seconds + "s";
    }
}
