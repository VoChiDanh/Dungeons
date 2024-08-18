package net.danh.dungeons.Utils;

import net.danh.dungeons.Dungeons;
import net.danh.dungeons.Resources.Files;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

public class UpdateChecker implements Listener {

    private final int RESOURCE_ID = 118953;
    private final Dungeons plugin;
    private final String pluginVersion;
    private String spigotVersion;
    private boolean updateAvailable;
    private boolean devBuildVersion;

    public UpdateChecker(@NotNull Dungeons dungeons) {
        plugin = dungeons;
        pluginVersion = dungeons.getDescription().getVersion();
    }

    public String getSpigotVersion() {
        return spigotVersion;
    }

    public void fetch() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (Files.getConfig().getBoolean("check_update")) {
                try {
                    HttpsURLConnection con = (HttpsURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID).openConnection();
                    con.setRequestMethod("GET");
                    spigotVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
                } catch (Exception ex) {
                    plugin.getLogger().info("Failed to check for updates on spigot.");
                    return;
                }

                if (spigotVersion == null || spigotVersion.isEmpty()) {
                    return;
                }

                updateAvailable = spigotIsNewer();
                devBuildVersion = devBuildIsNewer();

                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (devBuildVersion) {
                        plugin.getLogger().warning("You are using DevBuild version of Dungeons Plugin");
                        plugin.getLogger().warning("Most of things in DevBuild has fix bug and new features for the next version and it can be include another issues");
                        plugin.getLogger().warning("So if you have any issues, please go to my Discord and report it to Danh!");
                        plugin.getLogger().warning("Discord: https://discord.gg/r5ejaPSjku");
                    }
                    if (updateAvailable) {
                        plugin.getLogger().warning("An update for Dungeons (v" + getSpigotVersion() + ") is available at:");
                        plugin.getLogger().warning("SpigotMC: https://www.spigotmc.org/resources/" + RESOURCE_ID + "/");
                        plugin.getLogger().warning("Hangar: https://hangar.papermc.io/VoChiDanh/Dungeons");
                        plugin.getLogger().warning("You are using version v" + pluginVersion);
                        plugin.getLogger().warning("If your plugin version higher than spigotmc version, you can ignore this notice");
                        Bukkit.getPluginManager().registerEvents(this, plugin);
                    } else {
                        plugin.getLogger().info("This is the latest version of Dungeons Plugin");
                    }
                });
            }
        });
    }

    private boolean spigotIsNewer() {
        if (spigotVersion == null || spigotVersion.isEmpty() || !spigotVersion.matches("[0-9].[0-9]")) {
            return false;
        }

        int[] plV = toReadable(pluginVersion);
        int[] spV = toReadable(spigotVersion);

        if (plV == null || spV == null) return false;

        if (plV[0] < spV[0]) {
            return true;
        }
        return plV[1] < spV[1];
    }

    private boolean devBuildIsNewer() {
        if (spigotVersion == null || spigotVersion.isEmpty() || !spigotVersion.matches("[0-9].[0-9]")) {
            return false;
        }

        int[] plV = toReadable(pluginVersion);
        int[] spV = toReadable(spigotVersion);

        if (plV == null || spV == null) return false;

        if (plV[0] > spV[0]) {
            return true;
        }
        return plV[1] > spV[1];
    }

    private int[] toReadable(@NotNull String version) {
        if (version.endsWith("-SNAPSHOT")) {
            version = version.split("-SNAPSHOT")[0];
        }
        return Arrays.stream(version.split("\\.")).mapToInt(Integer::parseInt).toArray();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(@NotNull PlayerJoinEvent e) {
        if (updateAvailable) {
            if (e.getPlayer().hasPermission("dungeons.admin")) {
                Player player = e.getPlayer();
                if (spigotIsNewer()) {
                    player.sendMessage(ChatColor.GREEN + "An update is available for Dungeons at");
                    player.sendMessage("SpigotMC: https://www.spigotmc.org/resources/" + RESOURCE_ID + "/");
                    player.sendMessage("Hangar: https://hangar.papermc.io/VoChiDanh/Dungeons");
                    player.sendMessage(ChatColor.GREEN + String.format("You are using version %s", pluginVersion));
                }
            }
        }
    }
}