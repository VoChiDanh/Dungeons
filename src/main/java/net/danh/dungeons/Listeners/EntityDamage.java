package net.danh.dungeons.Listeners;

import net.danh.dungeons.Party.PartyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class EntityDamage implements Listener {

    @EventHandler
    public void onPartyPvP(@NotNull EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player
                && e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            Player p = (Player) e.getEntity();
            if (PartyManager.inParty(p) && PartyManager.inParty(p)) {
                String attackerParty = PartyManager.getPartyID(attacker);
                String playerParty = PartyManager.getPartyID(p);
                if (attackerParty != null && playerParty != null)
                    if (attackerParty.equalsIgnoreCase(playerParty))
                        e.setCancelled(true);
            }
        }
    }
}
