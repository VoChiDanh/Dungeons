package net.danh.dungeons.Dungeon;

import net.danh.dungeons.Resources.Chat;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;

public class DungeonManager {

    FileConfiguration config;

    public DungeonManager(String dungeonID) {
        config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
    }

    public String getDisplayName() {
        return Chat.normalColorize(config.getString("name"));
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
