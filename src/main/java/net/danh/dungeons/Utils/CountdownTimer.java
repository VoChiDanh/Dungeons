package net.danh.dungeons.Utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class CountdownTimer implements Runnable {

    private final JavaPlugin plugin;
    private final int seconds;
    private final Consumer<CountdownTimer> everySecond;
    private Integer assignedTaskId;
    private int secondsLeft;
    private boolean beforeTimerExecuted = false;
    private Runnable beforeTimer;
    private Runnable afterTimer;

    public CountdownTimer(
            JavaPlugin plugin,
            int seconds,
            Runnable beforeTimer,
            Consumer<CountdownTimer> everySecond,
            Runnable afterTimer
    ) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.secondsLeft = seconds;
        this.beforeTimer = beforeTimer;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    public CountdownTimer(
            JavaPlugin plugin,
            int seconds,
            Consumer<CountdownTimer> everySecond,
            Runnable afterTimer
    ) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.secondsLeft = seconds;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    public CountdownTimer(
            JavaPlugin plugin,
            int seconds,
            Runnable beforeTimer,
            Consumer<CountdownTimer> everySecond
    ) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.secondsLeft = seconds;
        this.beforeTimer = beforeTimer;
        this.everySecond = everySecond;
    }

    public CountdownTimer(
            JavaPlugin plugin,
            int seconds,
            Consumer<CountdownTimer> everySecond
    ) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.secondsLeft = seconds;
        this.everySecond = everySecond;
    }

    @Override
    public void run() {
        if (secondsLeft < 1) {
            if (afterTimer != null) {
                afterTimer.run();
            }
            if (assignedTaskId != null) {
                cancelCountdown();
            }
            return;
        }

        if (!beforeTimerExecuted && secondsLeft == seconds && beforeTimer != null) {
            beforeTimer.run();
            beforeTimerExecuted = true;
        }

        everySecond.accept(this);

        secondsLeft--;
    }

    public int getTotalSeconds() {
        return seconds;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public Integer getAssignedTaskId() {
        return assignedTaskId;
    }

    public void cancelCountdown() {
        Bukkit.getScheduler().cancelTask(assignedTaskId);
    }

    public void scheduleTimer() {
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
    }

}

