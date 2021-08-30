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

public class PlayTimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to run this command!");
            return true;
        }

        final Player player = (Player) sender;
        final PlayerData playerData = PlayTime.INSTANCE.dataManager.getPlayerData(player.getUniqueId());

        if(args.length != 0) {

            if (!player.hasPermission("playtime.commands.see")) {
                player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "You do not have the correct permissions for this command!"));
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if(target == null) {
                player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "" + args[0] + " has not logged on this server!"));
                return true;
            }
            if(!PlayTime.INSTANCE.cachedPlaytime.containsKey(target.getUniqueId())) {
                final ResultSet resultSet = PlayTime.INSTANCE.mysqlUtil.executeQuery("SELECT * FROM `playtime_users` WHERE `uuid`='" + target.getUniqueId().toString() + "'");
                if(resultSet == null) {
                    PlayTime.INSTANCE.cachedPlaytime.put(target.getUniqueId(), 0L);
                }
                try {
                    while (resultSet.next()) {
                        PlayTime.INSTANCE.cachedPlaytime.put(target.getUniqueId(), resultSet.getLong("playtime"));
                    }
                } catch (Exception exception) {
                    PlayTime.INSTANCE.cachedPlaytime.put(target.getUniqueId(), 0L);
                }
            }
            long playtime = PlayTime.INSTANCE.cachedPlaytime.getOrDefault(target.getUniqueId(), 0L);
            PlayerData targetData = PlayTime.INSTANCE.dataManager.getPlayerData(target.getUniqueId());
            if(targetData != null) {
                playtime = targetData.playTime;
            }

            if(playtime == 0L) {
                player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "" + args[0] + " has not logged on this server!"));
            } else player.sendMessage(ChatUtil.colour(MessageSettings.prefix + args[0] + " have played for " + playtime + " second(s)"));

        }else {
            if (player.hasPermission("playtime.commands.self")) {
                player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "You have played for " + playerData.playTime + " second(s)"));
            } else player.sendMessage(ChatUtil.colour(MessageSettings.prefix + "You do not have the correct permissions for this command!"));
        }
        return true;
    }
}
