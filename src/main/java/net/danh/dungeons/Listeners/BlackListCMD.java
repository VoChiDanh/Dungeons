package net.danh.dungeons.Listeners;

import net.danh.dungeons.Resources.Files;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public class BlackListCMD implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onCMDExecute(@NotNull PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String[] args = e.getMessage().replace("/", "").split(" ");
        if (Files.getConfig().getStringList("dungeon.black_list_command").contains(args[0])) {
            if (!p.hasPermission("dungeons.admin"))
                e.setCancelled(true);
        }
    }
}
