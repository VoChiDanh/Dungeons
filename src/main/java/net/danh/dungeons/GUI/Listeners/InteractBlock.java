package net.danh.dungeons.GUI.Listeners;

import net.danh.dungeons.GUI.Editor;
import net.danh.dungeons.Resources.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class InteractBlock implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBreak(@NotNull BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (Editor.editorDungeon.containsKey(p)) {
            String dungeon = Editor.editorDungeon.get(p);
            if (Editor.editorDungeonInfo.containsKey(p.getName() + "_" + dungeon)) {
                if (Editor.editorDungeonInfo.get(p.getName() + "_" + dungeon).equalsIgnoreCase("dungeon_start_location")) {
                    if (Editor.editorDungeonByBreak.containsKey(p.getName() + "_" + dungeon)) {
                        Editor.editorDungeonByBreak.replace(p.getName() + "_" + dungeon, e.getBlock().getLocation().getX()
                                + ";" + e.getBlock().getLocation().getY() + ";" + e.getBlock().getLocation().getZ() + ";"
                                + e.getBlock().getLocation().getYaw() + ";" + e.getBlock().getLocation().getPitch());
                    } else
                        Editor.editorDungeonByBreak.put(p.getName() + "_" + dungeon, e.getBlock().getLocation().getX()
                                + ";" + e.getBlock().getLocation().getY() + ";" + e.getBlock().getLocation().getZ() + ";"
                                + e.getBlock().getLocation().getYaw() + ";" + e.getBlock().getLocation().getPitch());
                    Editor.editDungeon(p, dungeon);
                    e.setCancelled(true);
                } else if (Editor.editorDungeonInfo.get(p.getName() + "_" + dungeon).equalsIgnoreCase("dungeon_complete_location")) {
                    if (Editor.editorDungeonByBreak.containsKey(p.getName() + "_" + dungeon)) {
                        Editor.editorDungeonByBreak.replace(p.getName() + "_" + dungeon, p.getWorld().getName() + ";" + e.getBlock().getLocation().getX()
                                + ";" + e.getBlock().getLocation().getY() + ";" + e.getBlock().getLocation().getZ() + ";"
                                + e.getBlock().getLocation().getYaw() + ";" + e.getBlock().getLocation().getPitch());
                    } else
                        Editor.editorDungeonByBreak.put(p.getName() + "_" + dungeon, p.getWorld().getName() + ";" + e.getBlock().getLocation().getX()
                                + ";" + e.getBlock().getLocation().getY() + ";" + e.getBlock().getLocation().getZ() + ";"
                                + e.getBlock().getLocation().getYaw() + ";" + e.getBlock().getLocation().getPitch());
                    Chat.sendMessage(p, "&7" + Editor.editorDungeonByBreak.get(p.getName() + "_" + dungeon));
                    Editor.editDungeon(p, dungeon);
                    e.setCancelled(true);
                } else if (Editor.editorStageType.containsKey(p.getName() + "_" + dungeon)) {
                    String editType = Editor.editorStageType.get(p.getName() + "_" + dungeon);
                    if (editType.equalsIgnoreCase("break_block")) {
                        if (Editor.editorDungeonByBreak.containsKey(p.getName() + "_" + dungeon)) {
                            Editor.editorDungeonByBreak.replace(p.getName() + "_" + dungeon, e.getBlock().getLocation().getX()
                                    + ";" + e.getBlock().getLocation().getY() + ";" + e.getBlock().getLocation().getZ() + ";"
                                    + e.getBlock().getLocation().getYaw() + ";" + e.getBlock().getLocation().getPitch());
                        } else
                            Editor.editorDungeonByBreak.put(p.getName() + "_" + dungeon, e.getBlock().getLocation().getX()
                                    + ";" + e.getBlock().getLocation().getY() + ";" + e.getBlock().getLocation().getZ() + ";"
                                    + e.getBlock().getLocation().getYaw() + ";" + e.getBlock().getLocation().getPitch());
                        Chat.sendMessage(p, "&7" + Editor.editorDungeonByBreak.get(p.getName() + "_" + dungeon));
                        Editor.editDungeon(p, dungeon);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
