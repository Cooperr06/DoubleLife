package de.cooperr.threelives;

import org.bukkit.plugin.java.JavaPlugin;

public final class ThreeLives extends JavaPlugin {
    
    @Override
    public void onEnable() {
        
        saveConfig();
        
        listenerRegistration();
        commandRegistration();
    }
    
    @Override
    public void onDisable() {
        
    }
    
    private void commandRegistration() {
    }
    
    private void listenerRegistration() {
    }
}
