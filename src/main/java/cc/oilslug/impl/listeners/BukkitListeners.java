package cc.oilslug.impl.listeners;

import cc.oilslug.PlayTime;
import cc.oilslug.api.data.PlayerData;
import cc.oilslug.api.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayTime.INSTANCE.dataManager.addPlayerData(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        PlayerData data = PlayTime.INSTANCE.dataManager.getPlayerData(event.getPlayer().getUniqueId());
        PlayTime.INSTANCE.executorService.execute(() -> {
            Bukkit.getConsoleSender().sendMessage(ChatUtil.colour("&eSaving " + data.getUuid() + "'s data to the database...."));
            if (data.inDatabase) {
                PlayTime.INSTANCE.mysqlUtil.executeQueryBoolean("UPDATE `playtime_users` SET `playtime`=" + data.playTime + " WHERE `uuid`='" + data.getUuid().toString() + "'");
                Bukkit.getConsoleSender().sendMessage(ChatUtil.colour("&aUpdated " + data.getUuid() + "'s data"));
            } else {
                PlayTime.INSTANCE.mysqlUtil.executeQueryBoolean("INSERT INTO `playtime_users`(`uuid`, `playtime`) VALUES ('" + data.getUuid().toString() + "'," + data.playTime + ")");
                Bukkit.getConsoleSender().sendMessage(ChatUtil.colour("&aSaved " + data.getUuid() + "'s data to the database...."));
            }
        });
        PlayTime.INSTANCE.dataManager.removePlayerData(event.getPlayer().getUniqueId());
    }

}
