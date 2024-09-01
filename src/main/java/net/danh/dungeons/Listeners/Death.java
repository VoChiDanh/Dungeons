package net.danh.dungeons.Listeners;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import net.danh.dungeons.API.DungeonsAPI;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Dungeons;
import net.danh.dungeons.Party.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class Death implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onDeath(@NotNull PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (StageManager.inDungeon(p))
            Bukkit.getScheduler().scheduleSyncDelayedTask(Dungeons.getDungeonCore(), () -> p.spigot().respawn(), 0);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRespawn(@NotNull PlayerPostRespawnEvent e) {
        Player p = e.getPlayer();
        if (StageManager.inDungeon(PartyManager.getPlayer(p))) {
            if (DungeonsAPI.getLives(p) <= 1) {
                p.teleport(StageManager.checkPoints.get(p.getName() + "_" + DungeonsAPI.getDungeon(PartyManager.getPlayer(p))));
                p.setGameMode(GameMode.SPECTATOR);
                if (!PartyManager.inParty(p)) {
                    StageManager.endDungeon(p, false, true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE,
                            Integer.MAX_VALUE));
                } else {
                    if (PartyManager.isPartyLeader(p))
                        StageManager.endPartyDungeon(p, false, true);
                }
            } else {
                StageManager.lives.replace(p.getName() + "_" + DungeonsAPI.getDungeon(PartyManager.getPlayer(p)),
                        StageManager.lives.get(p.getName() + "_" + DungeonsAPI.getDungeon(PartyManager.getPlayer(p))) - 1);
                p.teleport(StageManager.checkPoints.get(p.getName() + "_" + DungeonsAPI.getDungeon(PartyManager.getPlayer(p))));
            }
        }
    }
}
