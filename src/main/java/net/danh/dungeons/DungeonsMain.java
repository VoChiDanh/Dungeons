package net.danh.dungeons;

import net.danh.dungeons.Command.MainDungeon;
import net.danh.dungeons.NMS.NMSAssistant;
import net.danh.dungeons.Resources.Files;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;

public final class DungeonsMain extends JavaPlugin {

    private static DungeonsMain dungeons;

    public static DungeonsMain getDungeonCore() {
        return dungeons;
    }

    @Override
    public void onEnable() {
        dungeons = this;
        getLogger().log(Level.INFO, "Server version: " + new NMSAssistant().getNMSVersion().toString());
        SimpleConfigurationManager.register(dungeons);
        new MainDungeon();
        Files.loadFiles();
    }

    @Override
    public void onDisable() {
        Files.saveFiles();
    }

    private void registerEvents(Listener... listeners) {
        Arrays.asList(listeners).forEach(listener -> {
            Bukkit.getPluginManager().registerEvents(listener, dungeons);
            getLogger().info("Registered Listeners " + listener.getClass().getSimpleName());
        });
    }

}
