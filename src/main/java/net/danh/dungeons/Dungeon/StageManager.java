package net.danh.dungeons.Dungeon;

import io.lumine.mythic.lib.api.item.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import net.danh.dungeons.API.DungeonsAPI;
import net.danh.dungeons.Dungeons;
import net.danh.dungeons.Party.PartyManager;
import net.danh.dungeons.Resources.Chat;
import net.danh.dungeons.Resources.Files;
import net.danh.dungeons.Utils.CountdownTimer;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class StageManager {
    public static HashMap<Player, String> dungeon = new HashMap<>();
    public static HashMap<String, Integer> stage = new HashMap<>();
    public static HashMap<Player, DungeonStatus> status = new HashMap<>();
    public static HashMap<String, Location> checkPoints = new HashMap<>();
    public static HashMap<String, Integer> lives = new HashMap<>();
    public static HashMap<Player, GameMode> gamemode = new HashMap<>();

    public static void copy(String backup, String target) {
        Path srcDir = Paths.get(Dungeons.getDungeonCore().getDataFolder().getAbsolutePath(), "Worlds", backup);
        Path destDir = Paths.get(Bukkit.getServer().getWorldContainer().getAbsolutePath(), target);

        if (java.nio.file.Files.notExists(srcDir)) {
            srcDir = Paths.get(Bukkit.getServer().getWorldContainer().getAbsolutePath(), backup);
            if (java.nio.file.Files.notExists(srcDir)) {
                Dungeons.getDungeonCore().getLogger().warning("World does not exist!");
                return;
            }
        }

        try {
            if (java.nio.file.Files.notExists(destDir)) {
                java.nio.file.Files.createDirectories(destDir);
            }

            Path finalSrcDir = srcDir;
            java.nio.file.Files.walk(srcDir).filter(Objects::nonNull).forEach(source -> {
                Path destination = destDir.resolve(finalSrcDir.relativize(source));
                try {
                    if (java.nio.file.Files.isDirectory(source)) {
                        if (java.nio.file.Files.notExists(destination)) {
                            java.nio.file.Files.createDirectory(destination);
                        }
                    } else {
                        java.nio.file.Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            try (DirectoryStream<Path> directoryStream = java.nio.file.Files.newDirectoryStream(destDir)) {
                for (Path file : directoryStream) {
                    if (java.nio.file.Files.isRegularFile(file) && file.getFileName().toString().equalsIgnoreCase("uid.dat")) {
                        java.nio.file.Files.delete(file);
                    }
                }
            }

            Bukkit.getServer().createWorld(new WorldCreator(target));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(String target) {
        Path dir = Paths.get(Bukkit.getServer().getWorldContainer().getAbsolutePath(), target);

        boolean unloaded = Bukkit.getServer().unloadWorld(target, false);
        if (!unloaded) {
            return;
        }

        if (!java.nio.file.Files.exists(dir)) {
            return;
        }

        try {
            java.nio.file.Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            java.nio.file.Files.delete(path);
                        } catch (IOException e) {
                            Dungeons.getDungeonCore().getLogger().warning("Failed to delete file: " + path.toString());
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            Dungeons.getDungeonCore().getLogger().warning("Failed to walk through directory: " + dir.toString());
            e.printStackTrace();
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
        reloadDungeons();
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
                if (!getMeetItemsRequirement(p, requirementsItem)) {
                    Chat.sendMessage(p, Files.getMessage().getString("user.check_requirements"));
                    Chat.sendMessage(p, config.getStringList("requirements.item_lore"));
                    return false;
                }

            }
            if (!requirementsInfo.isEmpty()) {
                int meetRequirements = getMeetRequirements(requirementsInfo);
                if (!(meetRequirements == requirementsInfo.size())) {
                    Chat.sendMessage(p, Files.getMessage().getString("user.check_requirements"));
                    Chat.sendMessage(p, config.getStringList("requirements.info_lore"));
                    return false;
                }

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

    public static boolean getMeetItemsRequirement(@NotNull Player p, @NotNull List<String> requirementsItem) {
        int amountCheck = 0;
        List<String> checkedItems = new ArrayList<>();
        for (ItemStack itemStack : p.getInventory().getContents()) {
            for (String s : requirementsItem) {
                if (!checkedItems.contains(s) && amountCheck < requirementsItem.size()) {
                    String[] reqSplit = s.split(";");
                    String itemPlugin = reqSplit[0];
                    if (itemPlugin.equalsIgnoreCase("MMOITEMS")) {
                        String itemType = reqSplit[1];
                        String itemID = reqSplit[2];
                        int amount = Integer.parseInt(reqSplit[3]);
                        if (itemStack != null) {
                            NBTItem nbtItem = NBTItem.get(itemStack);
                            if (nbtItem != null) {
                                if (nbtItem.hasType() && nbtItem.getType().equalsIgnoreCase(itemType)) {
                                    if (nbtItem.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(itemID)) {
                                        if (getPlayerAmount(p, itemStack) >= amount) {
                                            amountCheck++;
                                            checkedItems.add(s);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (itemPlugin.equalsIgnoreCase("VANILLA")) {
                        String itemType = reqSplit[1];
                        int amount = Integer.parseInt(reqSplit[2]);
                        if (itemStack != null) {
                            if (itemStack.getType() != Material.AIR) {
                                if (itemStack.getType().toString().equals(itemType)
                                        && !itemStack.hasItemMeta()) {
                                    if (getPlayerAmount(p, itemStack) >= amount) {
                                        amountCheck++;
                                        checkedItems.add(s);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (amountCheck == requirementsItem.size() && checkedItems.size() == requirementsItem.size())
            removeItemReq(p, checkedItems);

        return amountCheck == requirementsItem.size() && checkedItems.size() == requirementsItem.size();
    }

    private static void removeItemReq(@NotNull Player p, List<String> checkedItems) {
        int amountCheck = 0;
        List<String> removedItems = new ArrayList<>();
        for (ItemStack itemStack : p.getInventory().getContents()) {
            for (String checkID : checkedItems) {
                if (!removedItems.contains(checkID) && amountCheck < checkedItems.size()) {
                    String[] reqSplit = checkID.split(";");
                    String itemPlugin = reqSplit[0];
                    if (itemPlugin.equalsIgnoreCase("MMOITEMS")) {
                        String itemType = reqSplit[1];
                        String itemID = reqSplit[2];
                        int amount = Integer.parseInt(reqSplit[3]);
                        if (itemStack != null) {
                            NBTItem nbtItem = NBTItem.get(itemStack);
                            if (nbtItem != null) {
                                if (nbtItem.hasType() && nbtItem.getType().equalsIgnoreCase(itemType)) {
                                    if (nbtItem.getString("MMOITEMS_ITEM_ID").equalsIgnoreCase(itemID)) {
                                        if (getPlayerAmount(p, itemStack) >= amount) {
                                            removeItems(p, itemStack, amount);
                                            amountCheck++;
                                            removedItems.add(checkID);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (itemPlugin.equalsIgnoreCase("VANILLA")) {
                        String itemType = reqSplit[1];
                        int amount = Integer.parseInt(reqSplit[2]);
                        if (itemStack != null) {
                            if (itemStack.getType() != Material.AIR) {
                                if (itemStack.getType().toString().equals(itemType)
                                        && !itemStack.hasItemMeta()) {
                                    if (getPlayerAmount(p, itemStack) >= amount) {
                                        removeItems(p, itemStack, amount);
                                        amountCheck++;
                                        removedItems.add(checkID);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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

    private static int getDefaultLives(Player player, String dungeonID) {
        File configFile = new File(Dungeons.getDungeonCore().getDataFolder(), "Dungeons/" + dungeonID + ".yml");

        if (!configFile.exists()) {
            Dungeons.getDungeonCore().getLogger().warning("Config file not found for dungeonID: " + dungeonID);
            return 0;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.contains("lives")) {
            Dungeons.getDungeonCore().getLogger().warning("No 'lives' key found in config file for dungeonID: " + dungeonID);
            return 0;
        }

        int defaultLives = config.getInt("lives");

        int maxLivesFromPermissions = player.getEffectivePermissions().stream()
                .filter(permission -> permission.getPermission().startsWith("dungeons.lives."))
                .map(permission -> {
                    try {
                        return Integer.parseInt(permission.getPermission().replace("dungeons.lives.", ""));
                    } catch (NumberFormatException e) {
                        Dungeons.getDungeonCore().getLogger().warning("Invalid permission format: " + permission.getPermission());
                        return 0;
                    }
                })
                .max(Integer::compareTo)
                .orElse(0);
        return Math.max(maxLivesFromPermissions, defaultLives);
    }

    public static void startPartyDungeon(Player p, String dungeonID) {
        Player leader = PartyManager.getPartyLeader(p);
        if (leader != null && leader.equals(p)) {
            List<Player> players = PartyManager.getMembers(p);
            File configFile = new File(Dungeons.getDungeonCore().getDataFolder(), "Dungeons/" + dungeonID + ".yml");
            if (configFile.exists()) {
                FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
                String worldName = config.getString("world");
                if (config.getInt("party_maximum", 0) >= PartyManager.getMembers(p).size()) {
                    if (worldName != null) {
                        File srcDir = new File(Dungeons.getDungeonCore().getDataFolder(), "Worlds/" + worldName);
                        File srcNormalDir = new File(Bukkit.getServer().getWorldContainer(), worldName);
                        if (srcDir.exists() || srcNormalDir.exists()) {
                            if (config.contains("name") && config.getString("name") != null) {
                                if (checkStatus(players)) {
                                    if (checkReq(players, dungeonID)) {
                                        updateStatus(players);
                                        players.forEach(player -> Chat.sendMessage(player,
                                                Objects.requireNonNull(Files.getMessage().getString("user.start_dungeon"))
                                                        .replace("<name>", Objects.requireNonNull(config.getString("name")))));
                                        copy(worldName, worldName + "_" + p.getName() + "_" + dungeonID);
                                        String locationJoin = config.getString("location.join");
                                        if (locationJoin != null) {
                                            double x = Double.parseDouble(locationJoin.split(";")[0]);
                                            double y = Double.parseDouble(locationJoin.split(";")[1]);
                                            double z = Double.parseDouble(locationJoin.split(";")[2]);
                                            float yaw;
                                            float pitch;
                                            if (locationJoin.split(";").length == 5) {
                                                yaw = Float.parseFloat(locationJoin.split(";")[3]);
                                                pitch = Float.parseFloat(locationJoin.split(";")[4]);
                                            } else {
                                                pitch = 0;
                                                yaw = 0;
                                            }
                                            World world = Bukkit.getWorld(worldName + "_" + p.getName() + "_" + dungeonID);
                                            if (world != null) {
                                                CountdownTimer countdownTimer = new CountdownTimer(Dungeons.getDungeonCore(), config.getInt("times.start", 3),
                                                        () -> players.forEach(player -> Chat.sendMessage(player, Objects.requireNonNull(Files.getMessage().getString("dungeons.times.start_soon")))),
                                                        (t) -> {
                                                            if (Files.getConfig().getIntegerList("dungeon.show_countdown_at").contains(t.getSecondsLeft())) {
                                                                players.forEach(player -> Chat.sendMessage(player,
                                                                        Objects.requireNonNull(Files.getMessage().getString("dungeons.times.start")).replace("<second>", String.valueOf(t.getSecondsLeft()))));
                                                            }
                                                        },
                                                        () -> {
                                                            Location location = new Location(world, x, y, z, yaw, pitch);
                                                            players.forEach(player -> {
                                                                gamemode.put(player, player.getGameMode());
                                                                player.teleport(location);
                                                                lives.put(player.getName() + "_" + dungeonID, getDefaultLives(player, dungeonID));
                                                                List<String> commands = config.getStringList("commands.join");
                                                                if (!commands.isEmpty()) {
                                                                    commands.forEach(cmd -> new BukkitRunnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Dungeons.getDungeonCore().getServer().dispatchCommand(Dungeons.getDungeonCore().getServer().getConsoleSender(), PlaceholderAPI.setPlaceholders(player, cmd));
                                                                        }
                                                                    }.runTask(Dungeons.getDungeonCore()));
                                                                }
                                                            });
                                                            checkPoints.put(PartyManager.getPlayer(p).getName() + "_" + dungeonID, location);
                                                            dungeon.put(p, dungeonID);
                                                            stage.put(p.getName() + "_" + dungeonID, 0);
                                                            nextStage(p);
                                                        });
                                                countdownTimer.scheduleTimer();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (config.getInt("party_maximum", 0) > 0) {
                        PartyManager.getMembers(p).forEach(player -> Chat.sendMessage(player, Objects.requireNonNull(Files.getMessage().getString("party.full_party"))
                                .replace("<dungeon>", Objects.requireNonNull(config.getString("name")))
                                .replace("<amount>", String.valueOf(config.getInt("party_maximum", 0)))
                        ));
                    } else {
                        PartyManager.getMembers(p).forEach(player -> Chat.sendMessage(player, Objects.requireNonNull(Files.getMessage().getString("party.not_accept_party"))
                                .replace("<dungeon>", Objects.requireNonNull(config.getString("name")))
                        ));
                    }
                }
            }
        }
    }

    public static void endPartyDungeon(Player p, boolean isComplete, boolean remove_countdown) {
        Player leader = PartyManager.getPartyLeader(p);
        if (leader != null && leader.equals(p)) {
            List<Player> players = PartyManager.getMembers(p);
            if (inPartyDungeon(players)) {
                String dungeonID = getPlayerDungeon(p);
                FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
                String locationComplete = config.getString("location.complete");
                if (locationComplete != null) {
                    World world = Bukkit.getWorld(locationComplete.split(";")[0]);
                    if (world != null) {
                        if (checkEndStatus(players)) {
                            players.forEach(player -> status.replace(player, DungeonStatus.ENDING));
                            players.forEach(player ->
                                    Chat.sendMessage(player, Objects.requireNonNull(Files.getMessage().getString("user.end_dungeon"))
                                            .replace("<name>", Objects.requireNonNull(config.getString("name")))));
                            Location rLocation = getLocation(locationComplete.replace(locationComplete.split(";")[0] + ";", ""), world);
                            CountdownTimer countdownTimer = new CountdownTimer(Dungeons.getDungeonCore(),
                                    !remove_countdown ? config.getInt("times.complete", 3) : 0,
                                    () -> players.forEach(player -> Chat.sendMessage(player, Objects.requireNonNull(Files.getMessage().getString("dungeons.times.end_soon")))),
                                    (t) -> {
                                        if (Files.getConfig().getIntegerList("dungeon.show_countdown_at").contains(t.getSecondsLeft())) {
                                            players.forEach(player ->
                                                    Chat.sendMessage(player, Objects.requireNonNull(Files.getMessage().getString("dungeons.times.end")).replace("<second>", String.valueOf(t.getSecondsLeft()))));
                                        }
                                    },
                                    () -> {
                                        for (Player player : p.getWorld().getPlayers())
                                            player.teleport(rLocation);
                                        players.forEach(player -> {
                                            player.removePotionEffect(PotionEffectType.BLINDNESS);
                                            player.setGameMode(gamemode.get(player));
                                            lives.remove(player.getName() + "_" + dungeonID);
                                            status.replace(player, DungeonStatus.NONE);
                                            if (isComplete) {
                                                List<String> commands = config.getStringList("commands.complete");
                                                if (!commands.isEmpty()) {
                                                    commands.forEach(cmd -> new BukkitRunnable() {
                                                        @Override
                                                        public void run() {
                                                            Dungeons.getDungeonCore().getServer().dispatchCommand(Dungeons.getDungeonCore().getServer().getConsoleSender(), PlaceholderAPI.setPlaceholders(player, cmd));
                                                        }
                                                    }.runTask(Dungeons.getDungeonCore()));
                                                }
                                            }
                                        });
                                        delete(config.getString("world") + "_" + p.getName() + "_" + dungeonID);
                                        stage.remove(p.getName() + "_" + dungeonID);
                                        checkPoints.remove(PartyManager.getPlayer(p).getName() + "_" + dungeonID);
                                        dungeon.remove(p, dungeonID);
                                        gamemode.remove(p);
                                    });
                            countdownTimer.scheduleTimer();
                        }
                    }
                }
            }
        }
    }

    public static void quitParty(Player p) {
        if (PartyManager.inParty(p)) {
            Player leader = PartyManager.getPartyLeader(p);
            if (leader != null && leader != p) {
                if (inDungeon(p)) {
                    String dungeonID = getPlayerDungeon(p);
                    FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
                    String locationComplete = config.getString("location.complete");
                    if (locationComplete != null) {
                        World world = Bukkit.getWorld(locationComplete.split(";")[0]);
                        Location rLocation = getLocation(locationComplete.replace(locationComplete.split(";")[0] + ";", ""), world);
                        status.replace(p, DungeonStatus.NONE);
                        p.teleport(rLocation);
                    }
                }
                PartyManager.kick(p);
            }
        }
    }

    private static boolean inPartyDungeon(@NotNull List<Player> players) {
        int amountCheck = 0;
        for (Player p : players) {
            if (inDungeon(PartyManager.getPlayer(p))) {
                amountCheck++;
            }
        }
        return amountCheck == players.size();
    }

    @Contract(pure = true)
    private static void updateStatus(@NotNull List<Player> players) {
        for (Player p : players) {
            if (!status.containsKey(p)) status.put(p, DungeonStatus.STARTING);
            else status.replace(p, DungeonStatus.STARTING);
        }
    }

    @Contract(pure = true)
    private static boolean checkReq(@NotNull List<Player> players, String dungeonID) {
        int amountCheck = 0;
        for (Player p : players) {
            if (StageManager.checkRequirements(p, dungeonID)) {
                amountCheck++;
            }
        }
        return amountCheck == players.size();
    }

    @Contract(pure = true)
    private static boolean checkStatus(@NotNull List<Player> players) {
        int amountCheck = 0;
        for (Player p : players) {
            if (!status.containsKey(p) || (status.containsKey(p) && status.get(p).equals(DungeonStatus.NONE))) {
                amountCheck++;
            }
        }
        return amountCheck == players.size();
    }

    @Contract(pure = true)
    private static boolean checkEndStatus(@NotNull List<Player> players) {
        int amountCheck = 0;
        for (Player p : players) {
            if (status.containsKey(p) && status.get(p).equals(DungeonStatus.STARTING)) {
                amountCheck++;
            }
        }
        return amountCheck == players.size();
    }

    public static void startDungeon(Player p, String dungeonID) {
        File configFile = new File(Dungeons.getDungeonCore().getDataFolder(), "Dungeons/" + dungeonID + ".yml");
        if (configFile.exists()) {
            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
            String worldName = config.getString("world");
            if (worldName != null) {
                File srcDir = new File(Dungeons.getDungeonCore().getDataFolder(), "Worlds/" + worldName);
                File srcNormalDir = new File(Bukkit.getServer().getWorldContainer(), worldName);
                if (srcDir.exists() || srcNormalDir.exists()) {
                    if (config.contains("name") && config.getString("name") != null) {
                        if (!status.containsKey(p) || (status.containsKey(p) && status.get(p).equals(DungeonStatus.NONE))) {
                            if (StageManager.checkRequirements(p, dungeonID)) {
                                if (!status.containsKey(p)) status.put(p, DungeonStatus.STARTING);
                                else status.replace(p, DungeonStatus.STARTING);
                                Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("user.start_dungeon")).replace("<name>", Objects.requireNonNull(config.getString("name"))));
                                copy(worldName, worldName + "_" + p.getName() + "_" + dungeonID);
                                String locationJoin = config.getString("location.join");
                                if (locationJoin != null) {
                                    double x = Double.parseDouble(locationJoin.split(";")[0]);
                                    double y = Double.parseDouble(locationJoin.split(";")[1]);
                                    double z = Double.parseDouble(locationJoin.split(";")[2]);
                                    float yaw;
                                    float pitch;
                                    if (locationJoin.split(";").length == 5) {
                                        yaw = Float.parseFloat(locationJoin.split(";")[3]);
                                        pitch = Float.parseFloat(locationJoin.split(";")[4]);
                                    } else {
                                        pitch = 0;
                                        yaw = 0;
                                    }
                                    World world = Bukkit.getWorld(worldName + "_" + p.getName() + "_" + dungeonID);
                                    if (world != null) {
                                        CountdownTimer countdownTimer = new CountdownTimer(Dungeons.getDungeonCore(), config.getInt("times.start", 3), () -> Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("dungeons.times.start_soon"))), (t) -> {
                                            if (Files.getConfig().getIntegerList("dungeon.show_countdown_at").contains(t.getSecondsLeft())) {
                                                Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("dungeons.times.start")).replace("<second>", String.valueOf(t.getSecondsLeft())));
                                            }
                                        }, () -> {
                                            gamemode.put(p, p.getGameMode());
                                            Location location = new Location(world, x, y, z, yaw, pitch);
                                            p.teleport(location);
                                            List<String> commands = config.getStringList("commands.join");
                                            if (!commands.isEmpty()) {
                                                commands.forEach(cmd -> new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        Dungeons.getDungeonCore().getServer().dispatchCommand(Dungeons.getDungeonCore().getServer().getConsoleSender(), PlaceholderAPI.setPlaceholders(p, cmd));
                                                    }
                                                }.runTask(Dungeons.getDungeonCore()));
                                            }
                                            dungeon.put(p, dungeonID);
                                            stage.put(p.getName() + "_" + dungeonID, 0);
                                            lives.put(p.getName() + "_" + dungeonID, getDefaultLives(p, dungeonID));
                                            checkPoints.put(p.getName() + "_" + dungeonID, location);
                                            nextStage(p);
                                        });
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
                        Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("user.end_dungeon")).replace("<name>", Objects.requireNonNull(config.getString("name"))));
                        Location rLocation = getLocation(locationComplete.replace(locationComplete.split(";")[0] + ";", ""), world);
                        CountdownTimer countdownTimer = new CountdownTimer(Dungeons.getDungeonCore(),
                                !remove_countdown ? config.getInt("times.complete", 3) : 0,
                                () -> Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("dungeons.times.end_soon"))),
                                (t) -> {
                                    if (Files.getConfig().getIntegerList("dungeon.show_countdown_at").contains(t.getSecondsLeft())) {
                                        Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("dungeons.times.end")).replace("<second>", String.valueOf(t.getSecondsLeft())));
                                    }
                                },
                                () -> {
                                    p.removePotionEffect(PotionEffectType.BLINDNESS);
                                    p.setGameMode(gamemode.get(p));
                                    for (Player player : p.getWorld().getPlayers())
                                        player.teleport(rLocation);
                                    delete(config.getString("world") + "_" + p.getName() + "_" + dungeonID);
                                    if (isComplete) {
                                        List<String> commands = config.getStringList("commands.complete");
                                        if (!commands.isEmpty()) {
                                            commands.forEach(cmd -> new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    Dungeons.getDungeonCore().getServer().dispatchCommand(Dungeons.getDungeonCore().getServer().getConsoleSender(), PlaceholderAPI.setPlaceholders(p, cmd));
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
                                });
                        countdownTimer.scheduleTimer();
                    }
                }
            }
        }
    }

    public static @NotNull Location getLocation(String location, World world) {
        double x = Double.parseDouble(location.split(";")[0]);
        double y = Double.parseDouble(location.split(";")[1]);
        double z = Double.parseDouble(location.split(";")[2]);
        float yaw;
        float pitch;
        if (location.split(";").length == 5) {
            yaw = Float.parseFloat(location.split(";")[3]);
            pitch = Float.parseFloat(location.split(";")[4]);
        } else {
            pitch = 0;
            yaw = 0;
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Contract(pure = true)
    public static @Nullable String getStageDisplay(Player p) {
        if (inDungeon(PartyManager.getPlayer(p))) {
            String dungeonID = dungeon.get(PartyManager.getPlayer(p));
            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
            int stageNumber = stage.get(PartyManager.getPlayer(p).getName() + "_" + dungeonID);
            String id = config.getString("stages.stage_" + stageNumber + ".id");
            if (id != null) {
                return DungeonsAPI.getStage(id).getDisplay(p);
            }
        }
        return null;
    }

    public static void nextStage(@NotNull Player p) {
        p = PartyManager.getPlayer(p);
        FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeon.get(p) + ".yml");
        int stageNumber = getStageNumber(PartyManager.getPlayer(p));
        if (config.contains("stages.stage_" + (stageNumber + 1))) {
            stage.replace(p.getName() + "_" + getPlayerDungeon(PartyManager.getPlayer(p)), stageNumber + 1);
            if (!PartyManager.inParty(p))
                Chat.sendMessage(p, getStageDisplay(p));
            else PartyManager.getMembers(p).forEach(player ->
                    Chat.sendMessage(player, getStageDisplay(PartyManager.getPlayer(player))));
            if (config.contains("stages.stage_" + (stageNumber + 1) + ".pre_stage")) {
                String id = config.getString("stages.stage_" + (stageNumber + 1) + ".id");
                String id_pre = config.getString("stages.stage_" + (stageNumber + 1) + ".pre_stage.id");
                if (id_pre != null) {
                    DungeonsAPI.getStage(id).activePreStage(p);
                }
            }
        } else {
            if (inDungeon(p)) {
                if (!PartyManager.inParty(p))
                    StageManager.endDungeon(p, true, false);
                else StageManager.endPartyDungeon(p, true, false);
            }
        }
    }

    public static int getStageNumber(Player p) {
        if (inDungeon(PartyManager.getPlayer(p))) {
            return stage.get(PartyManager.getPlayer(p).getName() + "_" + dungeon.get(p));
        }
        return -1;
    }

    public static String getPlayerDungeon(Player p) {
        return dungeon.getOrDefault(PartyManager.getPlayer(p), null);
    }

    public static boolean inDungeon(Player p) {
        return getPlayerDungeon(PartyManager.getPlayer(p)) != null;
    }
}
