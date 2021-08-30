package cc.oilslug.api.data;

import cc.oilslug.PlayTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    public long playTime;
    private Player player;
    public boolean inDatabase;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        loadData();
    }

    public void loadData() {
        final ResultSet resultSet = PlayTime.INSTANCE.mysqlUtil.executeQuery("SELECT * FROM `playtime_users` WHERE `uuid`='" + uuid.toString() + "'");

        if(resultSet == null) {
            playTime = 0;
            inDatabase = false;
        } else {
            try {
                while (resultSet.next()) {
                    playTime = resultSet.getLong("playtime");
                    inDatabase = true;
                }
            } catch (Exception exception) {
                playTime = 0;
                inDatabase = false;
            }
        }
    }

    public void updateData(int playTime) {
        this.playTime = playTime;
    }

    public void updateInMySQL() {

    }

    public UUID getUuid() {
        return uuid;
    }

    public void debug(String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&8[&cDebug&8] &7"+ message));
    }

}
