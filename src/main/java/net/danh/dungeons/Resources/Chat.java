package net.danh.dungeons.Resources;

import net.danh.dungeons.Dungeons;
import net.danh.dungeons.NMS.NMSAssistant;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Chat {

    private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");

    @Contract("_ -> new")
    public static @NotNull String normalColorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', replaceColorSymbol(string));
    }

    @Contract("_ -> new")
    public static @NotNull String normalColorizewp(String string) {
        return ChatColor.translateAlternateColorCodes('&', replaceColorSymbol(Files.getConfig().getString("settings.prefix") + " " + string));
    }

    @Contract("_ -> new")
    public static @NotNull List<String> normalColorize(List<String> msg) {
        return msg.stream().map(s -> "&7" + normalColorize(s)).collect(Collectors.toList());
    }

    public static void debug(String message) {
        if (Files.getConfig().getBoolean("settings.debug")) Dungeons.getDungeonCore().getLogger().warning(message);
    }

    @NotNull
    public static String replaceColorCode(String message) {
        message = message.replace("§", "&");
        if (message.contains("&0")) message = message.replace("&0", "<black>");
        if (message.contains("&1")) message = message.replace("&1", "<dark_blue>");
        if (message.contains("&2")) message = message.replace("&2", "<dark_green>");
        if (message.contains("&3")) message = message.replace("&3", "<dark_aqua>");
        if (message.contains("&4")) message = message.replace("&4", "<dark_red>");
        if (message.contains("&5")) message = message.replace("&5", "<dark_purple>");
        if (message.contains("&6")) message = message.replace("&6", "<gold>");
        if (message.contains("&7")) message = message.replace("&7", "<gray>");
        if (message.contains("&8")) message = message.replace("&8", "<dark_gray>");
        if (message.contains("&9")) message = message.replace("&9", "<blue>");
        if (message.contains("&o")) message = message.replace("&o", "<italic>");
        if (message.contains("&a")) message = message.replace("&a", "<green>");
        if (message.contains("&b")) message = message.replace("&b", "<aqua>");
        if (message.contains("&c")) message = message.replace("&c", "<red>");
        if (message.contains("&d")) message = message.replace("&d", "<light_purple>");
        if (message.contains("&e")) message = message.replace("&e", "<yellow>");
        if (message.contains("&f")) message = message.replace("&f", "<white>");
        if (message.contains("&k")) message = message.replace("&k", "<obfuscated>");
        if (message.contains("&l")) message = message.replace("&l", "<bold>");
        if (message.contains("&m")) message = message.replace("&m", "<strikethrough>");
        if (message.contains("&n")) message = message.replace("&n", "<underlined>");
        if (message.contains("&r")) message = message.replace("&r", "<reset>");
        if (message.contains("&O")) message = message.replace("&O", "<italic>");
        if (message.contains("&A")) message = message.replace("&A", "<green>");
        if (message.contains("&B")) message = message.replace("&B", "<aqua>");
        if (message.contains("&C")) message = message.replace("&C", "<red>");
        if (message.contains("&D")) message = message.replace("&D", "<light_purple>");
        if (message.contains("&E")) message = message.replace("&E", "<yellow>");
        if (message.contains("&F")) message = message.replace("&F", "<white>");
        if (message.contains("&K")) message = message.replace("&K", "<obfuscated>");
        if (message.contains("&L")) message = message.replace("&L", "<bold>");
        if (message.contains("&M")) message = message.replace("&M", "<strikethrough>");
        if (message.contains("&N")) message = message.replace("&N", "<underlined>");
        if (message.contains("&R")) message = message.replace("&R", "<reset>");
        return message;
    }

    @NotNull
    public static String replaceColorSymbol(@NotNull String message) {
        if (message.contains("<black>")) message = message.replace("<black>", "&0");
        if (message.contains("<dark_blue>")) message = message.replace("<dark_blue>", "&1");
        if (message.contains("<dark_green>")) message = message.replace("<dark_green>", "&2");
        if (message.contains("<dark_aqua>")) message = message.replace("<dark_aqua>", "&3");
        if (message.contains("<dark_red>")) message = message.replace("<dark_red>", "&4");
        if (message.contains("<dark_purple>")) message = message.replace("<dark_purple>", "&5");
        if (message.contains("<gold>")) message = message.replace("<gold>", "&6");
        if (message.contains("<gray>")) message = message.replace("<gray>", "&7");
        if (message.contains("<dark_gray>")) message = message.replace("<dark_gray>", "&8");
        if (message.contains("<blue>")) message = message.replace("<blue>", "&9");
        if (message.contains("<italic>")) message = message.replace("<italic>", "&o");
        if (message.contains("<green>")) message = message.replace("<green>", "&a");
        if (message.contains("<aqua>")) message = message.replace("<aqua>", "&b");
        if (message.contains("<red>")) message = message.replace("<red>", "&c");
        if (message.contains("<light_purple>")) message = message.replace("<light_purple>", "&d");
        if (message.contains("<yellow>")) message = message.replace("<yellow>", "&e");
        if (message.contains("<white>")) message = message.replace("<white>", "&f");
        if (message.contains("<obfuscated>")) message = message.replace("<obfuscated>", "&k");
        if (message.contains("<bold>")) message = message.replace("<bold>", "&l");
        if (message.contains("<strikethrough>")) message = message.replace("<strikethrough>", "&m");
        if (message.contains("<underlined>")) message = message.replace("<underlined>", "&n");
        if (message.contains("<reset>")) message = message.replace("<reset>", "&r");

        if (message.contains("</black>")) message = message.replace("</black>", "");
        if (message.contains("</dark_blue>")) message = message.replace("</dark_blue>", "");
        if (message.contains("</dark_green>")) message = message.replace("</dark_green>", "");
        if (message.contains("</dark_aqua>")) message = message.replace("</dark_aqua>", "");
        if (message.contains("</dark_red>")) message = message.replace("</dark_red>", "");
        if (message.contains("</dark_purple>")) message = message.replace("</dark_purple>", "");
        if (message.contains("</gold>")) message = message.replace("</gold>", "");
        if (message.contains("</gray>")) message = message.replace("</gray>", "");
        if (message.contains("</dark_gray>")) message = message.replace("</dark_gray>", "");
        if (message.contains("</blue>")) message = message.replace("</blue>", "");
        if (message.contains("</italic>")) message = message.replace("</italic>", "");
        if (message.contains("</green>")) message = message.replace("</green>", "");
        if (message.contains("</aqua>")) message = message.replace("</aqua>", "");
        if (message.contains("</red>")) message = message.replace("</red>", "");
        if (message.contains("</light_purple>")) message = message.replace("</light_purple>", "");
        if (message.contains("</yellow>")) message = message.replace("</yellow>", "");
        if (message.contains("</white>")) message = message.replace("</white>", "");
        if (message.contains("</obfuscated>")) message = message.replace("</obfuscated>", "");
        if (message.contains("</bold>")) message = message.replace("</bold>", "");
        if (message.contains("</strikethrough>")) message = message.replace("</strikethrough>", "");
        if (message.contains("</underlined>")) message = message.replace("</underlined>", "");
        if (message.contains("</reset>")) message = message.replace("</reset>", "");
        return applyColor(message, true);
    }


    public static String applyColor(String message, boolean r) {
        if (!r) {
            return message;
        } else if (new NMSAssistant().isVersionLessThan(16)) {
            return ChatColor.translateAlternateColorCodes('&', message);
        } else {
            for (Matcher matcher = hexPattern.matcher(message); matcher.find(); matcher = hexPattern.matcher(message)) {
                net.md_5.bungee.api.ChatColor hexColor = net.md_5.bungee.api.ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
                String before = message.substring(0, matcher.start());
                String after = message.substring(matcher.end());
                message = before + hexColor + after;
            }

            return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message);
        }
    }

    public static @NotNull String caseOnWords(@NotNull String input) {
        String[] words = input.replace("_", " ").replace("-", " ").split("\\s+");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (i == 0) {
                builder.append(capitalizeWord(word));
            } else {
                builder.append(capitalizeIfUppercase(word));
            }

            if (i < words.length - 1) {
                builder.append(" ");
            }
        }

        return builder.toString();
    }

    private static @NotNull String capitalizeWord(@NotNull String word) {
        if (word.isEmpty()) {
            return word;
        }
        return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
    }

    private static String capitalizeIfUppercase(@NotNull String word) {
        if (word.isEmpty()) {
            return word;
        }
        return word.equals(word.toUpperCase())
                ? capitalizeWord(word)
                : word.toLowerCase();
    }


    public static void sendMessage(@NotNull CommandSender sender, String message) {
        sender.sendMessage(normalColorizewp(message));
    }

    public static void sendMessage(CommandSender sender, @NotNull List<String> messages) {
        messages.forEach(message -> sendMessage(sender, message));
    }
}

