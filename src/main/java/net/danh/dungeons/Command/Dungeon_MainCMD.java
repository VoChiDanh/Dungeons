package net.danh.dungeons.Command;

import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Dungeons;
import net.danh.dungeons.GUI.DungeonGUI.MainEditor;
import net.danh.dungeons.Resources.Chat;
import net.danh.dungeons.Resources.Files;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dungeon_MainCMD extends CMDBase {
    public Dungeon_MainCMD() {
        super("Dungeons");
    }

    @Override
    public void execute(@NotNull CommandSender c, String[] args) {
        if (c.hasPermission("dungeons.admin")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    Files.reloadFiles();
                    Chat.sendMessage(c, Files.getMessage().getString("admin.reload_files"));
                }
            }
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("quit")) {
                if (c instanceof Player) {
                    Player p = (Player) c;
                    if (StageManager.inDungeon(p)) {
                        StageManager.endDungeon(p, false, false);
                    }
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {
                if (c instanceof Player) {
                    Player p = (Player) c;
                    StageManager.startDungeon(p, args[1]);
                }
            }
            if (args[0].equalsIgnoreCase("editor")) {
                if (c.hasPermission("dungeons.admin")) {
                    if (c instanceof Player) {
                        Player p = (Player) c;
                        new MainEditor(p, args[1]).openInventory(p);
                    }
                }
            }
        }
    }

    @Override
    public List<String> TabComplete(CommandSender sender, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("dungeons.admin")) {
                commands.add("reload");
                commands.add("editor");
            }
            commands.add("start");
            commands.add("quit");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")
                    || args[0].equalsIgnoreCase("editor")) {
                File dataFolder = new File(Dungeons.getDungeonCore().getDataFolder(), "Dungeons");
                File[] files = dataFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            commands.add(file.getName().replace(".yml", ""));
                        }
                    }
                }
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
