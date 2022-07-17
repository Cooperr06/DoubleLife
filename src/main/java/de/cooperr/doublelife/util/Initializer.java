package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
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
                    plugin.getPlaytimeManager().stopPlayerTimer(plugin.getServer().getOfflinePlayer(playerUuid), true));
                
                memberSection.getKeys(false).stream()
                    .filter(playerUuid -> plugin.getServer().getOnlinePlayers()
                        .stream().anyMatch(player -> player.getUniqueId().toString().equals(playerUuid)))
                    .forEach(playerUuid -> plugin.getPlaytimeManager().startPlayerTimer(plugin.getServer().getOfflinePlayer(playerUuid)));
                
            }
        }, delay, 1000 * 60 * 60 * 24, TimeUnit.MILLISECONDS);
    }
    
    public void startCheckPlaytimeTask() {
        
        var feedback = new ConcurrentHashMap<OfflinePlayer, Integer>();
        
        plugin.setCheckPlaytimeTask(plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            
            var playerTimes = new ArrayList<Map<String, Object>>();
            
            for (var i = 1; i <= plugin.getTeamsSize(); i++) {
                
                var teamsSection = plugin.getConfig().getConfigurationSection("teams");
                assert teamsSection != null;
                var memberSection = teamsSection.getConfigurationSection("team" + i + ".members");
                assert memberSection != null;
                
                playerTimes.add(memberSection.getValues(false));
            }
            
            for (var playerTime : playerTimes) {
                
                playerTime.forEach((uuid, object) -> {
                    
                    var currentTime = Integer.parseInt(String.valueOf(object));
                    var offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(uuid));
                    
                    if (offlinePlayer.isOnline() && plugin.getConfig().getInt("teams.team" + plugin.getPlayerTeamManager().getTeamOfPlayer(offlinePlayer).getTeamNumber() + ".lives") != 0) {
                        
                        var timer = plugin.getPlaytimeManager().getPlayerTimers().get(offlinePlayer);
                        
                        feedback.putIfAbsent(offlinePlayer, 0);
                        
                        if (timer.getTime() + currentTime >= 60 * 60 * 2) {
                            
                            ((Player) offlinePlayer).kick(Component.text("Deine Zeit ist abgelaufen!", NamedTextColor.DARK_RED, TextDecoration.BOLD));
                            plugin.getConfig().set("teams.team" + plugin.getPlayerTeamManager().getTeamOfPlayer(offlinePlayer).getTeamNumber() +
                                ".members." + offlinePlayer.getUniqueId(), timer.getTime() + currentTime);
                            plugin.getPlaytimeManager().stopPlayerTimer(offlinePlayer, true);
                            
                            feedback.put(offlinePlayer, 0);
                            return;
                            
                        } else if ((feedback.get(offlinePlayer) + 1) % 30 == 0) {
                            
                            ((Player) offlinePlayer).sendMessage(Component.text("Du hast noch " + Timer.formatTime(60 * 60 * 2 - (timer.getTime() + currentTime)) +
                                " übrig, bevor du gekickt wirst!", NamedTextColor.RED));
                            
                            feedback.put(offlinePlayer, 0);
                            return;
                        }
                        feedback.put(offlinePlayer, feedback.get(offlinePlayer) + 1);
                    }
                });
            }
        }, 20 * 30, 20 * 30));
    }
    
    private void scoreboardSetup() {
        
        var scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
        
        var teams = new Team[]{
            scoreboard.getTeam("highlife"),
            scoreboard.getTeam("midlife"),
            scoreboard.getTeam("lowlife"),
            scoreboard.getTeam("spectator")
        };
    
        for (int i = 0; i < teams.length; i++) {
            switch (i) {
                case 0 -> {
                    if (teams[i] == null) {
                        var team = scoreboard.registerNewTeam("highlife");
                        team.color(NamedTextColor.GREEN);
                        teams[i] = team;
                    }
                }
                case 1 -> {
                    if (teams[i] == null) {
                        var team = scoreboard.registerNewTeam("midlife");
                        team.color(NamedTextColor.YELLOW);
                        teams[i] = team;
                    }
                }
                case 2 -> {
                    if (teams[i] == null) {
                        var team = scoreboard.registerNewTeam("lowlife");
                        team.color(NamedTextColor.RED);
                        teams[i] = team;
                    }
                }
                case 3 -> {
                    if (teams[i] == null) {
                        var team = scoreboard.registerNewTeam("spectator");
                        team.color(NamedTextColor.GRAY);
                        teams[i] = team;
                    }
                }
            }
        }
        
        plugin.getColorTeams().addAll(List.of(teams));
    }
}
