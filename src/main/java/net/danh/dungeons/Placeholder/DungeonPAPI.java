package net.danh.dungeons.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.danh.dungeons.API.DungeonsAPI;
import net.danh.dungeons.Dungeon.DungeonManager;
import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Dungeons;
import net.danh.dungeons.Party.PartyManager;
import net.danh.dungeons.Resources.Chat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DungeonPAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "dungeons";
    }

    @Override
    public @NotNull String getAuthor() {
        return Dungeons.getDungeonCore().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return Dungeons.getDungeonCore().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player p, @NotNull String args) {
        if (p == null) return null;
        if (args.equalsIgnoreCase("stage_display")) {
            if (StageManager.inDungeon(PartyManager.getPlayer(p))) {
                if (StageManager.getStageDisplay(PartyManager.getPlayer(p)) != null)
                    return StageManager.getStageDisplay(p);
                else return "";
            }
        }
        if (args.equalsIgnoreCase("in_dungeon")) {
            return String.valueOf(StageManager.inDungeon(PartyManager.getPlayer(p)));
        }
        if (args.equalsIgnoreCase("dungeon")) {
            if (StageManager.inDungeon(PartyManager.getPlayer(p))) {
                return new DungeonManager(StageManager.getPlayerDungeon(PartyManager.getPlayer(p))).getDisplayName();
            } else return "";
        }
        if (args.equalsIgnoreCase("lives")) {
            if (StageManager.inDungeon(PartyManager.getPlayer(p))) {
                return String.valueOf(DungeonsAPI.getLives(p));
            }
        }
        if (args.equalsIgnoreCase("party")) {
            if (PartyManager.inParty(PartyManager.getPlayer(p)))
                return Chat.normalColorize(PartyManager.getPartyDisplay(PartyManager.getPartyID(PartyManager.getPlayer(p))));
            else return "";
        }
        return null;
    }
}
