package net.danh.dungeons.GUI.Stages.Manager;

import net.danh.dungeons.Dungeons;
import net.danh.dungeons.GUI.Stages.MythicKillMobs;
import net.danh.dungeons.GUI.Stages.ReachLocation;
import net.danh.dungeons.GUI.Stages.VanillaKillMobs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StageRegistry {
    private static final List<Class<? extends StageBase>> stage = new ArrayList<>();

    static {
        stage.add(ReachLocation.class);
        stage.add(VanillaKillMobs.class);
        if (Dungeons.isIsMythicMobsInstalled())
            stage.add(MythicKillMobs.class);
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull List<Class<? extends StageBase>> getStage() {
        return new ArrayList<>(stage);
    }

    public static void addStage(Class<? extends StageBase> new_stage) {
        stage.add(new_stage);
    }
}
