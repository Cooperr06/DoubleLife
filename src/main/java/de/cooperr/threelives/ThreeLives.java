package de.cooperr.threelives;

import de.cooperr.threelives.listener.PlayerDeathListener;
import de.cooperr.threelives.listener.PlayerJoinListener;
import de.cooperr.threelives.listener.PlayerQuitListener;
import de.cooperr.threelives.util.PlaytimeManager;
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
public final class ThreeLives extends JavaPlugin {
    
    private final List<Team> teams = new ArrayList<>();
    private PlaytimeManager playtimeManager;
    
    @Override
    public void onEnable() {
        initConfig();
        init();
        
        listenerRegistration();
        commandRegistration();
    }
    
    @Override
    public void onDisable() {
    }
    
    private void initConfig() {
        saveConfig();
        
        if (getConfig().getConfigurationSection("players") == null) {
            getConfig().createSection("players");
        }
    }
    
    private void init() {
        playtimeManager = new PlaytimeManager(this);
    }
    
    private void listenerRegistration() {
        new PlayerDeathListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
    }
    
    private void commandRegistration() {
    }
    
    private void teamRegistration() {
        
        var scoreboard = getServer().getScoreboardManager().getMainScoreboard();
        
        var highlifeTeam = scoreboard.registerNewTeam("highlife");      // 0
        var midlifeTeam = scoreboard.registerNewTeam("midlife");        // 1
        var lowlifeTeam = scoreboard.registerNewTeam("lowlife");        // 2
        var spectatorTeam = scoreboard.registerNewTeam("spectator");    // 3
        
        scoreboard.registerNewObjective("deaths", "dummy", Component.text("deaths"));
        
        highlifeTeam.color(NamedTextColor.GREEN);
        midlifeTeam.color(NamedTextColor.YELLOW);
        lowlifeTeam.color(NamedTextColor.RED);
        spectatorTeam.color(NamedTextColor.GRAY);
        
        Collections.addAll(teams, highlifeTeam, midlifeTeam, lowlifeTeam, spectatorTeam);
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
