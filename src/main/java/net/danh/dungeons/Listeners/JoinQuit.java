package net.danh.dungeons.Listeners;

import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Party.PartyManager;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
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
            if (StageManager.inDungeon(p)) {
                if (!PartyManager.inParty(p))
                    StageManager.endDungeon(p, false, true);
                else {
                    String dungeonID = StageManager.getPlayerDungeon(p);
                    FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeonID + ".yml");
                    String locationComplete = config.getString("location.complete");
                    if (locationComplete != null) {
                        World world = Bukkit.getWorld(locationComplete.split(";")[0]);
                        Location rLocation = StageManager.getLocation(locationComplete.replace(locationComplete.split(";")[0] + ";", ""), world);
                        if (PartyManager.isPartyLeader(p)) {
                            PartyManager.getMembers(p).forEach(player -> player.teleport(rLocation));
                            StageManager.delete(config.getString("world") + "_" + p.getName() + "_" + dungeonID);
                            StageManager.endPartyDungeon(p, false, true);
                        }
                    }
                }
            }
        }
        if (PartyManager.inParty(p)) {
            if (PartyManager.isPartyLeader(p))
                PartyManager.disbandParty(p);
        }
    }
}
