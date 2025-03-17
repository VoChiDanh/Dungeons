package net.danh.dungeons.WorldManager;

public interface WManagerInterface {

    boolean cloneWorld(String oldName, String newName);

    boolean deleteWorld(String name, boolean deleteWorldFolder);

    boolean doLoad(String name);

    boolean unloadWorld(String name);

    void removePlayersFromWorld(String name);
}

