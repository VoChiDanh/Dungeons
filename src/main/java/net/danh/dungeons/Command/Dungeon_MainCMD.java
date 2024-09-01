package net.danh.dungeons.Command;

import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Dungeons;
import net.danh.dungeons.GUI.DungeonGUI.MainEditor;
import net.danh.dungeons.Party.PartyManager;
import net.danh.dungeons.Resources.Chat;
import net.danh.dungeons.Resources.Files;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Dungeon_MainCMD extends CMDBase {
    public Dungeon_MainCMD() {
        super("Dungeons");
    }

    @Override
    public void execute(@NotNull CommandSender c, String[] args) {
        if (c instanceof Player) {
            Player p = (Player) c;
            if (p.hasPermission("dungeons.party")) {
                if (args.length == 2 || args.length >= 3) {
                    if (args[0].equalsIgnoreCase("party")) {
                        if (args.length == 2) {
                            if (args[1].equalsIgnoreCase("disband")) {
                                PartyManager.disbandParty(p);
                            }
                        } else if (args.length == 3) {
                            if (args[1].equalsIgnoreCase("create")) {
                                PartyManager.createParty(p, args[2]);
                            }
                            if (args[1].equalsIgnoreCase("invite")) {
                                Player invited = Bukkit.getPlayer(args[2]);
                                if (invited != null)
                                    PartyManager.invite(p, invited);
                            }
                            if (args[1].equalsIgnoreCase("kick")) {
                                Player kick = Bukkit.getPlayer(args[2]);
                                if (kick != null)
                                    if (PartyManager.inParty(p)) {
                                        Player leader = PartyManager.getPartyLeader(p);
                                        if (leader != null) {
                                            if (leader.equals(p) && !leader.equals(kick)) {
                                                PartyManager.kick(kick);
                                            }
                                        }
                                    }
                            }
                        }
                        if (args.length >= 3) {
                            if (args[1].equalsIgnoreCase("rename")) {
                                String name = String.join(" ", Arrays.asList(args).subList(2, args.length));
                                PartyManager.setDisplay(p, name);
                            }
                        }
                    }
                }
            }
        }
        if (c.hasPermission("dungeons.admin")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    Files.reloadFiles();
                    Chat.sendMessage(c, Files.getMessage().getString("admin.reload_files"));
                }
            }
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                Chat.sendMessage(c, Files.getMessage().getStringList("user.help"));
                Chat.sendMessage(c, Files.getMessage().getStringList("party.help"));
                if (c.hasPermission("dungeons.admin"))
                    Chat.sendMessage(c, Files.getMessage().getStringList("admin.help"));
            } else if (args[0].equalsIgnoreCase("quit")) {
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
            } else if (args[0].equalsIgnoreCase("editor")) {
                if (c.hasPermission("dungeons.admin")) {
                    if (c instanceof Player) {
                        Player p = (Player) c;
                        new MainEditor(p, args[1]).openInventory(p);
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("start")) {
                if (c.hasPermission("dungeons.admin")) {
                    Player p = Bukkit.getPlayer(args[2]);
                    if (p != null) {
                        StageManager.startDungeon(p, args[1]);
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
            if (sender.hasPermission("dungeons.party")) {
                commands.add("party");
            }
            commands.add("help");
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
            } else if (args[0].equalsIgnoreCase("party") && sender instanceof Player) {
                commands.add("create");
                commands.add("rename");
                commands.add("invite");
                commands.add("kick");
                commands.add("disband");
            }
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("party")
                    && sender instanceof Player) {
                if (args[1].equalsIgnoreCase("create"))
                    commands.add("PartyID");
                if (args[1].equalsIgnoreCase("rename"))
                    commands.add("PartyName");
                if (args[1].equalsIgnoreCase("invite"))
                    Bukkit.getOnlinePlayers().forEach(player -> commands.add(player.getName()));
                if (args[1].equalsIgnoreCase("kick")) {
                    Player p = (Player) sender;
                    List<String> players = PartyManager.getMembersName(p);
                    if (!players.isEmpty())
                        commands.addAll(players);
                }
            }
            if (args[0].equalsIgnoreCase("start")) {
                if (sender.hasPermission("dungeons.admin"))
                    Bukkit.getOnlinePlayers().forEach(p -> commands.add(p.getName()));
            }
            StringUtil.copyPartialMatches(args[2], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
