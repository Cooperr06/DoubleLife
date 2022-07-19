package de.cooperr.doublelife.util;

import de.cooperr.doublelife.DoubleLife;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class Base64Manager {
    
    private final DoubleLife plugin;
    
    public Base64Manager(DoubleLife plugin) {
        this.plugin = plugin;
    }
    
    public String write(ItemStack... items) {
        try (var outputStream = new ByteArrayOutputStream();
             var dataOutput = new BukkitObjectOutputStream(outputStream)) {
            
            dataOutput.writeInt(items.length);
            
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            
            return Base64Coder.encodeLines(outputStream.toByteArray());
            
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to write item stack", e);
            return null;
        }
    }
    
    public ItemStack[] read(String source) {
        try (var inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(source));
             var dataInput = new BukkitObjectInputStream(inputStream)) {
            
            ItemStack[] items = new ItemStack[dataInput.readInt()];
            
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            
            return items;
        } catch (IOException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to read item stack", e);
            return null;
        }
    }
}
