package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Initializer {
    
    private final DoubleLife plugin;
    
    public Initializer(DoubleLife plugin) {
        this.plugin = plugin;
    }
    
    public void init() {
        plugin.saveConfig();
        
        startDailyTask();
        
        if (!plugin.getServer().getOnlinePlayers().isEmpty()) {
            startCheckPlaytimeTask();
        }
    }
    
    public void lateInit() {
        scoreboardSetup();
    }
    
    private void startDailyTask() {
        
        var desiredDate = Date.from(LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.ofHours(2)));
        var now = new Date();
        var delay = desiredDate.getTime() - now.getTime();
        
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            
            for (var i = 1; i <= plugin.getTeamsSize(); i++) {
                
                var memberSection = plugin.getConfig().getConfigurationSection("teams.team" + i + ".members");
                assert memberSection != null;
                
                memberSection.getKeys(false).forEach(playerUuid ->
                    plugin.getPlaytimeManager().stopPlayerTimer(plugin.getServer().getPlayer(playerUuid), true));
                
                memberSection.getKeys(false).stream()
                    .filter(playerUuid -> plugin.getServer().getOnlinePlayers()
                        .stream().anyMatch(player -> player.getUniqueId().toString().equals(playerUuid)))
                    .forEach(playerUuid -> plugin.getPlaytimeManager().startPlayerTimer(plugin.getServer().getPlayer(playerUuid)));
                
            }
        }, delay, 1000 * 60 * 60 * 24, TimeUnit.MILLISECONDS);
    }
    
    public void startCheckPlaytimeTask() {
        
        var teamsSection = plugin.getConfig().getConfigurationSection("teams");
        assert teamsSection != null;
        var feedback = new ConcurrentHashMap<Player, Integer>();
    
        plugin.setCheckPlaytimeTask(plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
    
            var playerTimes = new ArrayList<Map<String, Object>>();
    
            for (var i = 1; i < plugin.getTeamsSize(); i++) {
        
                var teamSection = teamsSection.getConfigurationSection("team" + i);
                assert teamSection != null;
                var memberSection = teamSection.getConfigurationSection("members");
                assert memberSection != null;
        
                playerTimes.add(memberSection.getValues(true));
            }
    
            for (var playerTime : playerTimes) {
        
                playerTime.forEach((uuid, object) -> {
            
                    var currentTime = Integer.parseInt(String.valueOf(object));
                    var player = plugin.getServer().getPlayer(UUID.fromString(uuid));
                    assert player != null;
                    var timer = plugin.getPlaytimeManager().getPlayerTimers().get(player);
            
                    if (timer.getTime() + currentTime >= 60 * 60 * 2) {
                
                        player.kick(Component.text("Deine Zeit ist abgelaufen!", NamedTextColor.DARK_RED, TextDecoration.BOLD));
                        plugin.getPlaytimeManager().stopPlayerTimer(player, true);
                
                        feedback.put(player, 0);
                        return;
                
                    } else if ((feedback.get(player) + 1) % 30 == 0) {
                
                        player.sendMessage(Component.text("Du hast noch " + Timer.formatTime(7200 - (timer.getTime() + currentTime)) +
                            " übrig, bevor du gekickt wirst!", NamedTextColor.RED));
                
                        feedback.put(player, 0);
                        return;
                    }
                    feedback.put(player, feedback.get(player) + 1);
                });
            }
        }, 20 * 30, 20 * 30));
    }
    
    private void scoreboardSetup() {
        
        var scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
        
        var teams = new HashMap<String, Team>();
        teams.put("highlife", scoreboard.getTeam("highlife"));
        teams.put("midlife", scoreboard.getTeam("midlife"));
        teams.put("lowlife", scoreboard.getTeam("lowlife"));
        teams.put("spectator", scoreboard.getTeam("spectator"));
        
        if (scoreboard.getObjective("deaths") == null) {
            scoreboard.registerNewObjective("deaths", "deathCount", Component.text("deaths"));
        }
    
        teams.forEach((name, team) -> {
            if (team == null) {
                teams.put(name, scoreboard.registerNewTeam(name));
            }
        });
    
        plugin.getColorTeams().addAll(teams.values());
    }
}