package net.danh.dungeons.Listeners;

import net.danh.dungeons.Dungeon.DungeonManager;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Party.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReachLocation implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onMove(@NotNull PlayerMoveEvent e) {
        Player p = PartyManager.getPlayer(e.getPlayer());
        if (StageManager.inDungeon(p)) {
            String dungeonID = StageManager.getPlayerDungeon(p);
            DungeonManager dungeonManager = new DungeonManager(dungeonID);
            int stageNumber = StageManager.getStageNumber(p);
            String stageID = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".id");
            if (stageID != null) {
                if (stageID.equalsIgnoreCase("reach_location")) {
                    String location = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".location");
                    if (location != null) {
                        World world = Bukkit.getWorld(Objects.requireNonNull(dungeonManager.getConfig().getString("world")) + "_" + p.getName() + "_" + dungeonID);
                        if (world != null) {
                            if (p.getWorld().equals(world) && e.getTo().distance(StageManager.getLocation(location, world))
                                    <= Integer.parseInt(Objects.requireNonNull(dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".distance")))) {
                                StageManager.checkPoints.replace(p.getName() + "_" + dungeonID, StageManager.getLocation(location, world));
                                StageManager.nextStage(p);
                            }
                        }
                    }
                }
            }
        }
    }
}
