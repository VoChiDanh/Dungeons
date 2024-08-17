package net.danh.dungeons.Resources;

import net.danh.dungeons.Dungeons;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Chat {


    @Contract("_ -> new")
    public static @NotNull String normalColorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', replaceColorSymbol(string));
    }

    @Contract("_ -> new")
    public static @NotNull List<String> normalColorize(List<String> msg) {
        return msg.stream().map(s -> "&7" + normalColorize(s)).collect(Collectors.toList());
    }

    public static void debug(String message) {
        if (Files.getConfig().getBoolean("settings.debug")) Dungeons.getDungeonCore().getLogger().warning(message);
    }

    public static @NotNull Component colorize(String message) {
        return MiniMessage.miniMessage().deserialize(
                LegacyComponentSerializer.legacyAmpersand().serialize(
                        LegacyComponentSerializer.legacySection().deserialize(
                                LegacyComponentSerializer.legacyAmpersand().serialize(
                                        LegacyComponentSerializer.legacyAmpersand().deserialize(
                                                replaceColorCode(message)
                                        )
                                )
                        )
                )
        );
    }

    public static List<Component> colorize(String... message) {
        return Arrays.stream(message).map(Chat::colorize).collect(Collectors.toList());
    }

    public static List<Component> colorize(@NotNull List<String> message) {
        return message.stream().map(Chat::colorize).collect(Collectors.toList());
    }

    public static @NotNull Component colorizewp(String message) {
        return MiniMessage.miniMessage().deserialize(
                LegacyComponentSerializer.legacyAmpersand().serialize(
                        LegacyComponentSerializer.legacySection().deserialize(
                                LegacyComponentSerializer.legacyAmpersand().serialize(
                                        LegacyComponentSerializer.legacyAmpersand().deserialize(
                                                replaceColorCode(Files.getConfig().getString("settings.prefix") + " " + message)
                                        )
                                )
                        )
                )
        );
    }

    public static List<Component> colorizewp(String... message) {
        return Arrays.stream(message).map(Chat::colorizewp).collect(Collectors.toList());
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
        return message;
    }

    public static List<Component> colorizewp(@NotNull List<String> message) {
        return message.stream().map(Chat::colorizewp).collect(Collectors.toList());
    }

    public static @NotNull String caseOnWords(@NotNull String input) {
        StringBuilder builder = new StringBuilder(input.replace("_", " ")
                .replace("-", " "));
        boolean isLastSpace = true;

        for (int i = 0; i < builder.length(); ++i) {
            char ch = builder.charAt(i);
            if (isLastSpace && ch >= 'a' && ch <= 'z') {
                builder.setCharAt(i, (char) (ch + -32));
                isLastSpace = false;
            } else {
                isLastSpace = ch == ' ';
            }
        }

        return builder.toString();
    }

    public static void sendMessage(@NotNull Audience sender, String message) {
        sender.sendMessage(colorizewp(message));
    }

    public static void sendMessage(Audience sender, @NotNull List<String> messages) {
        messages.forEach(message -> sendMessage(sender, message));
    }
}

