package net.danh.dungeons.GUI.Stages;

import net.danh.dungeons.API.DungeonsAPI;
import net.danh.dungeons.GUI.Stages.Manager.StageBase;
import net.danh.dungeons.Resources.Chat;
import net.danh.dungeons.Resources.Files;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BreakWalls extends StageBase {
    public BreakWalls() {
        super("break_wall"
                , "location;depth;width", null);
    }

    @Override
    public @NotNull String getValueData(@NotNull String path) {
        if (path.equalsIgnoreCase("location"))
            return "string";
        else if (path.equalsIgnoreCase("depth"))
            return "int";
        else if (path.equalsIgnoreCase("width"))
            return "int";
        return "unknown";
    }

    @Override
    public @Nullable String getEditType(@NotNull String data) {
        if (data.equalsIgnoreCase("location"))
            return "break_block";
        else if (data.equalsIgnoreCase("depth"))
            return "chat_int";
        else if (data.equalsIgnoreCase("width"))
            return "chat_int";
        return null;
    }

    @Override
    public @Nullable String getSuggestEdit(@NotNull String path) {
        if (path.equalsIgnoreCase("location"))
            return Chat.normalColorize("&6Break block at location you want:");
        else if (path.equalsIgnoreCase("depth"))
            return Chat.normalColorize("&6Depth of wall");
        else if (path.equalsIgnoreCase("width"))
            return Chat.normalColorize("&6Width of wall");
        return null;
    }

    @Override
    public @NotNull String getDisplay(Player p) {
        String dungeonID = DungeonsAPI.getDungeon(p);
        int stageNumber = DungeonsAPI.getDungeonStage(p);
        if (dungeonID != null && stageNumber > 0) {
            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
            String location = config.getString("stages.stage_" + stageNumber + ".location");
            World world = Bukkit.getWorld(Objects.requireNonNull(config.getString("world")) + "_" + p.getName() + "_" + dungeonID);
            if (location != null) {
                int x = Integer.parseInt(location.split(";")[0]);
                int y = Integer.parseInt(location.split(";")[1]);
                int z = Integer.parseInt(location.split(";")[2]);
                if (p.getWorld().equals(world)) {
                    return Chat.normalColorize(Objects.requireNonNull(Files.getMessage().getString("dungeons.stage.break_wall"))
                            .replace("%x%", location.split(";")[0])
                            .replace("%y%", location.split(";")[1])
                            .replace("%z%", location.split(";")[2])
                            .replace("%block%", Chat.caseOnWords(new Location(world, x, y, z).getBlock().getType().toString()))
                            .replace("%distance%", String.valueOf((int) p.getLocation().distance(new Location(world, x, y, z)))));
                }
            }
        }
        return Chat.normalColorize("&cDisplay is null or has problem");
    }
}
