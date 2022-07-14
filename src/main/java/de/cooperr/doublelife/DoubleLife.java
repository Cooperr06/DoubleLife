package de.cooperr.doublelife;

import de.cooperr.doublelife.listener.PlayerDeathListener;
import de.cooperr.doublelife.listener.PlayerJoinListener;
import de.cooperr.doublelife.listener.PlayerQuitListener;
import de.cooperr.doublelife.util.LivesManager;
import de.cooperr.doublelife.util.PlayerTeamManager;
import de.cooperr.doublelife.util.PlaytimeManager;
import de.cooperr.doublelife.util.Timer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public final class DoubleLife extends JavaPlugin {
    
    private final List<Team> colorTeams = new ArrayList<>();
    private PlaytimeManager playtimeManager;
    private PlayerTeamManager playerTeamManager;
    private LivesManager livesManager;
    
    private BukkitTask checkPlaytimeTask;
    
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
        saveConfig();
        
        playtimeManager = new PlaytimeManager(this);
        playerTeamManager = new PlayerTeamManager(this);
        livesManager = new LivesManager(this);
        
        initPlayerTeams();
        startDailyTask();
    }
    
    private void initPlayerTeams() {
        
        var teamsSection = getConfig().getConfigurationSection("teams");
        assert teamsSection != null;
        
        for (int i = 1; i < 5; i++) {
            playerTeamManager.getPlayerTeams().forEach(playerTeam -> {
                var teamSection = teamsSection.getConfigurationSection("team" + playerTeam.getTeamNumber());
                assert teamSection != null;
                
                playerTeam.init(teamSection);
            });
        }
    }
    
    private void startDailyTask() {
        var desiredDate = new Date(1658095200); // Monday, 07.18.2022 at 00:00:00 AM
        var now = new Date();
        var delay = desiredDate.getTime() - now.getTime();
        
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            for (int i = 1; i < 5; i++) {
                var memberSection = getConfig().getConfigurationSection("teams.team" + i + ".members");
                assert memberSection != null;
                
                memberSection.getKeys(false).forEach(playerUuid -> getPlaytimeManager().stopPlayerTimer(getServer().getPlayer(playerUuid), true));
                memberSection.getKeys(false).stream()
                    .filter(playerUuid -> getServer().getOnlinePlayers()
                        .stream().anyMatch(player -> player.getUniqueId().toString().equals(playerUuid)))
                    .forEach(playerUuid -> getPlaytimeManager().startPlayerTimer(getServer().getPlayer(playerUuid)));
            }
        }, delay, TimeUnit.MILLISECONDS);
    }
    
    public void startCheckPlaytimeTask() {
        
        var teamsSection = getConfig().getConfigurationSection("teams");
        assert teamsSection != null;
        AtomicInteger sendFeedback = new AtomicInteger();
        
        var task = getServer().getScheduler().runTaskTimer(this, () -> {
            
            var playerTimes = new ArrayList<Map<String, Object>>();
            sendFeedback.getAndIncrement();
            
            for (int i = 1; i < 5; i++) {
                
                var teamSection = teamsSection.getConfigurationSection("team" + i);
                assert teamSection != null;
                var memberSection = teamSection.getConfigurationSection("members");
                assert memberSection != null;
                
                playerTimes.add(memberSection.getValues(true));
            }
    
            for (Map<String, Object> playerTime : playerTimes) {
                
                playerTime.forEach((uuid, object) -> {
                    
                    var currentTime = Integer.parseInt(String.valueOf(object));
                    var player = getServer().getPlayer(UUID.fromString(uuid));
                    assert player != null;
                    var timer = getPlaytimeManager().getPlayerTimers().get(player);
                    
                    if (timer.getTime() + currentTime >= 60 * 60 * 2) {
                        player.kick(Component.text("Deine Zeit ist abgelaufen!", NamedTextColor.DARK_RED, TextDecoration.BOLD));
                        getPlaytimeManager().stopPlayerTimer(player, true);
                    } else if (sendFeedback.get() % 30 == 0) {
                        player.sendMessage(Component.text("Du hast noch " + Timer.formatTime(7200 - (timer.getTime() + currentTime)) +
                            " übrig, bevor du gekickt wirst!", NamedTextColor.RED));
                        sendFeedback.set(0);
                    }
                });
            }
        }, 20 * 30, 20 * 30);
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
        
        Collections.addAll(colorTeams, highlifeTeam, midlifeTeam, lowlifeTeam, spectatorTeam);
        
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
