package net.danh.dungeons.Listeners;

import net.danh.dungeons.Dungeon.StageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class JoinQuit implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onQuit(@NotNull PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (StageManager.inDungeon(p)) {
            StageManager.endDungeon(p, false, true);
        }
    }
}