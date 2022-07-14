package de.cooperr.doublelife;

import de.cooperr.doublelife.listener.PlayerDeathListener;
import de.cooperr.doublelife.listener.PlayerJoinListener;
import de.cooperr.doublelife.listener.PlayerQuitListener;
import de.cooperr.doublelife.util.LivesManager;
import de.cooperr.doublelife.util.PlaytimeManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public final class DoubleLife extends JavaPlugin {
    
    private final List<Team> teams = new ArrayList<>();
    private PlaytimeManager playtimeManager;
    private LivesManager livesManager;
    
    @Override
    public void onEnable() {
        init();
        
        listenerRegistration();
        commandRegistration();
        scoreboardSetup();
    }
    
    @Override
    public void onDisable() {
    }
    
    private void init() {
        initConfig();
        
        playtimeManager = new PlaytimeManager(this);
        livesManager = new LivesManager(this);
    }
    
    private void initConfig() {
        saveConfig();
        
        if (getConfig().getConfigurationSection("players") == null) {
            getConfig().createSection("players");
        }
    }
    
    private void listenerRegistration() {
        new PlayerDeathListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
    }
    
    private void commandRegistration() {
    }
    
    private void scoreboardSetup() {
        
        var scoreboard = getServer().getScoreboardManager().getMainScoreboard();
        
        if (scoreboard.getTeams().stream().anyMatch(team -> team.getName().contains("life")) &&
            scoreboard.getTeams().size() == 4 &&
            scoreboard.getObjective("deaths") != null) {
            return;
        }
        
        var highlifeTeam = scoreboard.registerNewTeam("highlife");      // 0
        var midlifeTeam = scoreboard.registerNewTeam("midlife");        // 1
        var lowlifeTeam = scoreboard.registerNewTeam("lowlife");        // 2
        var spectatorTeam = scoreboard.registerNewTeam("spectator");    // 3
        
        highlifeTeam.color(NamedTextColor.GREEN);
        midlifeTeam.color(NamedTextColor.YELLOW);
        lowlifeTeam.color(NamedTextColor.RED);
        spectatorTeam.color(NamedTextColor.GRAY);
        
        Collections.addAll(teams, highlifeTeam, midlifeTeam, lowlifeTeam, spectatorTeam);
    
        scoreboard.registerNewObjective("deaths", "deathCount", Component.text("deaths"));
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
