package net.danh.dungeons.GUI.Stages;

import net.danh.dungeons.API.DungeonsAPI;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Dungeons;
import net.danh.dungeons.GUI.Editor;
import net.danh.dungeons.GUI.Stages.Manager.StageBase;
import net.danh.dungeons.Listeners.VanillaMobs;
import net.danh.dungeons.Party.PartyManager;
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

public class MythicKillMobs extends StageBase {
    public MythicKillMobs() {
        super("mm_kill_mob"
                , "type;amount"
                , "id;type;amount;location");
    }

    @Override
    public @NotNull String getValueData(@NotNull String path) {
        if (path.equalsIgnoreCase("type"))
            return "string";
        else if (path.equalsIgnoreCase("amount"))
            return "int";
        if (path.equalsIgnoreCase("id"))
            return "string";
        if (path.equalsIgnoreCase("location"))
            return "string";
        return "unknown";
    }

    @Override
    public @Nullable String getEditType(@NotNull String path) {
        if (path.equalsIgnoreCase("type"))
            return "chat_string";
        else if (path.equalsIgnoreCase("amount"))
            return "chat_int";
        if (path.equalsIgnoreCase("id"))
            return "chat_string";
        if (path.equalsIgnoreCase("location"))
            return "break_block";
        return null;
    }

    @Override
    public @Nullable String getSuggestEdit(@NotNull String path) {
        if (path.equalsIgnoreCase("type"))
            return Chat.normalColorize("&6InternalName Mob:");
        else if (path.equalsIgnoreCase("amount"))
            return Chat.normalColorize("&6Amount of mob:");
        if (path.equalsIgnoreCase("id"))
            return Chat.normalColorize("&6Pre Stage ID List: " + Editor.preStageList.toString()
                    .replace("[", "")
                    .replace("]", ""));
        if (path.equalsIgnoreCase("location"))
            return Chat.normalColorize("&6Break block at location you want:");
        return null;
    }

    @Override
    public @NotNull String getDisplay(Player p) {
        p = PartyManager.getPlayer(p);
        String dungeonID = DungeonsAPI.getDungeon(p);
        int stageNumber = DungeonsAPI.getDungeonStage(p);
        if (dungeonID != null && stageNumber > 0) {
            if (Dungeons.isIsMythicMobsInstalled()) {
                FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
                String mob_type = config.getString("stages.stage_" + stageNumber + ".type");
                int amount = config.getInt("stages.stage_" + stageNumber + ".amount");
                if (Dungeons.getMythicAPI().getDisplayName(mob_type) != null) {
                    return Chat.normalColorize(Objects.requireNonNull(Files.getMessage().getString("dungeons.stage.kill_mob"))
                            .replace("%killed%", String.valueOf(VanillaMobs.kill.getOrDefault(p.getName() + "_" + dungeonID, 0)))
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%mob%", Dungeons.getMythicAPI().getDisplayName(mob_type)));
                }
            }
        }
        return Chat.normalColorize("&cDisplay is null or has problem");
    }

    @Override
    public void activePreStage(Player p) {
        p = PartyManager.getPlayer(p);
        if (getPreData() != null) {
            String dungeonID = DungeonsAPI.getDungeon(p);
            int stageNumber = DungeonsAPI.getDungeonStage(p);
            if (dungeonID != null && stageNumber > 0) {
                FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
                String id = config.getString("stages.stage_" + stageNumber + ".pre_stage.id");
                if (id != null && id.equalsIgnoreCase("mm_spawn_mob")) {
                    if (Dungeons.isIsMythicMobsInstalled()) {
                        String type = config.getString("stages.stage_" + stageNumber + ".pre_stage.type");
                        String location = config.getString("stages.stage_" + stageNumber + ".pre_stage.location");
                        int amount = Integer.parseInt(config.getString("stages.stage_" + stageNumber + ".pre_stage.amount", String.valueOf(1)));
                        if (location != null) {
                            World world = Bukkit.getWorld(Objects.requireNonNull(config.getString("world")) + "_" + p.getName() + "_" + dungeonID);
                            if (world != null) {
                                Location rLocation = StageManager.getLocation(location, world);
                                if (Dungeons.getMythicAPI().getDisplayName(type) != null) {
                                    for (int i = 0; i < amount; i++) {
                                        Dungeons.getMythicAPI().spawnMythicMob(type, rLocation);
                                    }
                                }
                            }
                        }
                    } else {
                        Dungeons.getDungeonCore().getLogger().warning("You must install MythicMobs to active pre-stage " + stageNumber + " of Dungeon " + dungeonID);
                    }
                }
            }
        }
    }
}
