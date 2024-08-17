package net.danh.dungeons.GUI;

import net.danh.dungeons.Dungeons;
import net.danh.dungeons.GUI.DungeonGUI.MainEditor;
import net.danh.dungeons.GUI.Listeners.Chat;
import net.danh.dungeons.GUI.Stages.Manager.StageBase;
import net.xconfig.bukkit.model.SimpleConfigurationManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Editor {

    public static List<String> preStageList = new ArrayList<>();

    public static HashMap<Player, String> editorDungeon = new HashMap<>();
    public static HashMap<Player, String> editorChatType = new HashMap<>();
    public static HashMap<Player, String> editorType = new HashMap<>();
    public static HashMap<String, String> editorDungeonInfo = new HashMap<>();
    public static HashMap<String, String> editorDungeonByBreak = new HashMap<>();

    public static HashMap<String, String> editorStagePath = new HashMap<>();
    public static HashMap<String, String> editorStageType = new HashMap<>();

    @Contract(pure = true)
    public static void editDungeon(@NotNull Player p, @NotNull String dungeon) {
        if (editorDungeonInfo.containsKey(p.getName() + "_" + dungeon) && editorDungeon.get(p).equalsIgnoreCase(dungeon)) {
            String task = editorDungeonInfo.get(p.getName() + "_" + dungeon);
            if (editorType.containsKey(p)) {
                if (editorType.get(p).equalsIgnoreCase("chat")) {
                    String chat = Chat.chatEdit.getOrDefault(p.getName() + "_" + dungeon, null);
                    int chatNumber = Chat.chatNumberEdit.getOrDefault(p.getName() + "_" + dungeon, 0);
                    if (task.equalsIgnoreCase("dungeon_display_name")) {
                        if (Editor.editorChatType.get(p).equalsIgnoreCase("string")) {
                            edit(p, "name", chat, dungeon);
                        }
                    }
                    if (task.equalsIgnoreCase("dungeon_world")) {
                        if (Editor.editorChatType.get(p).equalsIgnoreCase("string")) {
                            edit(p, "world", chat, dungeon);
                        }
                    }
                    if (task.equalsIgnoreCase("dungeon_requirements_info")) {
                        if (Editor.editorChatType.get(p).equalsIgnoreCase("list_string")) {
                            edit(p, "requirements.info", chat, dungeon);
                        }
                    }
                    if (task.equalsIgnoreCase("dungeon_requirements_info_lore")) {
                        if (Editor.editorChatType.get(p).equalsIgnoreCase("list_string")) {
                            edit(p, "requirements.info_lore", chat, dungeon);
                        }
                    }
                    if (task.equalsIgnoreCase("dungeon_requirements_item")) {
                        if (Editor.editorChatType.get(p).equalsIgnoreCase("list_string")) {
                            edit(p, "requirements.item", chat, dungeon);
                        }
                    }
                    if (task.equalsIgnoreCase("dungeon_requirements_item_lore")) {
                        if (Editor.editorChatType.get(p).equalsIgnoreCase("list_string")) {
                            edit(p, "requirements.item_lore", chat, dungeon);
                        }
                    }
                    if (task.equalsIgnoreCase("dungeon_commands_join")) {
                        if (Editor.editorChatType.get(p).equalsIgnoreCase("list_string")) {
                            edit(p, "commands.join", chat, dungeon);
                        }
                    }
                    if (task.equalsIgnoreCase("dungeon_commands_complete")) {
                        if (Editor.editorChatType.get(p).equalsIgnoreCase("list_string")) {
                            edit(p, "commands.complete", chat, dungeon);
                        }
                    }
                    if (task.equalsIgnoreCase("dungeon_start_times")) {
                        if (Editor.editorChatType.get(p).equalsIgnoreCase("int")) {
                            edit(p, "times.start", String.valueOf(chatNumber), dungeon);
                        }
                    }
                    if (task.equalsIgnoreCase("dungeon_lives")) {
                        if (Editor.editorChatType.get(p).equalsIgnoreCase("int")) {
                            edit(p, "lives", String.valueOf(chatNumber), dungeon);
                        }
                    }
                    if (task.equalsIgnoreCase("dungeon_complete_times")) {
                        if (Editor.editorChatType.get(p).equalsIgnoreCase("int")) {
                            edit(p, "times.complete", String.valueOf(chatNumber), dungeon);
                        }
                    }
                } else if (editorType.get(p).equalsIgnoreCase("break_block")) {
                    if (task.equalsIgnoreCase("dungeon_start_location")) {
                        String edit = editorDungeonByBreak.get(p.getName() + "_" + dungeon);
                        edit(p, "location.join", edit, dungeon);
                    }
                    if (task.equalsIgnoreCase("dungeon_complete_location")) {
                        String edit = editorDungeonByBreak.get(p.getName() + "_" + dungeon);
                        edit(p, "location.complete", edit, dungeon);
                    }
                } else if (editorType.get(p).equalsIgnoreCase("stage")) {
                    if (Editor.editorStageType.containsKey(p.getName() + "_" + dungeon)) {
                        String editType = Editor.editorStageType.get(p.getName() + "_" + dungeon);
                        if (editType.contains("chat")) {
                            String[] editTypeSplit = editType.split("_");
                            if (editTypeSplit[0].equalsIgnoreCase("chat")) {
                                String chat = Chat.chatEdit.getOrDefault(p.getName() + "_" + dungeon, null);
                                int chatNumber = Chat.chatNumberEdit.getOrDefault(p.getName() + "_" + dungeon, 0);
                                if (editTypeSplit[1].equalsIgnoreCase("string")) {
                                    edit(p, Editor.editorStagePath.get(p.getName() + "_" + dungeon), chat, dungeon);
                                } else if (editTypeSplit[1].equalsIgnoreCase("int")) {
                                    edit(p, Editor.editorStagePath.get(p.getName() + "_" + dungeon), String.valueOf(chatNumber), dungeon);
                                }
                            }
                        } else if (editType.equalsIgnoreCase("break_block")) {
                            String edit = editorDungeonByBreak.get(p.getName() + "_" + dungeon);
                            edit(p, Editor.editorStagePath.get(p.getName() + "_" + dungeon), edit, dungeon);
                        } else if (editType.equalsIgnoreCase("create_new_stage")) {
                            String chat = Chat.chatEdit.getOrDefault(p.getName() + "_" + dungeon, null);
                            StageBase stageBase = Dungeons.stageBase.get(chat);
                            String stageData = stageBase.getData();
                            String path = Editor.editorStagePath.get(p.getName() + "_" + dungeon);
                            FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeon + ".yml");
                            config.set(path + "id", chat);
                            for (int i = 0; i < stageData.split(";").length; i++) {
                                if (stageBase.getValueData(stageData.split(";")[i]).equalsIgnoreCase("string")) {
                                    config.set(path + stageData.split(";")[i], "edit_this");
                                } else if (stageBase.getValueData(stageData.split(";")[i]).equalsIgnoreCase("int")) {
                                    config.set(path + stageData.split(";")[i], 0);
                                }
                            }
                            if (stageBase.getPreData() != null) {
                                String stagePreData = stageBase.getPreData();
                                String pathPreData = Editor.editorStagePath.get(p.getName() + "_" + dungeon) + "pre_stage.";
                                for (int i = 0; i < stagePreData.split(";").length; i++) {
                                    if (stageBase.getValueData(stagePreData.split(";")[i]).equalsIgnoreCase("string")) {
                                        config.set(pathPreData + stagePreData.split(";")[i], "edit_this");
                                    } else if (stageBase.getValueData(stagePreData.split(";")[i]).equalsIgnoreCase("int")) {
                                        config.set(pathPreData + stagePreData.split(";")[i], 0);
                                    }
                                }
                            }
                            saveFiles(dungeon);
                            reloadFiles(dungeon);
                            new MainEditor(p, dungeon).openInventory(p);
                        }
                    }
                }
                editorType.remove(p);
                editorDungeonByBreak.remove(p.getName() + "_" + dungeon);
                editorDungeonInfo.remove(p.getName() + "_" + dungeon);
                editorDungeon.remove(p);
                editorChatType.remove(p);
                Chat.chatEdit.remove(p.getName() + "_" + dungeon);
                Chat.chatNumberEdit.remove(p.getName() + "_" + dungeon);
                Editor.editorStageType.remove(p.getName() + "_" + dungeon);
                Editor.editorStagePath.remove(p.getName() + "_" + dungeon);
            }
        }
        new MainEditor(p, dungeon).openInventory(p);
    }

    private static void edit(Player p, @NotNull String path, @NotNull String edit, @NotNull String dungeon) {
        FileConfiguration config = SimpleConfigurationManager.get().get("Dungeons/" + dungeon + ".yml");
        if (editorType.get(p).equalsIgnoreCase("chat")) {
            if (Editor.editorChatType.get(p).equalsIgnoreCase("string")) {
                config.set(path, edit);
            } else if (Editor.editorChatType.get(p).equalsIgnoreCase("int")) {
                config.set(path, Integer.parseInt(edit));
            } else if (Editor.editorChatType.get(p).equalsIgnoreCase("list_string")) {
                if (!config.getStringList(path).isEmpty()) {
                    List<String> list = config.getStringList(path);
                    list.add(edit);
                    config.set(path, list);
                } else {
                    config.set(path, List.of(edit));
                }
            }
        } else if (editorType.get(p).equalsIgnoreCase("break_block")) {
            config.set(path, edit);
        } else if (editorType.get(p).equalsIgnoreCase("stage")) {
            String editType = Editor.editorStageType.get(p.getName() + "_" + dungeon);
            if (editType.contains("chat")) {
                String[] editTypeSplit = editType.split("_");
                if (editTypeSplit[1].equalsIgnoreCase("string")) {
                    config.set(path, edit);
                }
                if (editTypeSplit[1].equalsIgnoreCase("int")) {
                    config.set(path, Integer.parseInt(edit));
                }
            } else if (editType.equalsIgnoreCase("break_block")) {
                config.set(path, edit);
            }
        } else config.set(path, edit);
        saveFiles(dungeon);
        reloadFiles(dungeon);
    }

    public static void saveFiles(@NotNull String dungeon) {
        SimpleConfigurationManager.get().save("Dungeons/" + dungeon + ".yml");
    }

    public static void reloadFiles(@NotNull String dungeon) {
        SimpleConfigurationManager.get().reload("Dungeons/" + dungeon + ".yml");
    }
}
