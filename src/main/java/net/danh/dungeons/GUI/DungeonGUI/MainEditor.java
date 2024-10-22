package net.danh.dungeons.GUI.DungeonGUI;

import net.danh.dungeons.GUI.Editor;
import net.danh.dungeons.Resources.Chat;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.browsit.milkgui.gui.GUI;
import org.browsit.milkgui.gui.type.BasicGUI;
import org.browsit.milkgui.item.Item;
import org.browsit.milkgui.item.ItemSection;
import org.browsit.milkgui.util.Rows;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Objects;

public class MainEditor extends BasicGUI {
    Player p;
    String dungeonID;

    public MainEditor(Player player, String dungeon) {
        super(new GUI("Dungeons Editor: " + dungeon, Rows.TWO));
        this.p = player;
        this.dungeonID = dungeon;
        if (Editor.editorDungeon.containsKey(p)) {
            Editor.editorDungeon.replace(p, dungeonID);
        } else Editor.editorDungeon.put(p, dungeonID);
        SimpleConfigurationManager.get().build("", true, "Dungeons/" + dungeon + ".yml");
        FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeon + ".yml");
        Item dungeonName = new Item.ItemBuilder(Material.PAPER).displayName(Chat.normalColorize("&bDungeon Display Name")).lore(config.contains("name") ? Chat.normalColorize(config.getString("name")) : "").build();
        setItem(new ItemSection(0, dungeonName, "dungeon_display_name", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_display_name");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_display_name");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "string");
                } else Editor.editorChatType.put(clicker, "string");
                clicker.closeInventory();
                Chat.sendMessage(clicker, "&bDungeon name:");
            }
        }));
        Item worldName = new Item.ItemBuilder(Material.DIRT).displayName(Chat.normalColorize("&bDungeon World")).lore(config.contains("world") ? Chat.normalColorize("&7" + config.getString("world")) : "").build();
        setItem(new ItemSection(1, worldName, "dungeon_world", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_world");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_world");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "string");
                } else Editor.editorChatType.put(clicker, "string");
                clicker.closeInventory();
                Chat.sendMessage(clicker, "&bDungeon world:");
            }
        }));
        Item lives = new Item.ItemBuilder(Material.REDSTONE).displayName(Chat.normalColorize("&bDungeon Lives")).lore(config.contains("lives") ? Chat.normalColorize("&7" + config.getInt("lives")) : "").build();
        setItem(new ItemSection(2, lives, "dungeon_lives", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_lives");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_lives");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "int");
                } else Editor.editorChatType.put(clicker, "int");
                clicker.closeInventory();
                Chat.sendMessage(clicker, "&bDungeon lives:");
            }
        }));
        Item party_maximum = new Item.ItemBuilder(Material.IRON_BOOTS).displayName(Chat.normalColorize("&bDungeon Party Maximum")).lore(config.contains("party_maximum") ? Chat.normalColorize("&7" + config.getInt("party_maximum", 0)) : "").build();
        setItem(new ItemSection(3, party_maximum, "party_maximum", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "party_maximum");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "party_maximum");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "int");
                } else Editor.editorChatType.put(clicker, "int");
                clicker.closeInventory();
                Chat.sendMessage(clicker, "&bDungeon Party Maximum:");
            }
        }));
        Item startTimes = new Item.ItemBuilder(Material.CLOCK).displayName(Chat.normalColorize("&bDungeon Start Times")).lore(config.contains("times.start") ? Chat.normalColorize("&7" + config.getInt("times.start")) : "").build();
        setItem(new ItemSection(4, startTimes, "dungeon_start_times", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_start_times");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_start_times");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "int");
                } else Editor.editorChatType.put(clicker, "int");
                clicker.closeInventory();
                Chat.sendMessage(clicker, "&bDungeon start times:");
            }
        }));
        Item completeTimes = new Item.ItemBuilder(Material.CLOCK).displayName(Chat.normalColorize("&bDungeon Complete Times")).lore(config.contains("times.complete") ? Chat.normalColorize("&7" + config.getInt("times.complete")) : "").build();
        setItem(new ItemSection(5, completeTimes, "dungeon_complete_times", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_complete_times");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_complete_times");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "int");
                } else Editor.editorChatType.put(clicker, "int");
                clicker.closeInventory();
                Chat.sendMessage(clicker, "&bDungeon complete times:");
            }
        }));
        Item startLocation = new Item.ItemBuilder(Material.COMPASS).displayName(Chat.normalColorize("&bDungeon Start Location")).lore(config.contains("location.join") ? Chat.normalColorize("&7" + config.getString("location.join")) : "").build();
        setItem(new ItemSection(6, startLocation, "dungeon_start_location", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_start_location");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_start_location");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "break_block");
                } else Editor.editorType.put(clicker, "break_block");
                clicker.closeInventory();
                Chat.sendMessage(clicker, "&bGo to the dungeon map and break the block as the location you want to set as your starting location");
            }
        }));
        Item completeLocation = new Item.ItemBuilder(Material.COMPASS).displayName(Chat.normalColorize("&bDungeon Complete Location")).lore(config.contains("location.complete") ? Chat.normalColorize("&7" + config.getString("location.complete")) : "").build();
        setItem(new ItemSection(7, completeLocation, "dungeon_complete_location", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_complete_location");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_complete_location");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "break_block");
                } else Editor.editorType.put(clicker, "break_block");
                clicker.closeInventory();
                Chat.sendMessage(clicker, "&bGo to the default map (NOT DUNGEON MAP) and break the block as the location you want to set as the location when completing your dungeon");
            }
        }));
        Item requirementsInfo = new Item.ItemBuilder(Material.RED_DYE).displayName(Chat.normalColorize("&bDungeon Requirements Info")).lore(config.contains("requirements.info") ? Chat.normalColorize(config.getStringList("requirements.info")) : Chat.normalColorize(Collections.singletonList("&7"))).build();
        setItem(new ItemSection(8, requirementsInfo, "dungeon_requirements_info", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_requirements_info");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_requirements_info");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "list_string");
                } else Editor.editorChatType.put(clicker, "list_string");
                clicker.closeInventory();
                new ListStringEditor(p, dungeon, "requirements.info").openInventory(p);
            }
        }));
        Item requirementsItem = new Item.ItemBuilder(Material.IRON_BOOTS).displayName(Chat.normalColorize("&bDungeon Requirements Item")).lore(config.contains("requirements.item") ? Chat.normalColorize(config.getStringList("requirements.item")) : Chat.normalColorize(Collections.singletonList("&7"))).build();
        setItem(new ItemSection(9, requirementsItem, "dungeon_requirements_item", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_requirements_item");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_requirements_item");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "list_string");
                } else Editor.editorChatType.put(clicker, "list_string");
                clicker.closeInventory();
                new ListStringEditor(p, dungeon, "requirements.item").openInventory(p);
            }
        }));
        Item requirementsInfoLore = new Item.ItemBuilder(Material.PAPER).displayName(Chat.normalColorize("&bDungeon Requirements Info Lore")).lore(config.contains("requirements.info_lore") ? Chat.normalColorize(config.getStringList("requirements.info_lore")) : Chat.normalColorize(Collections.singletonList("&7"))).build();
        setItem(new ItemSection(10, requirementsInfoLore, "dungeon_requirements_info_lore", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_requirements_info_lore");
                } else
                    Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_requirements_info_lore");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "list_string");
                } else Editor.editorChatType.put(clicker, "list_string");
                clicker.closeInventory();
                new ListStringEditor(p, dungeon, "requirements.info_lore").openInventory(p);
            }
        }));
        Item requirementsItemLore = new Item.ItemBuilder(Material.IRON_AXE).displayName(Chat.normalColorize("&bDungeon Requirements Item Lore")).lore(config.contains("requirements.item_lore") ? Chat.normalColorize(config.getStringList("requirements.item_lore")) : Chat.normalColorize(Collections.singletonList("&7"))).build();
        setItem(new ItemSection(11, requirementsItemLore, "dungeon_requirements_item_lore", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_requirements_item_lore");
                } else
                    Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_requirements_item_lore");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "list_string");
                } else Editor.editorChatType.put(clicker, "list_string");
                clicker.closeInventory();
                new ListStringEditor(p, dungeon, "requirements.item_lore").openInventory(p);
            }
        }));
        Item commandJoin = new Item.ItemBuilder(Material.ENCHANTED_BOOK).displayName(Chat.normalColorize("&bDungeon Command Join")).lore(config.contains("commands.join") ? Chat.normalColorize(config.getStringList("commands.join")) : Chat.normalColorize(Collections.singletonList("&7"))).build();
        setItem(new ItemSection(12, commandJoin, "dungeon_commands_join", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_commands_join");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_commands_join");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "list_string");
                } else Editor.editorChatType.put(clicker, "list_string");
                clicker.closeInventory();
                new ListStringEditor(p, dungeon, "commands.join").openInventory(p);
            }
        }));
        Item commandComplete = new Item.ItemBuilder(Material.ENCHANTED_BOOK).displayName(Chat.normalColorize("&bDungeon Command Complete")).lore(config.contains("commands.complete") ? Chat.normalColorize(config.getStringList("commands.complete")) : Chat.normalColorize(Collections.singletonList("&7"))).build();
        setItem(new ItemSection(13, commandComplete, "dungeon_commands_complete", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_commands_complete");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_commands_complete");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "chat");
                } else Editor.editorType.put(clicker, "chat");
                if (Editor.editorChatType.containsKey(clicker)) {
                    Editor.editorChatType.replace(clicker, "list_string");
                } else Editor.editorChatType.put(clicker, "list_string");
                clicker.closeInventory();
                new ListStringEditor(p, dungeon, "commands.complete").openInventory(p);
            }
        }));
        Item stage = new Item.ItemBuilder(Material.EMERALD).displayName(Chat.normalColorize("&bDungeon Stages")).lore(config.contains("stages") ? Chat.normalColorize("&7" + Objects.requireNonNull(config.getConfigurationSection("stages")).getKeys(false).size() + "&7 Stages") : Chat.normalColorize("&7")).build();
        setItem(new ItemSection(14, stage, "dungeon_stages", e -> {
            if (e.getWhoClicked() instanceof Player) {
                Player clicker = (Player) e.getWhoClicked();
                if (Editor.editorDungeonInfo.containsKey(clicker.getName() + "_" + dungeon)) {
                    Editor.editorDungeonInfo.replace(clicker.getName() + "_" + dungeon, "dungeon_stages");
                } else Editor.editorDungeonInfo.put(clicker.getName() + "_" + dungeon, "dungeon_stages");
                if (Editor.editorType.containsKey(clicker)) {
                    Editor.editorType.replace(clicker, "stage");
                } else Editor.editorType.put(clicker, "stage");
                clicker.closeInventory();
                new MainStagesEditor(dungeon).openInventory(p);
            }
        }));
    }
}
