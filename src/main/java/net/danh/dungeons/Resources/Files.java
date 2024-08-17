package net.danh.dungeons.Resources;

import net.danh.dungeons.Dungeon.StageManager;
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

    public static FileConfiguration getConfig() {
        return SimpleConfigurationManager.get().get("config.yml");
    }

    public static FileConfiguration getMessage() {
        return SimpleConfigurationManager.get().get("message.yml");
    }
}
