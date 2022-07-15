package de.cooperr.doublelife;

import de.cooperr.doublelife.listener.PlayerCommandPreprocessListener;
import de.cooperr.doublelife.listener.PlayerDeathListener;
import de.cooperr.doublelife.listener.PlayerJoinListener;
import de.cooperr.doublelife.listener.PlayerQuitListener;
import de.cooperr.doublelife.util.Initializer;
import de.cooperr.doublelife.util.LivesManager;
import de.cooperr.doublelife.util.PlayerTeamManager;
import de.cooperr.doublelife.util.PlaytimeManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class DoubleLife extends JavaPlugin {
    
    private final List<Team> colorTeams = new ArrayList<>();
    private final String[] disabledCommands = /*{"seed", "teammsg", "tm", "trigger"}*/ {};
    
    private Initializer initializer;
    private PlaytimeManager playtimeManager;
    private PlayerTeamManager playerTeamManager;
    private LivesManager livesManager;
    
    @Setter
    private BukkitTask checkPlaytimeTask;
    
    @Override
    public void onEnable() {
        init();
        
        listenerRegistration();
        commandRegistration();
    }
    
    @Override
    public void onDisable() {
    }
    
    private void init() {
        initializer = new Initializer(this);
        playtimeManager = new PlaytimeManager(this);
        playerTeamManager = new PlayerTeamManager(this);
        livesManager = new LivesManager(this);
    
        initializer.init();
    }
    
    
    
    private void listenerRegistration() {
        new PlayerCommandPreprocessListener(this);
        new PlayerDeathListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
    }
    
    private void commandRegistration() {
    }
    
    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
    
    public void registerCommand(String commandName, CommandExecutor executor) {
        
        var pluginCommand = getCommand(commandName);
        assert pluginCommand != null;
        
        pluginCommand.setExecutor(executor);
    }
}
