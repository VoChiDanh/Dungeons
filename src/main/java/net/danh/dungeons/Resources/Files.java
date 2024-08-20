package net.danh.dungeons.Resources;

import com.tchristofferson.configupdater.ConfigUpdater;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Dungeons;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

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
        if (configVersion < 2) {
            Dungeons.getDungeonCore().getLogger().warning("Your config file is outdated");
            updateConfig();
        }
        if (messageVersion < 3) {
            Dungeons.getDungeonCore().getLogger().warning("Your message file is outdated");
            updateMessage();
        }
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


    public static void updateConfig() {
        SimpleConfigurationManager.get().save("config.yml");
        java.io.File configFile = new java.io.File(Dungeons.getDungeonCore().getDataFolder(), "config.yml");
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(Dungeons.getDungeonCore().getResource("config.yml")), StandardCharsets.UTF_8));
        FileConfiguration currentConfig = YamlConfiguration.loadConfiguration(configFile);
        int default_configVersion = defaultConfig.getInt("version");
        int current_configVersion = getConfigVersion();
        if (default_configVersion > current_configVersion || default_configVersion < current_configVersion) {
            try {
                ConfigUpdater.update(Dungeons.getDungeonCore(), "config.yml", configFile);
                Dungeons.getDungeonCore().getLogger().log(Level.WARNING, "Your config have been updated successful");
            } catch (IOException e) {
                Dungeons.getDungeonCore().getLogger().log(Level.WARNING, "Can not update config by it self, please backup and rename your config then restart to get newest config!!");
                e.printStackTrace();
            }
            SimpleConfigurationManager.get().reload("config.yml");
        }
    }

    public static void updateMessage() {
        SimpleConfigurationManager.get().save("message.yml");
        java.io.File configFile = new java.io.File(Dungeons.getDungeonCore().getDataFolder(), "message.yml");
        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(Dungeons.getDungeonCore().getResource("message.yml")), StandardCharsets.UTF_8));
        FileConfiguration currentConfig = YamlConfiguration.loadConfiguration(configFile);
        int default_configVersion = defaultConfig.getInt("version");
        int current_configVersion = getMessageVersion();
        if (default_configVersion > current_configVersion || default_configVersion < current_configVersion) {
            Dungeons.getDungeonCore().getLogger().log(Level.WARNING, "Your message is updating...");
            List<String> default_admin_help = defaultConfig.getStringList("admin.help");
            List<String> default_user_help = defaultConfig.getStringList("user.help");
            List<String> current_admin_help = currentConfig.getStringList("admin.help");
            List<String> current_user_help = currentConfig.getStringList("user.help");
            if (default_admin_help.size() != current_admin_help.size()) {
                getConfig().set("admin.help", default_admin_help);
                SimpleConfigurationManager.get().save("message.yml");
            }
            if (default_user_help.size() != current_user_help.size()) {
                getConfig().set("user.help", default_user_help);
                SimpleConfigurationManager.get().save("message.yml");
            }
            try {
                ConfigUpdater.update(Dungeons.getDungeonCore(), "message.yml", configFile);
                Dungeons.getDungeonCore().getLogger().log(Level.WARNING, "Your message have been updated successful");
            } catch (IOException e) {
                Dungeons.getDungeonCore().getLogger().log(Level.WARNING, "Can not update message by it self, please backup and rename your message then restart to get newest message!!");
                e.printStackTrace();
            }
            SimpleConfigurationManager.get().reload("message.yml");
        }
    }
}
