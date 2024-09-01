package net.danh.dungeons.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.jetbrains.annotations.NotNull;

public class WorldListener implements Listener {

    @EventHandler
    public void onWorldCreate(@NotNull WorldInitEvent e) {
        World world = e.getWorld();
        String worldName = world.getName();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (worldName.contains(p.getName())
            || worldName.contains(p.getName().toLowerCase())
            || worldName.contains(p.getName().toUpperCase())) {
                world.setKeepSpawnInMemory(false);
            }
        }
    }
}
