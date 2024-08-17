package net.danh.dungeons.Listeners;

import net.danh.dungeons.Dungeon.DungeonManager;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Resources.Number;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class VanillaMobs implements Listener {

    public static HashMap<String, Integer> kill = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onVanillaKill(@NotNull EntityDeathEvent e) {
        LivingEntity mob = e.getEntity();
        Player p = mob.getKiller();
        String vanillaMobs = e.getEntityType().toString();
        if (p != null && StageManager.inDungeon(p)) {
            String dungeonID = StageManager.getPlayerDungeon(p);
            DungeonManager dungeonManager = new DungeonManager(dungeonID);
            int stageNumber = StageManager.getStageNumber(p);
            String stageID = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".id");
            if (stageID != null) {
                if (stageID.equalsIgnoreCase("v_kill_mob")) {
                    String type = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".type");
                    if (type != null && type.equalsIgnoreCase(vanillaMobs)) {
                        int amount = Number.getInteger(dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".amount"));
                        if (kill.containsKey(p.getName() + "_" + dungeonID)) {
                            if (amount > kill.get(p.getName() + "_" + dungeonID)) {
                                kill.replace(p.getName() + "_" + dungeonID, kill.get(p.getName() + "_" + dungeonID) + 1);
                                if (amount <= kill.get(p.getName() + "_" + dungeonID)) {
                                    kill.remove(p.getName() + "_" + dungeonID);
                                    StageManager.nextStage(p);
                                }
                            }
                        } else {
                            kill.put(p.getName() + "_" + dungeonID, 1);
                            if (amount <= kill.get(p.getName() + "_" + dungeonID)) {
                                kill.remove(p.getName() + "_" + dungeonID);
                                StageManager.nextStage(p);
                            }
                        }
                    }
                }
            }
        }
    }
}
