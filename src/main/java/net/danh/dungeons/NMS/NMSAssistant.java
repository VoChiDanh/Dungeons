package net.danh.dungeons.NMS;

public class NMSAssistant {
    public NMSVersion getNMSVersion() {
        return new NMSVersion();
    }

    public boolean isVersionGreaterThan(int major, int minor, int revision) {
        if (getNMSVersion().getMajor() > major)
            return true;
        else if (getNMSVersion().getMinor() > minor)
            return true;
        else return getNMSVersion().getRevision() > revision;
    }


    public boolean isVersionGreaterThanOrEqualTo(int major, int minor, int revision) {
        if (getNMSVersion().getMajor() >= major)
            return true;
        else if (getNMSVersion().getMinor() >= minor)
            return true;
        else return getNMSVersion().getRevision() >= revision;
    }

    public boolean isVersionLessThan(int major, int minor, int revision) {
        if (getNMSVersion().getMajor() < major)
            return true;
        else if (getNMSVersion().getMinor() < minor)
            return true;
        else return getNMSVersion().getRevision() < revision;
    }

    public boolean isVersionLessThanOrEqualTo(int major, int minor, int revision) {
        if (getNMSVersion().getMajor() <= major)
            return true;
        else if (getNMSVersion().getMinor() <= minor)
            return true;
        else return getNMSVersion().getRevision() <= revision;
    }

    public boolean isVersion(int major, int minor, int revision) {
        return getNMSVersion().getMajor() == major && getNMSVersion().getMinor() == minor && getNMSVersion().getRevision() == revision;
    }

    public boolean isNotVersion(int major, int minor, int revision) {
        return getNMSVersion().getMajor() != major && getNMSVersion().getMinor() != minor && getNMSVersion().getRevision() != revision;
    }
}