package net.danh.dungeons.Listeners;

import net.danh.dungeons.Dungeon.StageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.jetbrains.annotations.NotNull;

public class BlockBreak implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBreak(@NotNull BlockBreakEvent e) {
        if (StageManager.inDungeon(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(@NotNull EntityChangeBlockEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (StageManager.inDungeon(p))
                e.setCancelled(true);
        } else {
            if (!e.getEntity().getLocation().getNearbyPlayers(20).isEmpty()) {
                for (Player p : e.getEntity().getLocation().getNearbyPlayers(20)) {
                    if (StageManager.inDungeon(p))
                        e.setCancelled(true);
                }
            }
        }
    }
}
