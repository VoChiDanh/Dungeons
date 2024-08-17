package net.danh.dungeons.GUI.Listeners;

import net.danh.dungeons.Dungeons;
import net.danh.dungeons.GUI.DungeonGUI.MainEditor;
import net.danh.dungeons.GUI.Editor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Chat implements Listener {

    public static HashMap<String, String> chatEdit = new HashMap<>();
    public static HashMap<String, Integer> chatNumberEdit = new HashMap<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChat(@NotNull PlayerChatEvent e) {
        Player p = e.getPlayer();
        if (Editor.editorDungeon.containsKey(p)) {
            String dungeon = Editor.editorDungeon.get(p);
            if (Editor.editorChatType.containsKey(p)) {
                if (Editor.editorChatType.get(p).equalsIgnoreCase("string")
                        || Editor.editorChatType.get(p).equalsIgnoreCase("list_string")) {
                    if (chatEdit.containsKey(p.getName() + "_" + dungeon)) {
                        chatEdit.replace(p.getName() + "_" + dungeon, e.getMessage());
                    } else chatEdit.put(p.getName() + "_" + dungeon, e.getMessage());
                } else if (Editor.editorChatType.get(p).equalsIgnoreCase("int")) {
                    if (chatNumberEdit.containsKey(p.getName() + "_" + dungeon)) {
                        chatNumberEdit.replace(p.getName() + "_" + dungeon, Integer.parseInt(e.getMessage()));
                    } else chatNumberEdit.put(p.getName() + "_" + dungeon, Integer.parseInt(e.getMessage()));
                }
                net.danh.dungeons.Resources.Chat.sendMessage(p, "&7" + e.getMessage());
                Editor.editDungeon(p, dungeon);
                e.setCancelled(true);
            }
            if (Editor.editorStageType.containsKey(p.getName() + "_" + dungeon)) {
                String editType = Editor.editorStageType.get(p.getName() + "_" + dungeon);
                if (editType.startsWith("chat")) {
                    String[] editTypeSplit = editType.split("_");
                    if (editTypeSplit[0].equalsIgnoreCase("chat")) {
                        if (editTypeSplit[1].equalsIgnoreCase("string")) {
                            if (chatEdit.containsKey(p.getName() + "_" + dungeon)) {
                                chatEdit.replace(p.getName() + "_" + dungeon, e.getMessage());
                            } else chatEdit.put(p.getName() + "_" + dungeon, e.getMessage());
                        } else if (editTypeSplit[1].equalsIgnoreCase("int")) {
                            if (chatNumberEdit.containsKey(p.getName() + "_" + dungeon)) {
                                chatNumberEdit.replace(p.getName() + "_" + dungeon, Integer.parseInt(e.getMessage()));
                            } else chatNumberEdit.put(p.getName() + "_" + dungeon, Integer.parseInt(e.getMessage()));
                        }
                        net.danh.dungeons.Resources.Chat.sendMessage(p, "&7" + e.getMessage());
                        Editor.editDungeon(p, dungeon);
                        e.setCancelled(true);
                    }
                } else if (editType.equalsIgnoreCase("create_new_stage")) {
                    if (Dungeons.stageBase.containsKey(e.getMessage())) {
                        if (chatEdit.containsKey(p.getName() + "_" + dungeon)) {
                            chatEdit.replace(p.getName() + "_" + dungeon, e.getMessage());
                        } else chatEdit.put(p.getName() + "_" + dungeon, e.getMessage());
                        net.danh.dungeons.Resources.Chat.sendMessage(p, "&7" + e.getMessage());
                        Editor.editDungeon(p, dungeon);
                        e.setCancelled(true);
                    } else {
                        Editor.editorType.remove(p);
                        Editor.editorDungeonByBreak.remove(p.getName() + "_" + dungeon);
                        Editor.editorDungeonInfo.remove(p.getName() + "_" + dungeon);
                        Editor.editorDungeon.remove(p);
                        Editor.editorChatType.remove(p);
                        Chat.chatEdit.remove(p.getName() + "_" + dungeon);
                        Chat.chatNumberEdit.remove(p.getName() + "_" + dungeon);
                        Editor.editorStageType.remove(p.getName() + "_" + dungeon);
                        Editor.editorStagePath.remove(p.getName() + "_" + dungeon);
                        new MainEditor(p, dungeon).openInventory(p);
                    }
                }
            }
        }
    }
}
