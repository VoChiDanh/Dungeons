package net.danh.dungeons.Dungeon;

import io.lumine.mythic.lib.api.item.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import net.danh.dungeons.API.DungeonsAPI;
import net.danh.dungeons.Dungeons;
import net.danh.dungeons.Resources.Chat;
import net.danh.dungeons.Resources.Files;
import net.danh.dungeons.Utils.CountdownTimer;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StageManager {

    public static HashMap<Player, String> dungeon = new HashMap<>();
    public static HashMap<String, Integer> stage = new HashMap<>();
    public static HashMap<Player, DungeonStatus> status = new HashMap<>();
    public static HashMap<String, Location> checkPoints = new HashMap<>();
    public static HashMap<String, Integer> lives = new HashMap<>();
    public static HashMap<Player, GameMode> gamemode = new HashMap<>();

    public static void copy(String backup, String target) {
        File srcDir = new File(Dungeons.getDungeonCore().getDataFolder(), "Worlds/" + backup);
        if (!srcDir.exists()) {
            Dungeons.getDungeonCore().getLogger().warning("World does not exist!");
            return;
        }
        File destDir = new File(Bukkit.getServer().getWorldContainer(), target);
        try {
            FileUtils.copyDirectory(srcDir, destDir);
            for (File file : destDir.listFiles())
                if (file.isFile())
                    if (file.getName().equalsIgnoreCase("uid.dat"))
                        file.delete();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Bukkit.getServer().createWorld(new WorldCreator(target));
    }

    public static void delete(String target) {
        File dir = new File(Bukkit.getServer().getWorldContainer(), target);
        Bukkit.getServer().unloadWorld(target, false);
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void loadDungeons() {
        File dataFolder = new File(Dungeons.getDungeonCore().getDataFolder(), "Dungeons");
        File[] files = dataFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    Dungeons.getDungeonCore().getLogger().info("Load Dungeon " + file.getName().replace(".yml", ""));
                    SimpleConfigurationManager.get().build("", true, "Dungeons/" + file.getName().replace(".yml", "") + ".yml");
                }
            }
        }
    }

    public static void reloadDungeons() {
        File dataFolder = new File(Dungeons.getDungeonCore().getDataFolder(), "Dungeons");
        File[] files = dataFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    Dungeons.getDungeonCore().getLogger().info("Reload Dungeon " + file.getName().replace(".yml", ""));
                    SimpleConfigurationManager.get().reload("Dungeons/" + file.getName().replace(".yml", "") + ".yml");
                }
            }
        }
    }

    public static void saveDungeons() {
        File dataFolder = new File(Dungeons.getDungeonCore().getDataFolder(), "Dungeons");
        File[] files = dataFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    Dungeons.getDungeonCore().getLogger().info("Save Dungeon " + file.getName().replace(".yml", ""));
                    SimpleConfigurationManager.get().reload("Dungeons/" + file.getName().replace(".yml", "") + ".yml");
                }
            }
        }
    }

    public static boolean checkRequirements(Player p, String dungeonID) {
        File configFile = new File(Dungeons.getDungeonCore().getDataFolder(), "Dungeons/" + dungeonID + ".yml");
        if (configFile.exists()) {
            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
            List<String> requirementsInfo = PlaceholderAPI.setPlaceholders(p, config.getStringList("requirements.info"));
            List<String> requirementsItem = PlaceholderAPI.setPlaceholders(p, config.getStringList("requirements.item"));
            if (!requirementsItem.isEmpty()) {
                int meetRequirements = getMeetItemsRequirement(p, requirementsItem);
                if (!(meetRequirements == requirementsItem.size())) {
                    Chat.sendMessage(p, Files.getMessage().getString("user.check_requirements"));
                    Chat.sendMessage(p, config.getStringList("requirements.item_lore"));
                }
                return (meetRequirements == requirementsItem.size());
            }
            if (!requirementsInfo.isEmpty()) {
                int meetRequirements = getMeetRequirements(requirementsInfo);
                if (!(meetRequirements == requirementsInfo.size())) {
                    Chat.sendMessage(p, Files.getMessage().getString("user.check_requirements"));
                    Chat.sendMessage(p, config.getStringList("requirements.info_lore"));
                }
                return (meetRequirements == requirementsInfo.size());
            }
            return true;
        }
        return false;
    }

    public static int getPlayerAmount(@NotNull HumanEntity player, ItemStack item) {
        final PlayerInventory inv = player.getInventory();
        final ItemStack[] items = inv.getContents();
        int c = 0;
        for (final ItemStack is : items) {
            if (is != null) {
                if (is.isSimilar(item)) {
                    c += is.getAmount();
                }
            }
        }
        return c;
    }

    public static void removeItems(@NotNull Player player, ItemStack item, long amount) {
        item = item.clone();
        final PlayerInventory inv = player.getInventory();
        final ItemStack[] items = inv.getContents();
        int c = 0;
        for (int i = 0; i < items.length; ++i) {
            final ItemStack is = items[i];
            if (is != null) {
                if (is.isSimilar(item)) {
                    if (c + is.getAmount() > amount) {
                        final long canDelete = amount - c;
                        is.setAmount((int) (is.getAmount() - canDelete));
                        items[i] = is;
                        break;
                    }
                    c += is.getAmount();
                    items[i] = null;
                }
            }
        }
        inv.setContents(items);
        player.updateInventory();
    }

    private static int getMeetItemsRequirement(Player p, @NotNull List<String> requirementsItem) {
        int amountCheck = 0;
        for (String s : requirementsItem) {
            String[] reqSplit = s.split(";");
            String itemPlugin = reqSplit[0];
            if (itemPlugin.equalsIgnoreCase("MMOITEMS")) {
                if (Dungeons.isIsMMOItemsInstalled()) {
                    String itemType = reqSplit[1];
                    String itemID = reqSplit[2];
                    int amount = Integer.parseInt(reqSplit[3]);
                    for (ItemStack itemStack : p.getInventory().getContents()) {
                        if (itemStack != null && !itemStack.isEmpty()) {
                            NBTItem nbtItem = NBTItem.get(itemStack);
                            if (nbtItem != null) {
                                if (nbtItem.hasType() && nbtItem.getType().equalsIgnoreCase(itemType)) {
                                    if (nbtItem.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(itemID)) {
                                        if (getPlayerAmount(p, itemStack) >= amount) {
                                            removeItems(p, itemStack, amount);
                                            amountCheck++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return amountCheck;
    }

    private static int getMeetRequirements(@NotNull List<String> requirementsInfo) {
        int amountCheck = 0;
        for (String s : requirementsInfo) {
            String[] reqSplit = s.split(";");
            int papi = Integer.parseInt(reqSplit[0]);
            String compare = reqSplit[1];
            int var = Integer.parseInt(reqSplit[2]);
            if (compare.equalsIgnoreCase(">")) {
                if (papi > var) amountCheck++;
            } else if (compare.equalsIgnoreCase(">=")) {
                if (papi >= var) amountCheck++;
            } else if (compare.equalsIgnoreCase("<")) {
                if (papi < var) amountCheck++;
            } else if (compare.equalsIgnoreCase("<=")) {
                if (papi <= var) amountCheck++;
            } else if (compare.equalsIgnoreCase("==")) {
                if (papi == var) amountCheck++;
            }
        }
        return amountCheck;
    }

    private static int getDefaultLives(Player p, String dungeonID) {
        File configFile = new File(Dungeons.getDungeonCore().getDataFolder(), "Dungeons/" + dungeonID + ".yml");
        if (configFile.exists()) {
            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
            if (config.contains("lives")) {
                return p.getEffectivePermissions().stream()
                        .filter(permission -> permission.getPermission().startsWith("dungeons.lives"))
                        .map(permission -> Integer.parseInt(permission.getPermission().replace("dungeons.lives.", "")))
                        .max(Integer::compareTo)
                        .orElse(config.getInt("lives"));
            }
        }
        return -1;
    }

    public static void startDungeon(Player p, String dungeonID) {
        File configFile = new File(Dungeons.getDungeonCore().getDataFolder(), "Dungeons/" + dungeonID + ".yml");
        if (configFile.exists()) {
            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
            String worldName = config.getString("world");
            if (worldName != null) {
                File srcDir = new File(Dungeons.getDungeonCore().getDataFolder(), "Worlds/" + worldName);
                if (srcDir.exists()) {
                    if (config.contains("name") && config.getString("name") != null) {
                        if (!status.containsKey(p) || (status.containsKey(p) && status.get(p).equals(DungeonStatus.NONE))) {
                            if (StageManager.checkRequirements(p, dungeonID)) {
                                if (!status.containsKey(p))
                                    status.put(p, DungeonStatus.STARTING);
                                else status.replace(p, DungeonStatus.STARTING);
                                Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("user.start_dungeon"))
                                        .replace("<name>", Objects.requireNonNull(config.getString("name"))));
                                copy(worldName, worldName + "_" + p.getName() + "_" + dungeonID);
                                String locationJoin = config.getString("location.join");
                                if (locationJoin != null) {
                                    int x = Integer.parseInt(locationJoin.split(";")[0]);
                                    int y = Integer.parseInt(locationJoin.split(";")[1]);
                                    int z = Integer.parseInt(locationJoin.split(";")[2]);
                                    World world = Bukkit.getWorld(worldName + "_" + p.getName() + "_" + dungeonID);
                                    if (world != null) {
                                        CountdownTimer countdownTimer = new CountdownTimer(Dungeons.getDungeonCore(),
                                                config.getInt("times.start", 3),
                                                () -> Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("dungeons.times.start_soon"))),
                                                (t) -> {
                                                    if (Files.getConfig().getIntegerList("dungeon.show_countdown_at").contains(t.getSecondsLeft())) {
                                                        Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("dungeons.times.start"))
                                                                .replace("<second>", String.valueOf(t.getSecondsLeft())));
                                                    }
                                                },
                                                () -> {
                                                    gamemode.put(p, p.getGameMode());
                                                    Location location = new Location(world, x, y, z);
                                                    p.teleport(location);
                                                    List<String> commands = config.getStringList("commands.join");
                                                    if (!commands.isEmpty()) {
                                                        commands.forEach(cmd -> new BukkitRunnable() {
                                                            @Override
                                                            public void run() {
                                                                Dungeons.getDungeonCore().getServer().dispatchCommand(
                                                                        Dungeons.getDungeonCore().getServer().getConsoleSender(), PlaceholderAPI.setPlaceholders(p, cmd)
                                                                );
                                                            }
                                                        }.runTask(Dungeons.getDungeonCore()));
                                                    }
                                                    dungeon.put(p, dungeonID);
                                                    stage.put(p.getName() + "_" + dungeonID, 0);
                                                    lives.put(p.getName() + "_" + dungeonID, getDefaultLives(p, dungeonID));
                                                    checkPoints.put(p.getName() + "_" + dungeonID, location);
                                                    nextStage(p);
                                                }
                                        );
                                        countdownTimer.scheduleTimer();
                                    }
                                }
                            }
                        }
                    } else Chat.sendMessage(p, Files.getMessage().getString("user.dungeon_error"));
                }
            }
        }
    }

    public static void endDungeon(Player p, boolean isComplete, boolean remove_countdown) {
        String dungeonID = getPlayerDungeon(p);
        if (inDungeon(p)) {
            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
            String locationComplete = config.getString("location.complete");
            if (locationComplete != null) {
                World world = Bukkit.getWorld(locationComplete.split(";")[0]);
                if (world != null) {
                    if (status.containsKey(p) && status.get(p).equals(DungeonStatus.STARTING)) {
                        status.replace(p, DungeonStatus.ENDING);
                        Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("user.end_dungeon"))
                                .replace("<name>", Objects.requireNonNull(config.getString("name"))));
                        int x = Integer.parseInt(locationComplete.split(";")[1]);
                        int y = Integer.parseInt(locationComplete.split(";")[2]);
                        int z = Integer.parseInt(locationComplete.split(";")[3]);
                        Location rLocation = new Location(world, x, y, z);
                        CountdownTimer countdownTimer = new CountdownTimer(Dungeons.getDungeonCore(),
                                !remove_countdown ? config.getInt("times.complete", 3) : 0,
                                () -> Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("dungeons.times.end_soon"))),
                                (t) -> {
                                    if (Files.getConfig().getIntegerList("dungeon.show_countdown_at").contains(t.getSecondsLeft())) {
                                        Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("dungeons.times.end"))
                                                .replace("<second>", String.valueOf(t.getSecondsLeft())));
                                    }
                                },
                                () -> {
                                    p.removePotionEffect(PotionEffectType.BLINDNESS);
                                    p.setGameMode(gamemode.get(p));
                                    p.teleport(rLocation);
                                    delete(config.getString("world") + "_" + p.getName() + "_" + dungeonID);
                                    if (isComplete) {
                                        List<String> commands = config.getStringList("commands.complete");
                                        if (!commands.isEmpty()) {
                                            commands.forEach(cmd -> new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    Dungeons.getDungeonCore().getServer().dispatchCommand(
                                                            Dungeons.getDungeonCore().getServer().getConsoleSender(), PlaceholderAPI.setPlaceholders(p, cmd)
                                                    );
                                                }
                                            }.runTask(Dungeons.getDungeonCore()));
                                        }
                                    }
                                    stage.remove(p.getName() + "_" + dungeonID);
                                    dungeon.remove(p, dungeonID);
                                    lives.remove(p.getName() + "_" + dungeonID);
                                    checkPoints.remove(p.getName() + "_" + dungeonID);
                                    status.replace(p, DungeonStatus.NONE);
                                    gamemode.remove(p);
                                }
                        );
                        countdownTimer.scheduleTimer();
                    }
                }
            }
        }
    }

    @Contract(pure = true)
    public static @Nullable String getStageDisplay(Player p) {
        if (inDungeon(p)) {
            String dungeonID = dungeon.get(p);
            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
            int stageNumber = stage.get(p.getName() + "_" + dungeonID);
            String id = config.getString("stages.stage_" + stageNumber + ".id");
            if (id != null) {
                return DungeonsAPI.getStage(id).getDisplay(p);
            }
        }
        return null;
    }

    public static void nextStage(@NotNull Player p) {
        String dungeonID = dungeon.get(p);
        FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeon.get(p) + ".yml");
        int stageNumber = stage.get(p.getName() + "_" + dungeonID);
        if (config.contains("stages.stage_" + (stageNumber + 1))) {
            stage.replace(p.getName() + "_" + dungeon.get(p), stageNumber + 1);
            Chat.sendMessage(p, getStageDisplay(p));
            if (config.contains("stages.stage_" + (stageNumber + 1) + ".pre_stage")) {
                String id = config.getString("stages.stage_" + (stageNumber + 1) + ".id");
                String id_pre = config.getString("stages.stage_" + (stageNumber + 1) + ".pre_stage.id");
                if (id_pre != null) {
                    DungeonsAPI.getStage(id).activePreStage(p);
                }
            }
        } else endDungeon(p, true, false);
    }

    public static int getStageNumber(Player p) {
        if (inDungeon(p)) {
            return stage.get(p.getName() + "_" + dungeon.get(p));
        }
        return -1;
    }

    public static String getPlayerDungeon(Player p) {
        return dungeon.getOrDefault(p, null);
    }

    public static boolean inDungeon(Player p) {
        return getPlayerDungeon(p) != null;
    }
}
