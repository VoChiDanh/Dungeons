package net.danh.dungeons.GUI.Stages;

import net.danh.dungeons.API.DungeonsAPI;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.GUI.Stages.Manager.StageBase;
import net.danh.dungeons.Party.PartyManager;
import net.danh.dungeons.Resources.Chat;
import net.danh.dungeons.Resources.Files;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ReachLocation extends StageBase {
    public ReachLocation() {
        super("reach_location"
                , "location;distance"
                , null);
    }

    @Override
    public @NotNull String getValueData(@NotNull String path) {
        if (path.equalsIgnoreCase("location"))
            return "string";
        else if (path.equalsIgnoreCase("distance"))
            return "int";
        return "unknown";
    }

    @Override
    public @Nullable String getEditType(@NotNull String path) {
        if (path.equalsIgnoreCase("location"))
            return "break_block";
        else if (path.equalsIgnoreCase("distance"))
            return "chat_int";
        return null;
    }

    @Override
    public @Nullable String getSuggestEdit(@NotNull String path) {
        if (path.equalsIgnoreCase("location"))
            return Chat.normalColorize("&6Break block at location you want:");
        else if (path.equalsIgnoreCase("distance"))
            return Chat.normalColorize("&6Distance from player location to reach location to complete:");
        return null;
    }

    @Override
    public @NotNull String getDisplay(@NotNull Player p) {
        String dungeonID = DungeonsAPI.getDungeon(PartyManager.getPlayer(p));
        int stageNumber = DungeonsAPI.getDungeonStage(PartyManager.getPlayer(p));
        if (dungeonID != null && stageNumber > 0) {
            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
            String location = config.getString("stages.stage_" + stageNumber + ".location");
            World world = Bukkit.getWorld(Objects.requireNonNull(config.getString("world")) + "_" + PartyManager.getPlayer(p).getName() + "_" + dungeonID);
            if (location != null) {
                if (PartyManager.getPlayer(p).getWorld().equals(world)) {
                    return Chat.normalColorize(Objects.requireNonNull(Files.getMessage().getString("dungeons.stage.reach_location"))
                            .replace("%x%", location.split(";")[0])
                            .replace("%y%", location.split(";")[1])
                            .replace("%z%", location.split(";")[2])
                            .replace("%distance%", String.valueOf((int) p.getLocation().distance(StageManager.getLocation(location, world)))));
                }
            }
        }
        return Chat.normalColorize("&cDisplay is null or has problem");
    }
}
