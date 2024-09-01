package net.danh.dungeons.Listeners;

import net.danh.dungeons.Dungeon.DungeonManager;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Party.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BlockBreak implements Listener {

    public static HashMap<String, Integer> block_data = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onShoot(@NotNull ProjectileHitEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player p = (Player) e.getEntity().getShooter();
            if (StageManager.inDungeon(p)) {
                p = PartyManager.getPlayer(p);
                String dungeonID = StageManager.getPlayerDungeon(p);
                DungeonManager dungeonManager = new DungeonManager(dungeonID);
                int stageNumber = StageManager.getStageNumber(p);
                String stageID = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".id");
                if (stageID != null) {
                    if (stageID.equalsIgnoreCase("shoot_block")) {
                        String location = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".location");
                        if (location != null) {
                            World world = Bukkit.getWorld(Objects.requireNonNull(dungeonManager.getConfig().getString("world")) + "_" + p.getName() + "_" + dungeonID);
                            if (world != null) {
                                int distance = dungeonManager.getConfig().getInt("stages.stage_" + stageNumber + ".distance");
                                int amount = dungeonManager.getConfig().getInt("stages.stage_" + stageNumber + ".amount");
                                Location shootLocation = StageManager.getLocation(location, world);
                                Block block = e.getHitBlock();
                                if (block != null) {
                                    if (block.getLocation().distanceSquared(shootLocation) <= distance) {
                                        if (block_data.containsKey(p.getName() + "_" + dungeonID)) {
                                            if (amount > block_data.get(p.getName() + "_" + dungeonID)) {
                                                block_data.replace(p.getName() + "_" + dungeonID, block_data.get(p.getName() + "_" + dungeonID) + 1);
                                                if (amount <= block_data.get(p.getName() + "_" + dungeonID)) {
                                                    block_data.remove(p.getName() + "_" + dungeonID);
                                                    StageManager.nextStage(p);
                                                }
                                            }
                                        } else {
                                            block_data.put(p.getName() + "_" + dungeonID, 1);
                                            if (amount <= block_data.get(p.getName() + "_" + dungeonID)) {
                                                block_data.remove(p.getName() + "_" + dungeonID);
                                                StageManager.nextStage(p);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(@NotNull BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (StageManager.inDungeon(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(@NotNull BlockBreakEvent e) {
        Player p = PartyManager.getPlayer(e.getPlayer());
        if (StageManager.inDungeon(p)) {
            String dungeonID = StageManager.getPlayerDungeon(p);
            DungeonManager dungeonManager = new DungeonManager(dungeonID);
            int stageNumber = StageManager.getStageNumber(p);
            String stageID = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".id");
            if (stageID != null) {
                if (stageID.equalsIgnoreCase("break_wall")) {
                    String location = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".location");
                    if (location != null) {
                        World world = Bukkit.getWorld(Objects.requireNonNull(dungeonManager.getConfig().getString("world")) + "_" + p.getName() + "_" + dungeonID);
                        if (world != null) {
                            int width = dungeonManager.getConfig().getInt("stages.stage_" + stageNumber + ".width");
                            int depth = dungeonManager.getConfig().getInt("stages.stage_" + stageNumber + ".depth");
                            Location breakLocation = StageManager.getLocation(location, world);
                            if (e.getBlock().getLocation().distanceSquared(breakLocation) <= 3) {
                                if (breakLocation.getBlock().getType().equals(e.getBlock().getType())) {
                                    BreakDirection direction = BreakDirection.getFacingDirection(p);
                                    List<Location> blocks = direction.getRadius(e.getBlock(), depth, width);
                                    for (Location b_lo : blocks)
                                        b_lo.getBlock().setType(Material.AIR);
                                    StageManager.nextStage(p);
                                }
                            }
                        }
                    }
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(@NotNull EntityChangeBlockEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
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

    public enum BreakDirection {
        NORTH(0, 0, -1),
        SOUTH(0, 0, 1),
        EAST(1, 0, 0),
        WEST(-1, 0, 0),
        UP(0, 1, 0),
        DOWN(0, -1, 0);
        private Vector addition;
        private boolean isX;
        private boolean isY;
        private boolean isZ;

        BreakDirection(int x, int y, int z) {
            this.addition = new Vector(x, y, z);
            this.isX = x != 0;
            this.isY = y != 0;
            this.isZ = z != 0;
        }

        public static BreakDirection getFacingDirection(@NotNull Player player) {
            Vector dir = player.getLocation().getDirection().normalize();
            double x = dir.getX();
            double y = dir.getY();
            double z = dir.getZ();
            // up or down
            if (Math.abs(y) > 0.5D)
                return (y > 0.0D) ? BreakDirection.UP : BreakDirection.DOWN;
                //east or west
            else if (Math.abs(x) > 0.5D)
                return (x > 0.0D) ? BreakDirection.EAST : BreakDirection.WEST;
                //north or south
            else
                return (z > 0.0D) ? BreakDirection.SOUTH : BreakDirection.NORTH;
        }

        public @NotNull List<Location> getRadius(@NotNull Block start, int depth, int width) {
            width = Math.abs(width);
            int evenBlocks = (width % 2 == 1) ? 0 : 1;
            int radius = (int) ((evenBlocks != 1) ? Math.floor((width - 1) / 2D) : Math.floor(width / 2D));
            List<Location> result = new ArrayList<>();
            result.add(start.getLocation().clone());
            Location location = start.getLocation();
            for (int i = 0; i < depth; i++) {
                for (int x = -radius; x <= radius - evenBlocks; x++)
                    for (int y = -radius; y <= radius - evenBlocks; y++)
                        for (int z = -radius; z <= radius - evenBlocks; z++) {
                            Location toadd = null;
                            if (isX)
                                toadd = location.clone().add(0, y, z);
                            if (isY)
                                toadd = location.clone().add(x, 0, z);
                            if (isZ)
                                toadd = location.clone().add(x, y, 0);
                            if (toadd != null && !result.contains(toadd))
                                result.add(toadd);
                        }
                location = location.add(addition);
            }
            return result;
        }
    }
}
