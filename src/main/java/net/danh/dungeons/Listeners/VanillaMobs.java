package net.danh.dungeons.Listeners;

import net.danh.dungeons.Dungeon.DungeonManager;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Dungeons;
import net.danh.dungeons.Party.PartyManager;
import net.danh.dungeons.Resources.Number;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class VanillaMobs implements Listener {

    public static HashMap<String, Integer> kill = new HashMap<>();

    @EventHandler
    public void onEntityExplosion(@NotNull EntityExplodeEvent e) {
        Entity mob = e.getEntity();
        String vanillaMobs = e.getEntityType().toString();
        World world = mob.getWorld();
        for (Player killer : world.getPlayers()) {
            killer = PartyManager.getPlayer(killer);
            if (StageManager.inDungeon(killer)) {
                String dungeonID = StageManager.getPlayerDungeon(killer);
                DungeonManager dungeonManager = new DungeonManager(dungeonID);
                int stageNumber = StageManager.getStageNumber(killer);
                String stageID = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".id");
                if (stageID != null) {
                    if (stageID.equalsIgnoreCase("v_kill_mob")) {
                        String type = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".type");
                        if (type != null && type.equalsIgnoreCase(vanillaMobs)) {
                            int amount = Number.getInteger(dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".amount"));
                            if (kill.containsKey(killer.getName() + "_" + dungeonID)) {
                                if (amount > kill.get(killer.getName() + "_" + dungeonID)) {
                                    kill.replace(killer.getName() + "_" + dungeonID, kill.get(killer.getName() + "_" + dungeonID) + 1);
                                    if (amount <= kill.get(killer.getName() + "_" + dungeonID)) {
                                        kill.remove(killer.getName() + "_" + dungeonID);
                                        StageManager.nextStage(killer);
                                    }
                                }
                            } else {
                                kill.put(killer.getName() + "_" + dungeonID, 1);
                                if (amount <= kill.get(killer.getName() + "_" + dungeonID)) {
                                    kill.remove(killer.getName() + "_" + dungeonID);
                                    StageManager.nextStage(killer);
                                }
                            }
                        }
                    } else if (stageID.equalsIgnoreCase("mm_kill_mob")) {
                        if (Dungeons.getMythicAPI().isMythicMob(e.getEntity())) {
                            String type = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".type");
                            if (type != null && type.equalsIgnoreCase(Dungeons.getMythicAPI().getInternalName(e.getEntity()))) {
                                int amount = Number.getInteger(dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".amount"));
                                if (kill.containsKey(killer.getName() + "_" + dungeonID)) {
                                    if (amount > kill.get(killer.getName() + "_" + dungeonID)) {
                                        kill.replace(killer.getName() + "_" + dungeonID, kill.get(killer.getName() + "_" + dungeonID) + 1);
                                        if (amount <= kill.get(killer.getName() + "_" + dungeonID)) {
                                            kill.remove(killer.getName() + "_" + dungeonID);
                                            StageManager.nextStage(killer);
                                        }
                                    }
                                } else {
                                    kill.put(killer.getName() + "_" + dungeonID, 1);
                                    if (amount <= kill.get(killer.getName() + "_" + dungeonID)) {
                                        kill.remove(killer.getName() + "_" + dungeonID);
                                        StageManager.nextStage(killer);
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
    public void onVanillaKill(@NotNull EntityDeathEvent e) {
        LivingEntity mob = e.getEntity();
        Player p = mob.getKiller();
        String vanillaMobs = e.getEntityType().toString();
        if (p != null) {
            p = PartyManager.getPlayer(p);
            if (StageManager.inDungeon(p)) {
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
                    } else if (stageID.equalsIgnoreCase("mm_kill_mob")) {
                        if (Dungeons.getMythicAPI().isMythicMob(e.getEntity())) {
                            String type = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".type");
                            if (type != null && type.equalsIgnoreCase(Dungeons.getMythicAPI().getInternalName(e.getEntity()))) {
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
        } else {
            World world = mob.getWorld();
            for (Player killer : world.getPlayers()) {
                killer = PartyManager.getPlayer(killer);
                if (StageManager.inDungeon(killer)) {
                    String dungeonID = StageManager.getPlayerDungeon(killer);
                    DungeonManager dungeonManager = new DungeonManager(dungeonID);
                    int stageNumber = StageManager.getStageNumber(killer);
                    String stageID = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".id");
                    if (stageID != null) {
                        if (stageID.equalsIgnoreCase("v_kill_mob")) {
                            String type = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".type");
                            if (type != null && type.equalsIgnoreCase(vanillaMobs)) {
                                int amount = Number.getInteger(dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".amount"));
                                if (kill.containsKey(killer.getName() + "_" + dungeonID)) {
                                    if (amount > kill.get(killer.getName() + "_" + dungeonID)) {
                                        kill.replace(killer.getName() + "_" + dungeonID, kill.get(killer.getName() + "_" + dungeonID) + 1);
                                        if (amount <= kill.get(killer.getName() + "_" + dungeonID)) {
                                            kill.remove(killer.getName() + "_" + dungeonID);
                                            StageManager.nextStage(killer);
                                        }
                                    }
                                } else {
                                    kill.put(killer.getName() + "_" + dungeonID, 1);
                                    if (amount <= kill.get(killer.getName() + "_" + dungeonID)) {
                                        kill.remove(killer.getName() + "_" + dungeonID);
                                        StageManager.nextStage(killer);
                                    }
                                }
                            }
                        } else if (stageID.equalsIgnoreCase("mm_kill_mob")) {
                            if (Dungeons.getMythicAPI().isMythicMob(e.getEntity())) {
                                String type = dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".type");
                                if (type != null && type.equalsIgnoreCase(Dungeons.getMythicAPI().getInternalName(e.getEntity()))) {
                                    int amount = Number.getInteger(dungeonManager.getConfig().getString("stages.stage_" + stageNumber + ".amount"));
                                    if (kill.containsKey(killer.getName() + "_" + dungeonID)) {
                                        if (amount > kill.get(killer.getName() + "_" + dungeonID)) {
                                            kill.replace(killer.getName() + "_" + dungeonID, kill.get(killer.getName() + "_" + dungeonID) + 1);
                                            if (amount <= kill.get(killer.getName() + "_" + dungeonID)) {
                                                kill.remove(killer.getName() + "_" + dungeonID);
                                                StageManager.nextStage(killer);
                                            }
                                        }
                                    } else {
                                        kill.put(killer.getName() + "_" + dungeonID, 1);
                                        if (amount <= kill.get(killer.getName() + "_" + dungeonID)) {
                                            kill.remove(killer.getName() + "_" + dungeonID);
                                            StageManager.nextStage(killer);
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
