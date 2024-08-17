package net.danh.dungeons.Listeners;

import net.danh.dungeons.Dungeon.DungeonManager;
import net.danh.dungeons.Dungeon.StageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
        Player p = e.getPlayer();
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
                            int x = Integer.parseInt(location.split(";")[0]);
                            int y = Integer.parseInt(location.split(";")[1]);
                            int z = Integer.parseInt(location.split(";")[2]);
                            if (p.getWorld().equals(world) && e.getTo().distance(new Location(world, x, y, z))
                                    <= Integer.parseInt(Objects.requireNonNull(dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".distance")))) {
                                StageManager.checkPoints.replace(p.getName() + "_" + dungeonID, new Location(world, x, y, z));
                                StageManager.nextStage(p);
                            }
                        }
                    }
                }
            }
        }
    }
}
