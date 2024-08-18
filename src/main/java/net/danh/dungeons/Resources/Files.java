package net.danh.dungeons.Resources;

import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Dungeons;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;

public class Files {

    public static void loadFiles() {
        SimpleConfigurationManager.get().build("", false, "config.yml", "message.yml", "Dungeons/example_dungeon.yml", "Worlds/temp.yml");
        StageManager.loadDungeons();
    }

    public static void saveFiles() {
        SimpleConfigurationManager.get().save("config.yml", "message.yml", "Dungeons/example_dungeon.yml", "Worlds/temp.yml");
        StageManager.saveDungeons();
    }

    public static void reloadFiles() {
        SimpleConfigurationManager.get().reload("config.yml", "message.yml", "Dungeons/example_dungeon.yml", "Worlds/temp.yml");
        StageManager.reloadDungeons();
    }

    public static void updateFiles() {
        int configVersion = getConfigVersion();
        int messageVersion = getMessageVersion();
        if (configVersion < 1)
            Dungeons.getDungeonCore().getLogger().warning("Your config file is outdated");
        if (messageVersion < 1)
            Dungeons.getDungeonCore().getLogger().warning("Your message file is outdated");
    }

    public static int getConfigVersion() {
        if (!getConfig().contains("version")) {
            getConfig().set("version", 1);
            getConfig().set("check_update", true);
            saveFiles();
            reloadFiles();
        }
        return getConfig().contains("version") ? getConfig().getInt("version") : 1;
    }

    public static int getMessageVersion() {
        if (!getMessage().contains("version")) {
            getMessage().set("version", 1);
            saveFiles();
            reloadFiles();
        }
        return getMessage().contains("version") ? getMessage().getInt("version") : 1;
    }

    public static FileConfiguration getConfig() {
        return SimpleConfigurationManager.get().get("config.yml");
    }

    public static FileConfiguration getMessage() {
        return SimpleConfigurationManager.get().get("message.yml");
    }
}
