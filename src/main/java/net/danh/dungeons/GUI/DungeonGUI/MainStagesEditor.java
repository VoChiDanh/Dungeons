package net.danh.dungeons.GUI.DungeonGUI;

import net.danh.dungeons.Dungeons;
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

import java.util.Objects;

public class MainStagesEditor extends BasicGUI {
    public MainStagesEditor(String dungeon) {
        super(new GUI("Stage Editor - Dungeon " + dungeon));
        SimpleConfigurationManager.get().build("", true, "Dungeons/" + dungeon + ".yml");
        FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeon + ".yml");
        if (config.contains("stages")) {
            int stageList = Objects.requireNonNull(config.getConfigurationSection("stages")).getKeys(false).size();
            for (int i = 0; i <= stageList; i++) {
                if (i < stageList) {
                    Item stage = new Item.ItemBuilder(Material.PAPER)
                            .displayName(Chat.normalColorize("&bDungeon Stages " + (i + 1)))
                            .lore(config.contains("stages.stage_" + (i + 1))
                                            ? Chat.normalColorize("&7Stage ID: " + config.getString("stages.stage_" + (i + 1) + ".id"))
                                            : Chat.normalColorize("&7Stage ID: null")
                                    , Chat.normalColorize("&7Left Click to edit"), Chat.normalColorize("&7Right Click to delete"))
                            .build();
                    int o = i + 1;
                    setItem(new ItemSection(i, stage, "dungeon_stages_" + i, e -> {
                        if (e.getWhoClicked() instanceof Player) {
                            Player clicker = (Player) e.getWhoClicked();
                            if (e.getClick().isLeftClick()) {
                                clicker.closeInventory();
                                if (config.contains("stages.stage_" + o)) {
                                    new StageEditor(dungeon,
                                            o,
                                            config.getString("stages.stage_" + o + ".id"))
                                            .openInventory(clicker);
                                }
                            } else if (e.getClick().isRightClick()) {
                                config.set("stages.stage_" + o, null);
                                for (int j = o; j < stageList; j++) {
                                    config.set("stages.stage_" + j, config.getConfigurationSection("stages.stage_" + (j + 1)));
                                }
                                config.set("stages.stage_" + stageList, null);
                                Editor.saveFiles(dungeon);
                                Editor.reloadFiles(dungeon);
                                clicker.closeInventory();
                                new MainEditor(clicker, dungeon).openInventory(clicker);
                            }
                        }
                    }));
                } else {
                    Item stage = new Item.ItemBuilder(Material.BOOK)
                            .displayName(Chat.normalColorize("&bCreate Dungeon Stages " + (i + 1)))
                            .lore(Chat.normalColorize("&7Left Click to create"))
                            .build();
                    int o = i + 1;
                    setItem(new ItemSection(i, stage, "dungeon_stages_" + i, e -> {
                        if (e.getWhoClicked() instanceof Player) {
                            Player clicker = (Player) e.getWhoClicked();
                            clicker.closeInventory();
                            Editor.editorStagePath.put(clicker.getName() + "_" + dungeon, "stages.stage_" + o + ".");
                            Editor.editorStageType.put(clicker.getName() + "_" + dungeon, "create_new_stage");
                            clicker.closeInventory();
                            Chat.sendMessage(clicker, Chat.normalColorize("&7Stages ID: " + Dungeons.stageBase.keySet().toString()
                                    .replace("[", "")
                                    .replace("]", "")));
                        }
                    }));
                }
            }
        } else {
            Item stage = new Item.ItemBuilder(Material.BOOK)
                    .displayName(Chat.normalColorize("&bCreate Dungeon Stages " + 1))
                    .lore(Chat.normalColorize("&7Left Click to create"))
                    .build();
            setItem(new ItemSection(0, stage, "dungeon_stages_" + 0, e -> {
                if (e.getWhoClicked() instanceof Player) {
                    Player clicker = (Player) e.getWhoClicked();
                    clicker.closeInventory();
                    Editor.editorStagePath.put(clicker.getName() + "_" + dungeon, "stages.stage_" + 1 + ".");
                    Editor.editorStageType.put(clicker.getName() + "_" + dungeon, "create_new_stage");
                    clicker.closeInventory();
                    Chat.sendMessage(clicker, Chat.normalColorize("&7Stages ID: " + Dungeons.stageBase.keySet().toString()
                            .replace("[", "")
                            .replace("]", "")));
                }
            }));
        }
    }
}
