package net.danh.dungeons.API;

import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Dungeons;
import net.danh.dungeons.GUI.Editor;
import net.danh.dungeons.GUI.Stages.Manager.StageBase;
import net.danh.dungeons.GUI.Stages.Manager.StageRegistry;
import net.danh.dungeons.Party.PartyManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DungeonsAPI {

    public static @Nullable String getDungeon(Player p) {
        if (StageManager.inDungeon(p)) {
            return StageManager.getPlayerDungeon(p);
        }
        return null;
    }

    public static int getDungeonStage(Player p) {
        if (StageManager.inDungeon(p) && getDungeon(p) != null) {
            return StageManager.stage.get(p.getName() + "_" + getDungeon(p));
        }
        return -1;
    }

    public static void addPreStage(String preStageID) {
        Editor.preStageList.add(preStageID);
    }

    public static void addPreStage(String @NotNull ... preStageID) {
        for (String preStage : preStageID)
            addPreStage(preStage);
    }

    public static void registerStage(Class<? extends StageBase> stage) {
        StageRegistry.addStage(stage);
    }

    @SafeVarargs
    public static void registerStage(Class<? extends StageBase> @NotNull ... stages) {
        for (Class<? extends StageBase> stage : stages)
            registerStage(stage);
    }

    public static StageBase getStage(String stageID) {
        return Dungeons.stageBase.get(stageID);
    }

    public static int getLives(Player p) {
        if (StageManager.inDungeon(PartyManager.getPlayer(p)) && getDungeon(PartyManager.getPlayer(p)) != null) {
            return StageManager.lives.get(p.getName() + "_" + getDungeon(PartyManager.getPlayer(p)));
        }
        return -1;
    }
}
