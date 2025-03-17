package net.danh.dungeons.NMS;

import org.bukkit.Bukkit;

import java.util.Objects;

public class NMSVersion {


    private final int major;
    private final int minor;
    private final int revision;

    public NMSVersion() {
        String version = Bukkit.getServer().getBukkitVersion();
        version = version.split("-")[0];
        final String[] versionDetails = version.split("\\.");
        major = Integer.parseInt(versionDetails[0]); // Always probably going to be '1'.
        minor = Integer.parseInt(versionDetails[1]); // 16/18/7/8 etc. etc.
        revision = versionDetails.length == 3 ? Integer.parseInt(versionDetails[2]) : 0;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRevision() {
        return revision;
    }

    @Override
    public String toString() {
        return getMajor() + "." + getMinor() + "." + getRevision();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NMSVersion that = (NMSVersion) o;
        return major == that.major && minor == that.minor && revision == that.revision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, revision);
    }
}
