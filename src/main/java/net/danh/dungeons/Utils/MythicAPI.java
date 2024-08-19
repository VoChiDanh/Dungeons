package net.danh.dungeons.Utils;

import net.danh.dungeons.Dungeons;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class MythicAPI {

    private final List<String> mmPackageAPI = Arrays.asList("io.lumine.mythic.bukkit", "io.lumine.xikage.mythicmobs.api.bukkit", "io.lumine.xikage.mythicmobs.api.bukkit");
    public String packageName = "";
    private Object apiClass;

    public MythicAPI() {
        scanPackage();
    }

    private void scanPackage() {
        for (String packagee : this.mmPackageAPI) {
            try {
                String className = packagee + ".BukkitAPIHelper";
                this.apiClass = Class.forName(className).newInstance();
                this.packageName = packagee;
                Dungeons.getDungeonCore().getLogger().info("MythicMobs API Class: " + className);
                break;
            } catch (Exception exception) {
            }
        }
    }

    public Entity spawnMythicMob(String key, Location loc) {
        try {
            return (Entity) this.apiClass.getClass().getMethod("spawnMythicMob", String.class, Location.class).invoke(this.apiClass, new Object[]{key, loc});
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isMythicMob(Entity entity) {
        try {
            return (Boolean) this.apiClass.getClass().getMethod("isMythicMob", Entity.class).invoke(this.apiClass, new Object[]{entity});
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getDisplayName(Entity entity) {
        try {
            Object activeInstance = this.apiClass.getClass().getMethod("getMythicMobInstance", Entity.class).invoke(this.apiClass, entity);
            Object mmInstance = activeInstance.getClass().getMethod("getType").invoke(activeInstance);
            Object displayNamePlaceholder = mmInstance.getClass().getMethod("getDisplayName").invoke(mmInstance);
            if (displayNamePlaceholder instanceof String) return (String) displayNamePlaceholder;
            Object displayName = displayNamePlaceholder.getClass().getMethod("get").invoke(displayNamePlaceholder);
            return (displayName == null) ? null : (String) displayName;
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDisplayName(String key) {
        try {
            Object mmInstance = this.apiClass.getClass().getMethod("getMythicMob", String.class).invoke(this.apiClass, key);
            Object displayNamePlaceholder = mmInstance.getClass().getMethod("getDisplayName").invoke(mmInstance);
            if (displayNamePlaceholder instanceof String) return (String) displayNamePlaceholder;
            Object displayName = displayNamePlaceholder.getClass().getMethod("get").invoke(displayNamePlaceholder);
            return (displayName == null) ? null : (String) displayName;
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (Exception exception) {
        }
        return key;
    }

    public String getInternalName(Entity entity) {
        try {
            Object activeInstance = this.apiClass.getClass().getMethod("getMythicMobInstance", Entity.class).invoke(this.apiClass, entity);
            Object mmInstance = activeInstance.getClass().getMethod("getType").invoke(activeInstance);
            Object internalName = mmInstance.getClass().getMethod("getInternalName").invoke(mmInstance);
            return (String) internalName;
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
