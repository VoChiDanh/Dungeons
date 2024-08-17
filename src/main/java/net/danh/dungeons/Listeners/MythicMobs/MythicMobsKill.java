package net.danh.dungeons.Listeners.MythicMobs;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.danh.dungeons.Dungeon.DungeonManager;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Resources.Number;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MythicMobsKill implements Listener {

    public static HashMap<String, Integer> kill_mythic = new HashMap<>();

    @EventHandler
    public void onMythicKill(@NotNull MythicMobDeathEvent e) {
        if (e.getKiller() instanceof Player p) {
            if (StageManager.inDungeon(p)) {
                String dungeonID = StageManager.getPlayerDungeon(p);
                DungeonManager dungeonManager = new DungeonManager(dungeonID);
                int stageNumber = StageManager.getStageNumber(p);
                String stageID = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".id");
                if (stageID != null) {
                    if (stageID.equalsIgnoreCase("mm_kill_mob")) {
                        String type = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".type");
                        if (type != null && type.equalsIgnoreCase(e.getMobType().getInternalName())) {
                            int amount = Number.getInteger(dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".amount"));
                            if (kill_mythic.containsKey(p.getName() + "_" + dungeonID)) {
                                if (amount > kill_mythic.get(p.getName() + "_" + dungeonID)) {
                                    kill_mythic.replace(p.getName() + "_" + dungeonID, kill_mythic.get(p.getName() + "_" + dungeonID) + 1);
                                    if (amount <= kill_mythic.get(p.getName() + "_" + dungeonID)) {
                                        kill_mythic.remove(p.getName() + "_" + dungeonID);
                                        StageManager.nextStage(p);
                                    }
                                }
                            } else {
                                kill_mythic.put(p.getName() + "_" + dungeonID, 1);
                                if (amount <= kill_mythic.get(p.getName() + "_" + dungeonID)) {
                                    kill_mythic.remove(p.getName() + "_" + dungeonID);
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
