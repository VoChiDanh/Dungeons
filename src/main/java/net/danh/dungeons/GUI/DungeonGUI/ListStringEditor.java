package net.danh.dungeons.GUI.DungeonGUI;

import net.danh.dungeons.GUI.Editor;
import net.danh.dungeons.Resources.Chat;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.browsit.milkgui.gui.GUI;
import org.browsit.milkgui.gui.type.BasicGUI;
import org.browsit.milkgui.item.Item;
import org.browsit.milkgui.item.ItemSection;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListStringEditor extends BasicGUI {
    Player p;
    String path;
    String dungeon;

    public ListStringEditor(Player p, String dungeon, String path) {
        super(new GUI(dungeon + " - Path: " + path));
        this.p = p;
        this.path = path;
        this.dungeon = dungeon;
        SimpleConfigurationManager.get().build("", true, "Dungeons/" + dungeon + ".yml");
        FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeon + ".yml");
        List<String> list = config.contains(path) ? config.getStringList(path) : new ArrayList<>();
        if (!list.isEmpty()) {
            for (int i = 0; i <= list.size(); i++) {
                if (i < list.size()) {
                    Item item = new Item.ItemBuilder(Material.PAPER)
                            .displayName(Chat.normalColorize("&7" + path + " " + (i + 1)))
                            .lore(Chat.normalColorize("&7" + list.get(i)), " ",
                                    Chat.normalColorize("&7Left Click to edit"), Chat.normalColorize("&7Right Click to delete"))
                            .build();
                    int o = i;
                    setItem(new ItemSection(i, item, "path_" + i, e -> {
                        if (e.getWhoClicked() instanceof Player) {
                            Player clicker = (Player) e.getWhoClicked();
                            if (e.getClick().isLeftClick()) {
                                if (Editor.editorType.containsKey(clicker)) {
                                    Editor.editorType.replace(clicker, "chat");
                                } else Editor.editorType.put(clicker, "chat");
                                if (Editor.editorChatType.containsKey(clicker)) {
                                    Editor.editorChatType.replace(clicker, "list_string");
                                } else Editor.editorChatType.put(clicker, "list_string");
                                list.remove(o);
                                config.set(path, list);
                                SimpleConfigurationManager.get().save("Dungeons/" + dungeon + ".yml");
                                SimpleConfigurationManager.get().reload("Dungeons/" + dungeon + ".yml");
                                clicker.closeInventory();
                                if (path.equalsIgnoreCase("requirements.info")) {
                                    Chat.sendMessage(clicker, "&bFormat Tips: %placeholder%;<compare>;<amount>");
                                    Chat.sendMessage(clicker, "&b    %placeholder% is placeholder from PlaceholderAPI");
                                    Chat.sendMessage(clicker, "&b    <compare> include: >, >=, <, <=, ==");
                                    Chat.sendMessage(clicker, "&b    <amount> mean number");
                                    Chat.sendMessage(clicker, "&b    Example: %player_level%;>=;5");
                                } else if (path.equalsIgnoreCase("requirements.item")) {
                                    Chat.sendMessage(clicker, "&bFormat Tips: <plugin>;<item_data>;<amount>");
                                    Chat.sendMessage(clicker, "&b    <plugin> is item plugin: MMOITEMS");
                                    Chat.sendMessage(clicker, "&b    <amount> mean number");
                                    Chat.sendMessage(clicker, "&b    Example: MMOITEMS;MATERIAL;DUNGEON_TICKET;1");
                                    Chat.sendMessage(clicker, "&b    Example: VANILLA;DIAMOND;1");
                                    Chat.sendMessage(clicker, "&b    Example: ITEMEDIT;ITEM_ID;1");
                                } else if (path.equalsIgnoreCase("requirements.info_lore")) {
                                    Chat.sendMessage(clicker, "&bCustom display for requirements info");
                                } else if (path.equalsIgnoreCase("requirements.item_lore")) {
                                    Chat.sendMessage(clicker, "&bCustom display for requirements item");
                                } else if (path.startsWith("commands")) {
                                    Chat.sendMessage(clicker, "&bSupport placeholder from PlaceholderAPI");
                                    Chat.sendMessage(clicker, "&b   Example: eco give %player_name% 1000");
                                }
                            }
                            if (e.getClick().isRightClick()) {
                                list.remove(o);
                                config.set(path, list);
                                SimpleConfigurationManager.get().save("Dungeons/" + dungeon + ".yml");
                                SimpleConfigurationManager.get().reload("Dungeons/" + dungeon + ".yml");
                                Editor.editorType.remove(p);
                                Editor.editorDungeonByBreak.remove(p.getName() + "_" + dungeon);
                                Editor.editorDungeonInfo.remove(p.getName() + "_" + dungeon);
                                Editor.editorDungeon.remove(p);
                                Editor.editorChatType.remove(p);
                                net.danh.dungeons.GUI.Listeners.Chat.chatEdit.remove(p.getName() + "_" + dungeon);
                                net.danh.dungeons.GUI.Listeners.Chat.chatNumberEdit.remove(p.getName() + "_" + dungeon);
                                clicker.closeInventory();
                                new MainEditor(p, dungeon).openInventory(p);
                            }
                        }
                    }));
                } else {
                    Item item = new Item.ItemBuilder(Material.BOOK)
                            .displayName(Chat.normalColorize("&7Create " + path + " " + (i + 1)))
                            .lore(Chat.normalColorize(Chat.normalColorize("&7Left Click to create")))
                            .build();
                    setItem(new ItemSection(i, item, "path_" + i, e -> {
                        if (e.getWhoClicked() instanceof Player) {
                            Player clicker = (Player) e.getWhoClicked();
                            if (e.getClick().isLeftClick()) {
                                if (Editor.editorType.containsKey(clicker)) {
                                    Editor.editorType.replace(clicker, "chat");
                                } else Editor.editorType.put(clicker, "chat");
                                if (Editor.editorChatType.containsKey(clicker)) {
                                    Editor.editorChatType.replace(clicker, "list_string");
                                } else Editor.editorChatType.put(clicker, "list_string");
                                SimpleConfigurationManager.get().save("Dungeons/" + dungeon + ".yml");
                                SimpleConfigurationManager.get().reload("Dungeons/" + dungeon + ".yml");
                                clicker.closeInventory();
                                if (path.equalsIgnoreCase("requirements.info")) {
                                    Chat.sendMessage(clicker, "&bFormat Tips: %placeholder%;<compare>;<amount>");
                                    Chat.sendMessage(clicker, "&b    %placeholder% is placeholder from PlaceholderAPI");
                                    Chat.sendMessage(clicker, "&b    <compare> include: >, >=, <, <=, ==");
                                    Chat.sendMessage(clicker, "&b    <amount> mean number");
                                    Chat.sendMessage(clicker, "&b    Example: %player_level%;>=;5");
                                } else if (path.equalsIgnoreCase("requirements.item")) {
                                    Chat.sendMessage(clicker, "&bFormat Tips: <plugin>;<item_data>;<amount>");
                                    Chat.sendMessage(clicker, "&b    <plugin> is item plugin: MMOITEMS");
                                    Chat.sendMessage(clicker, "&b    <amount> mean number");
                                    Chat.sendMessage(clicker, "&b    Example: MMOITEMS;MATERIAL;DUNGEON_TICKET;1");
                                    Chat.sendMessage(clicker, "&b    Example: VANILLA;DIAMOND;1");
                                    Chat.sendMessage(clicker, "&b    Example: ITEMEDIT;ITEM_ID;1");
                                } else if (path.equalsIgnoreCase("requirements.info_lore")) {
                                    Chat.sendMessage(clicker, "&bCustom display for requirements info");
                                } else if (path.equalsIgnoreCase("requirements.item_lore")) {
                                    Chat.sendMessage(clicker, "&bCustom display for requirements item");
                                } else if (path.startsWith("commands")) {
                                    Chat.sendMessage(clicker, "&bSupport placeholder from PlaceholderAPI");
                                    Chat.sendMessage(clicker, "&b   Example: eco give %player_name% 1000");
                                }
                            }
                        }
                    }));
                }
            }
        } else {
            Item item = new Item.ItemBuilder(Material.BOOK)
                    .displayName(Chat.normalColorize("&7Create " + path + " " + (1)))
                    .lore(Chat.normalColorize(Chat.normalColorize("&7Left Click to create")))
                    .build();
            setItem(new ItemSection(0, item, "path_" + 0, e -> {
                if (e.getWhoClicked() instanceof Player) {
                    Player clicker = (Player) e.getWhoClicked();
                    if (e.getClick().isLeftClick()) {
                        if (Editor.editorType.containsKey(clicker)) {
                            Editor.editorType.replace(clicker, "chat");
                        } else Editor.editorType.put(clicker, "chat");
                        if (Editor.editorChatType.containsKey(clicker)) {
                            Editor.editorChatType.replace(clicker, "list_string");
                        } else Editor.editorChatType.put(clicker, "list_string");
                        SimpleConfigurationManager.get().save("Dungeons/" + dungeon + ".yml");
                        SimpleConfigurationManager.get().reload("Dungeons/" + dungeon + ".yml");
                        clicker.closeInventory();
                        if (path.equalsIgnoreCase("requirements.info")) {
                            Chat.sendMessage(clicker, "&bFormat Tips: %placeholder%;<compare>;<amount>");
                            Chat.sendMessage(clicker, "&b    %placeholder% is placeholder from PlaceholderAPI");
                            Chat.sendMessage(clicker, "&b    <compare> include: >, >=, <, <=, ==");
                            Chat.sendMessage(clicker, "&b    <amount> mean number");
                            Chat.sendMessage(clicker, "&b    Example: %player_level%;>=;5");
                        } else if (path.equalsIgnoreCase("requirements.item")) {
                            Chat.sendMessage(clicker, "&bFormat Tips: <plugin>;<item_data>;<amount>");
                            Chat.sendMessage(clicker, "&b    <plugin> is item plugin: MMOITEMS");
                            Chat.sendMessage(clicker, "&b    <amount> mean number");
                            Chat.sendMessage(clicker, "&b    Example: MMOITEMS;MATERIAL;DUNGEON_TICKET;1");
                            Chat.sendMessage(clicker, "&b    Example: VANILLA;DIAMOND;1");
                            Chat.sendMessage(clicker, "&b    Example: ITEMEDIT;ITEM_ID;1");
                        } else if (path.equalsIgnoreCase("requirements.info_lore")) {
                            Chat.sendMessage(clicker, "&bCustom display for requirements info");
                        } else if (path.equalsIgnoreCase("requirements.item_lore")) {
                            Chat.sendMessage(clicker, "&bCustom display for requirements item");
                        } else if (path.startsWith("commands")) {
                            Chat.sendMessage(clicker, "&bSupport placeholder from PlaceholderAPI");
                            Chat.sendMessage(clicker, "&b   Example: eco give %player_name% 1000");
                        }
                    }
                }
            }));
        }
    }
}
