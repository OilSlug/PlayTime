package cc.oilslug.api.utils;

import org.bukkit.ChatColor;

public class ChatUtil {
    public static String colour(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}