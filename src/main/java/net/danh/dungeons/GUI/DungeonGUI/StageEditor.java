package net.danh.dungeons.GUI.DungeonGUI;

import net.danh.dungeons.Dungeons;
import net.danh.dungeons.GUI.Editor;
import net.danh.dungeons.GUI.Stages.Manager.StageBase;
import net.danh.dungeons.Resources.Chat;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.browsit.milkgui.gui.GUI;
import org.browsit.milkgui.gui.type.BasicGUI;
import org.browsit.milkgui.item.Item;
import org.browsit.milkgui.item.ItemSection;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class StageEditor extends BasicGUI {
    public StageEditor(String dungeon, int stageNumber, String stage) {
        super(new GUI("Stage Editor - Stage ID: " + stage));
        if (Dungeons.stageBase.containsKey(stage)) {
            SimpleConfigurationManager.get().build("", true, "Dungeons/" + dungeon + ".yml");
            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeon + ".yml");
            StageBase stageBase = Dungeons.stageBase.get(stage);
            String[] stageDataSplit = stageBase.getData().split(";");
            for (int i = 0; i < stageDataSplit.length; i++) {
                Item item = new Item.ItemBuilder(Material.BOOK)
                        .displayName(Chat.normalColorize("&bDungeon Stages " + stage))
                        .lore(config.contains("stages.stage_" + stageNumber + "." + stageDataSplit[i])
                                ? Chat.normalColorize("&7" + stageDataSplit[i] + ": " + config.getString("stages.stage_" + stageNumber + "." + stageDataSplit[i]))
                                : Chat.normalColorize("&7"))
                        .build();
                int o = i;
                setItem(new ItemSection(i, item, "dungeon_stages_" + stageNumber + "_" + stage, e -> {
                    if (e.getWhoClicked() instanceof Player clicker) {
                        Editor.editorStagePath.put(clicker.getName() + "_" + dungeon, "stages.stage_" + stageNumber + "." + stageDataSplit[o]);
                        Editor.editorStageType.put(clicker.getName() + "_" + dungeon, stageBase.getEditType(stageDataSplit[o]));
                        clicker.closeInventory();
                        Chat.sendMessage(clicker, stageBase.getSuggestEdit(stageDataSplit[o]));
                    }
                }));
            }
            if (stageBase.getPreData() != null) {
                String[] stagePreDataSplit = stageBase.getPreData().split(";");
                for (int i = stageDataSplit.length; i < stageDataSplit.length + stagePreDataSplit.length; i++) {
                    Item item = new Item.ItemBuilder(Material.PAPER)
                            .displayName(Chat.normalColorize("&bDungeon Pre Stages " + stage))
                            .lore(config.contains("stages.stage_" + stageNumber + ".pre_stage." + stagePreDataSplit[i - stageDataSplit.length])
                                    ? Chat.normalColorize("&7" + stagePreDataSplit[i - stageDataSplit.length] + "&7: " + config.getString("stages.stage_" + stageNumber + ".pre_stage." + stagePreDataSplit[i - stageDataSplit.length]))
                                    : Chat.normalColorize("&7"))
                            .build();
                    int o = i - stageDataSplit.length;
                    setItem(new ItemSection(i, item, "dungeon_stages_" + stageNumber + "_pre_stage_" + stage, e -> {
                        if (e.getWhoClicked() instanceof Player clicker) {
                            Editor.editorStagePath.put(clicker.getName() + "_" + dungeon, "stages.stage_" + stageNumber + ".pre_stage." + stagePreDataSplit[o]);
                            Editor.editorStageType.put(clicker.getName() + "_" + dungeon, stageBase.getEditType(stagePreDataSplit[o]));
                            clicker.closeInventory();
                            Chat.sendMessage(clicker, stageBase.getSuggestEdit(stagePreDataSplit[o]));
                        }
                    }));
                }
            }
        }
    }
}
