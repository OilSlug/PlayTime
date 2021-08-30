package cc.oilslug.impl.commands;

import cc.oilslug.PlayTime;
import cc.oilslug.api.data.PlayerData;
import cc.oilslug.api.utils.ChatUtil;
import cc.oilslug.settings.MessageSettings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;

public class AdminPlayTimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to run this command!");
            return true;
        }

        final Player player = (Player) sender;
        final PlayerData playerData = PlayTime.INSTANCE.dataManager.getPlayerData(player.getUniqueId());

        if(!player.hasPermission("playtime.commands.modify")) {
            player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "You do not have the correct permissions for this command!"));
            return true;
        }

        if(args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if(target == null) {
                player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "" + args[0] + " has not logged on this server!"));
                return true;
            }

            long time;
            try {
                time = Long.parseLong(args[1]);
            } catch (Exception exception) {
                player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "Please enter an valid number"));
                return true;
            }

            if(time < 0) {
                player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "Please enter a time over 0!"));
                return true;
            }

            PlayerData targetData = PlayTime.INSTANCE.dataManager.getPlayerData(target.getUniqueId());
            if(targetData != null) {
                targetData.playTime = time;
            }

            if(!PlayTime.INSTANCE.cachedPlaytime.containsKey(target.getUniqueId())) {
                final ResultSet resultSet = PlayTime.INSTANCE.mysqlUtil.executeQuery("SELECT * FROM `playtime_users` WHERE `uuid`='" + target.getUniqueId().toString() + "'");
                if(resultSet == null) {
                    PlayTime.INSTANCE.cachedPlaytime.put(target.getUniqueId(), 0L);
                } else {
                    try {
                        while (resultSet.next()) {
                            PlayTime.INSTANCE.cachedPlaytime.put(target.getUniqueId(), resultSet.getLong("playtime"));
                        }
                    } catch (Exception exception) {
                        PlayTime.INSTANCE.cachedPlaytime.put(target.getUniqueId(), 0L);
                    }
                }
            }

            long playtime = PlayTime.INSTANCE.cachedPlaytime.getOrDefault(target.getUniqueId(), 0L);
            PlayTime.INSTANCE.cachedPlaytime.put(target.getUniqueId(), 0L);

            if(playtime != 0 || playerData.inDatabase) {
                PlayTime.INSTANCE.mysqlUtil.executeQueryBoolean("UPDATE `playtime_users` SET `playtime`=" + time + " WHERE `uuid`='" + target.getUniqueId().toString() + "'");
            } else {
                PlayTime.INSTANCE.mysqlUtil.executeQueryBoolean("INSERT INTO `playtime_users`(`uuid`, `playtime`) VALUES ('" + target.getUniqueId().toString() + "'," + time + ")");
            }
            player.sendMessage(ChatUtil.colour(MessageSettings.prefix + args[0] + "'s play time has been set to " + time));
        }else {
            player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "Usage: /modifyplaytime <Player> <time in seconds>"));
        }
        return true;
    }
}
