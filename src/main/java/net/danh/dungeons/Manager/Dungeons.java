package net.danh.dungeons.Manager;

import net.danh.dungeons.WorldManager.WorldManager;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Dungeons extends BukkitRunnable {
    private final String dungeonFile;

    public Dungeons(String dungeonFile) {
        this.dungeonFile = dungeonFile;
    }

    public FileConfiguration getFileManager() {
        return SimpleConfigurationManager.get().get("Dungeons/" + dungeonFile + ".yml");
    }

    public String getWorld() {
        if (getFileManager().contains("world") && getFileManager().getString("world") != null) {
            return getFileManager().getString("world");
        }
        return null;
    }

    public Location getStartLocation(@NotNull Player p) {
        double x = getFileManager().getDouble("location.start.x");
        double y = getFileManager().getDouble("location.start.y");
        double z = getFileManager().getDouble("location.start.z");
        float yaw = Float.parseFloat(getFileManager().getString("location.start.yaw", "0"));
        float pitch = Float.parseFloat(getFileManager().getString("location.start.pitch", "0"));
        World world = Bukkit.getWorld(getWorld() + "_" + p.getName());
        if (world == null) {
            WorldManager worldManager = new WorldManager();
            worldManager.cloneWorld(getWorld(), getWorld() + "_" + p.getName());
            worldManager.doLoad(getWorld() + "_" + p.getName());
            world = Bukkit.getWorld(getWorld() + "_" + p.getName());
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    public Location getCompleteLocation() {
        double x = getFileManager().getDouble("location.complete.x");
        double y = getFileManager().getDouble("location.complete.y");
        double z = getFileManager().getDouble("location.complete.z");
        float yaw = Float.parseFloat(getFileManager().getString("location.complete.yaw", "0"));
        float pitch = Float.parseFloat(getFileManager().getString("location.complete.pitch", "0"));
        World world = Bukkit.getWorld(Objects.requireNonNull(getFileManager().getString("location.complete.world")));
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void onStart(@NotNull Player p) {
        DungeonManager.dungeonStats.put(p.getName() + "_dungeon", dungeonFile);
        p.teleport(getStartLocation(p));
    }

    public void onComplete(@NotNull Player p) {
        p.teleport(getCompleteLocation());
        WorldManager manager = new WorldManager();
        manager.deleteWorld(getWorld() + "_" + p.getName(), true);
        DungeonManager.dungeonStats.remove(p.getName() + "_dungeon");
    }

    public String getDungeonFile() {
        return dungeonFile;
    }

    @Override
    public void run() {
    }
}
