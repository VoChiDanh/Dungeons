package net.danh.dungeons.Manager;

import net.danh.dungeons.DungeonsMain;
import net.xconfig.bukkit.model.SimpleConfigurationManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class DungeonManager {

    private static final List<String> dungeon = new ArrayList<>();
    public static HashMap<String, String> dungeonStats = new HashMap<>();

    public static void loadDungeons() {
        if (!dungeon.isEmpty())
            dungeon.clear();
        File directory = new File(DungeonsMain.getDungeonCore().getDataFolder(), "Dungeons");
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    String dungName = file.getName().replace(".yml", "");
                    if (file.isFile()) {
                        dungeon.add(dungName);
                        SimpleConfigurationManager.get().build("", false, "Dungeons/" + dungName + ".yml");
                        DungeonsMain.getDungeonCore().getLogger().log(Level.INFO, "Loaded Dungeon " + dungName);
                    }
                }
            } else {
                DungeonsMain.getDungeonCore().getLogger().log(Level.INFO, "The directory is empty.");
            }
        } else {
            DungeonsMain.getDungeonCore().getLogger().log(Level.INFO, "The specified path is not a directory or does not exist.");
        }
    }

    public static void saveDungeons() {
        for (String dungeonFile : DungeonManager.getDungeon()) {
            SimpleConfigurationManager.get().save("Dungeons/" + dungeonFile + ".yml");
            DungeonsMain.getDungeonCore().getLogger().log(Level.INFO, "Save dungeon" + dungeonFile);
        }
    }

    public static void reloadDungeons() {
        for (String dungeonFile : DungeonManager.getDungeon()) {
            SimpleConfigurationManager.get().reload("Dungeons/" + dungeonFile + ".yml");
            DungeonsMain.getDungeonCore().getLogger().log(Level.INFO, "Reload dungeon" + dungeonFile);
        }
    }

    public static List<String> getDungeon() {
        return dungeon;
    }
}
