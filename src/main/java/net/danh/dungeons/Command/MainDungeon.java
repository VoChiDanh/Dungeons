package net.danh.dungeons.Command;

import net.danh.dungeons.Manager.DungeonManager;
import net.danh.dungeons.Manager.Dungeons;
import net.danh.dungeons.Resources.Chat;
import net.danh.dungeons.Resources.Files;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainDungeon extends CMDBase {
    public MainDungeon() {
        super("dungeons");
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
        if (args.length == 1){

        }
        if (c instanceof Player p) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("start")) {
                    String dungeon = args[1];
                    Dungeons dungeons = new Dungeons(dungeon);
                    dungeons.onStart(p);
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("quit")) {
                    if (DungeonManager.dungeonStats.containsKey(p.getName() + "_dungeon")) {
                        String dungeon = DungeonManager.dungeonStats.get(p.getName() + "_dungeon");
                        Dungeons dungeons = new Dungeons(dungeon);
                        dungeons.run();
                        dungeons.onComplete(p);
                    }
                }
            }
        }
        if (args.length == 3) {
            Player p = Bukkit.getPlayer(args[2]);
            if (p != null) {
                if (args[0].equalsIgnoreCase("start")) {
                    String dungeon = args[1];
                    Dungeons dungeons = new Dungeons(dungeon);
                    dungeons.onStart(p);
                }
            }
        } else if (args.length == 2) {
            Player p = Bukkit.getPlayer(args[1]);
            if (p != null) {
                if (args[0].equalsIgnoreCase("quit")) {
                    if (DungeonManager.dungeonStats.containsKey(p.getName() + "_dungeon")) {
                        String dungeon = DungeonManager.dungeonStats.get(p.getName() + "_dungeon");
                        Dungeons dungeons = new Dungeons(dungeon);
                        dungeons.onComplete(p);
                    }
                }
            }
        }
    }

    @Override
    public List<String> TabComplete(CommandSender sender, String @NotNull [] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("dungeons.admin")) {
                commands.add("reload");
            }
            commands.add("start");
            commands.add("quit");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {
                commands.addAll(DungeonManager.getDungeon());
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
