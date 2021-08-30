package cc.oilslug.impl.commands;

import cc.oilslug.PlayTime;
import cc.oilslug.api.data.PlayTimeData;
import cc.oilslug.api.data.PlayerData;
import cc.oilslug.api.utils.ChatUtil;
import cc.oilslug.settings.MessageSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.*;

public class TopPlayTimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to run this command!");
            return true;
        }

        final Player player = (Player) sender;
        final PlayerData playerData = PlayTime.INSTANCE.dataManager.getPlayerData(player.getUniqueId());

        if (!player.hasPermission("playtime.commands.top")) {
            player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "You do not have the correct permissions for this command!"));
            return true;
        }

        final ResultSet resultSet = PlayTime.INSTANCE.mysqlUtil.executeQuery("SELECT * FROM `playtime_users` WHERE 1");
        if (resultSet == null) {
            return true;
        }

        HashMap<String, PlayTimeData> allData = new HashMap<>();

        PlayTime.INSTANCE.executorService.execute(() -> {
            try {

                while (resultSet.next()) {
                    String uuid = resultSet.getString("uuid");
                    allData.put(uuid, new PlayTimeData(uuid, resultSet.getLong("playtime")));
                    if(resultSet.isLast()) {
                        List<PlayTimeData> playTimeDataList = new ArrayList<>(allData.values());
                        Collections.sort(playTimeDataList);
                        Collections.reverse(playTimeDataList);

                        player.sendMessage("");
                        player.sendMessage(ChatUtil.colour("          &b&lTOP PLAYTIME"));
                        player.sendMessage("");
                        for (int i = 0; i < Math.min(10, playTimeDataList.size()); i++) {
                            PlayTimeData playTimeData = playTimeDataList.get(i);
                            player.sendMessage(ChatUtil.colour("&b&l" + (i + 1) + ". &f" + Bukkit.getOfflinePlayer(UUID.fromString(playTimeData.UUID)).getName() + " &8: &7" + playTimeData.playtime + " second(s)"));
                        }
                    }
                }

            } catch (Exception exception) {
                player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "Failed to pull data from the database :("));
            }
        });

        return true;
    }
}
