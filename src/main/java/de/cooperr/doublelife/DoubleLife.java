package de.cooperr.doublelife;

import de.cooperr.doublelife.command.TimeCommand;
import de.cooperr.doublelife.listener.*;
import de.cooperr.doublelife.util.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Getter
public final class DoubleLife extends JavaPlugin {
    
    private final List<Team> colorTeams = new ArrayList<>(4);
    private final String[] disabledCommands = {"seed", "teammsg", "tm", "trigger", "help", "pl", "plugins"};
    
    @Setter
    private int teamsSize;
    
    private Config config;
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
        saveConfig();
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            if (getPlayerTeamManager().getTeamOfPlayer(getServer().getOfflinePlayer(onlinePlayer.getUniqueId())).getLives() != 0) {
                getPlaytimeManager().stopPlayerTimer(getServer().getOfflinePlayer(onlinePlayer.getUniqueId()), false);
            }
        }
    }
    
    private void init() {
        config = new Config(this, "config.yml");
        initializer = new Initializer(this);
        playerTeamManager = new PlayerTeamManager(this);
        initializer.init();
        
        var plugin = this;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                initializer.lateInit();
                playtimeManager = new PlaytimeManager(plugin);
                livesManager = new LivesManager(plugin);
            }
        }, 1000);
    }
    
    private void listenerRegistration() {
        new AsyncPlayerPreLoginListener(this);
        new EntityDamageListener(this);
        new EntitySpawnListener(this);
        new PlayerCommandPreprocessListener(this);
        new PlayerDeathListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
    }
    
    private void commandRegistration() {
        new TimeCommand(this);
    }
    
    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
    
    public void registerCommand(String commandName, CommandExecutor executor) {
        
        var pluginCommand = getCommand(commandName);
        assert pluginCommand != null;
        
        pluginCommand.setExecutor(executor);
    }
    
    @Override
    public @NotNull FileConfiguration getConfig() {
        return config;
    }
    
    @Override
    public void saveConfig() {
        config.save();
    }
}
