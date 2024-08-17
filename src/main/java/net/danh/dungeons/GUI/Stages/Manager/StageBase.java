package net.danh.dungeons.GUI.Stages.Manager;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StageBase {
    String id;
    String data;
    String preData;

    public StageBase(@NotNull String stageID, @NotNull String stageData, @Nullable String stagePreData) {
        id = stageID;
        data = stageData;
        preData = stagePreData;
    }

    @NotNull
    public String getID() {
        return id;
    }

    @NotNull
    public String getData() {
        return data;
    }

    @Nullable
    public String getPreData() {
        return preData;
    }

    @NotNull
    public String getValueData(@NotNull String path) {
        return path;
    }

    @Nullable
    public String getEditType(@NotNull String data) {
        return data;
    }

    @Nullable
    public String getSuggestEdit(@NotNull String path) {
        return "Suggest sth";
    }

    @NotNull
    public String getDisplay(Player p) {
        return "Display";
    }

    public void activePreStage(Player p) {

    }
}
