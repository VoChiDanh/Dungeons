package net.danh.dungeons.WorldManager;

import net.danh.dungeons.DungeonsMain;
import net.danh.dungeons.NMS.NMSAssistant;
import net.danh.dungeons.Resources.FileUtils;
import net.kyori.adventure.util.TriState;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WorldManager implements WManagerInterface {
    private final DungeonsMain plugin;

    public WorldManager() {
        this.plugin = DungeonsMain.getDungeonCore();
    }

    @Override
    public boolean cloneWorld(String oldName, String newName) {
        final File oldWorldFile = new File(this.plugin.getServer().getWorldContainer(), oldName);
        final File newWorldFile = new File(this.plugin.getServer().getWorldContainer(), newName);
        final List<String> ignoreFiles = new ArrayList<>(Arrays.asList("session.lock", "uid.dat"));

        if (newWorldFile.exists()) {
            plugin.getLogger().warning("Folder for new world " + newName + " already exists");
            return false;
        }
        plugin.getLogger().config("Copying files for world" + oldName);
        if (!FileUtils.copyFolder(oldWorldFile, newWorldFile, Optional.of(ignoreFiles))) {
            plugin.getLogger().warning("Failed to copy files for world " + newName);
            return false;
        } else return true;
    }

    @Override
    public boolean doLoad(String name) {
        WorldCreator creator = WorldCreator.name(name);
        creator.keepSpawnLoaded(TriState.FALSE);
        return doLoad(creator);
    }

    private boolean doLoad(@NotNull WorldCreator creator) {
        String worldName = creator.name();
        if (!new File(this.plugin.getServer().getWorldContainer(), worldName).exists() && !new File(this.plugin.getServer().getWorldContainer().getParent(), worldName).exists()) {
            plugin.getLogger().warning("WorldManager: Can't load this world because the folder was deleted/moved: " + worldName);
            return false;
        }

        World cbworld;
        try {
            cbworld = creator.createWorld();
            if (cbworld != null) {
                cbworld.setAutoSave(false);
                if (new NMSAssistant().isVersionGreaterThanOrEqualTo(1, 20, 5)) {
                    cbworld.setGameRule(GameRule.SPAWN_RADIUS, 0);
                    cbworld.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 0);
                } else {
                    cbworld.setKeepSpawnInMemory(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return cbworld != null;
    }

    @Override
    public boolean deleteWorld(String name, boolean deleteWorldFolder) {
        World world = this.plugin.getServer().getWorld(name);
        if (world == null) {
            // We can only delete loaded worlds
            return false;
        }

        if (!this.unloadWorld(name))
            this.unloadWorld(name);
        try {
            File worldFile = world.getWorldFolder();
            plugin.getLogger().finer("deleteWorld(): worldFile: " + worldFile.getAbsolutePath());
            if (deleteWorldFolder ? FileUtils.deleteFolder(worldFile) : FileUtils.deleteFolderContents(worldFile)) {
                plugin.getLogger().info("World " + name + " was DELETED.");
                return true;
            } else {
                plugin.getLogger().severe("World " + name + "  was NOT deleted.");
                plugin.getLogger().severe("Are you sure the folder " + name + "  exists?");
                plugin.getLogger().severe("Please check your file permissions on " + name);
                return false;
            }
        } catch (Throwable e) {
            plugin.getLogger().severe("Hmm, go delete the folder by hand. Sorry.");
            plugin.getLogger().severe(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean unloadWorld(String name) {
        this.removePlayersFromWorld(name);
        return this.plugin.getServer().unloadWorld(name, false);
    }

    @Override
    public void removePlayersFromWorld(String name) {
        World w = this.plugin.getServer().getWorld(name);
        if (w != null) {
            World safeWorld = this.plugin.getServer().getWorlds().getFirst();
            List<Player> ps = w.getPlayers();
            for (Player p : ps) {
                // We're removing players forcefully from a world, they'd BETTER spawn safely.
                p.teleport(safeWorld.getSpawnLocation());
            }
        }
    }
}
