package net.danh.dungeons.GUI.Stages;

import net.danh.dungeons.API.DungeonsAPI;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.GUI.Stages.Manager.StageBase;
import net.danh.dungeons.Listeners.BlockBreak;
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

public class ShootBlock extends StageBase {
    public ShootBlock() {
        super("shoot_block"
                , "location;amount;distance"
                , null);
    }

    @Override
    public @NotNull String getValueData(@NotNull String path) {
        if (path.equalsIgnoreCase("location"))
            return "string";
        else if (path.equalsIgnoreCase("amount"))
            return "int";
        else if (path.equalsIgnoreCase("distance"))
            return "int";
        return "unknown";
    }

    @Override
    public @Nullable String getEditType(@NotNull String path) {
        if (path.equalsIgnoreCase("location"))
            return "break_block";
        else if (path.equalsIgnoreCase("amount"))
            return "chat_int";
        else if (path.equalsIgnoreCase("distance"))
            return "chat_int";
        return null;
    }

    @Override
    public @Nullable String getSuggestEdit(@NotNull String path) {
        if (path.equalsIgnoreCase("location"))
            return Chat.normalColorize("&6Break block at location you want:");
        else if (path.equalsIgnoreCase("amount"))
            return Chat.normalColorize("&6Amount of shoot:");
        else if (path.equalsIgnoreCase("distance"))
            return Chat.normalColorize("&6Distance player need to shoot it to complete:");
        return null;
    }

    @Override
    public @NotNull String getDisplay(@NotNull Player p) {
        p = PartyManager.getPlayer(p);
        String dungeonID = DungeonsAPI.getDungeon(p);
        int stageNumber = DungeonsAPI.getDungeonStage(p);
        if (dungeonID != null && stageNumber > 0) {
            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
            String location = config.getString("stages.stage_" + stageNumber + ".location");
            World world = Bukkit.getWorld(Objects.requireNonNull(config.getString("world")) + "_" + p.getName() + "_" + dungeonID);
            if (location != null) {
                if (p.getWorld().equals(world)) {
                    int amount = config.getInt("stages.stage_" + stageNumber + ".amount");
                    return Chat.normalColorize(Objects.requireNonNull(Files.getMessage().getString("dungeons.stage.shoot_block"))
                            .replace("%x%", location.split(";")[0])
                            .replace("%y%", location.split(";")[1])
                            .replace("%z%", location.split(";")[2])
                            .replace("%shot%", String.valueOf(BlockBreak.block_data.getOrDefault(p.getName() + "_" + dungeonID, 0)))
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%block%", Chat.caseOnWords(StageManager.getLocation(location, world).getBlock().getType().toString()))
                            .replace("%distance%", String.valueOf((int) p.getLocation().distance(StageManager.getLocation(location, world)))));
                }
            }
        }
        return Chat.normalColorize("&cDisplay is null or has problem");
    }
}
