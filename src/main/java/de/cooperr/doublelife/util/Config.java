package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config extends YamlConfiguration {
    
    private final DoubleLife plugin;
    private final File file;
    
    public Config(DoubleLife plugin, String fileName) {
        this.plugin = plugin;
        
        var dataFolder = plugin.getDataFolder();
        file = new File(dataFolder, fileName);
        
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create config file", e);
            }
        }
        
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load config file", e);
        }
    }
    
    public void save() {
        try {
            super.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config file", e);
        }
    }
}
