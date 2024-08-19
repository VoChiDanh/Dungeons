package net.danh.dungeons;

import net.danh.dungeons.API.DungeonsAPI;
import net.danh.dungeons.Command.Dungeon_MainCMD;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.GUI.Listeners.Chat;
import net.danh.dungeons.GUI.Listeners.InteractBlock;
import net.danh.dungeons.GUI.Stages.Manager.StageBase;
import net.danh.dungeons.GUI.Stages.Manager.StageRegistry;
import net.danh.dungeons.Listeners.*;
import net.danh.dungeons.Placeholder.DungeonPAPI;
import net.danh.dungeons.Resources.Files;
import net.danh.dungeons.Utils.MythicAPI;
import net.danh.dungeons.Utils.UpdateChecker;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.browsit.milkgui.MilkGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class Dungeons extends JavaPlugin {

    public static HashMap<String, StageBase> stageBase = new HashMap<>();

    private static Dungeons dungeons;
    private static MythicAPI mythicAPI;
    private static boolean isMythicMobsInstalled = false;
    private static boolean isMMOItemsInstalled = false;

    public static Dungeons getDungeonCore() {
        return dungeons;
    }

    public static boolean isIsMythicMobsInstalled() {
        return isMythicMobsInstalled;
    }

    public static boolean isIsMMOItemsInstalled() {
        return isMMOItemsInstalled;
    }

    public static MythicAPI getMythicAPI() {
        return mythicAPI;
    }

    @Override
    public void onEnable() {
        dungeons = this;
        SimpleConfigurationManager.register(dungeons);
        MilkGUI.INSTANCE.register(dungeons);
        Files.loadFiles();
        Files.updateFiles();
        new Dungeon_MainCMD();
        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new DungeonPAPI().register();
        }
        DungeonsAPI.addPreStage("v_spawn_mob", "mm_spawn_mob");
        if (Bukkit.getServer().getPluginManager().getPlugin("MythicMobs") != null) {
            isMythicMobsInstalled = true;
            mythicAPI = new MythicAPI();
            Dungeons.getDungeonCore().getLogger().info("Compatible with MythicMobs");
        }
        if (Bukkit.getServer().getPluginManager().getPlugin("MMOItems") != null) {
            isMMOItemsInstalled = true;
            Dungeons.getDungeonCore().getLogger().info("Compatible with MMOItems");
        }
        registerEvents(new ReachLocation(), new VanillaMobs(), new Death(), new JoinQuit(), new BlockBreak(), new Chat(), new InteractBlock(), new BlackListCMD());
        registerStages();
        registerEvents(new UpdateChecker(dungeons));
        new UpdateChecker(dungeons).fetch();
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (StageManager.inDungeon(p)) {
                StageManager.endDungeon(p, false, true);
            }
        }
        Files.saveFiles();
    }

    private void registerEvents(Listener... listeners) {
        Arrays.asList(listeners).forEach(listener -> {
            Bukkit.getPluginManager().registerEvents(listener, dungeons);
            getLogger().info("Registered Listeners " + listener.getClass().getSimpleName());
        });
    }

    private void registerStages() {
        List<StageBase> stageBases = new ArrayList<>();
        for (Class<? extends StageBase> type : StageRegistry.getStage()) {
            try {
                stageBases.add(type.getDeclaredConstructor().newInstance());
                stageBase.put(type.getDeclaredConstructor().newInstance().getID(),
                        type.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        for (StageBase stage : stageBases) {
            Dungeons.getDungeonCore().getLogger().info("Load stage " + stage.getID());
        }
    }

}
