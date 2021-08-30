package cc.oilslug;

import cc.oilslug.api.data.DataManager;
import cc.oilslug.api.msql.MysqlUtil;
import cc.oilslug.impl.commands.AdminPlayTimeCommand;
import cc.oilslug.impl.commands.PlayTimeCommand;
import cc.oilslug.impl.commands.TopPlayTimeCommand;
import cc.oilslug.impl.listeners.BukkitListeners;
import cc.oilslug.impl.runnables.AutoSaveRunnable;
import cc.oilslug.impl.runnables.PlayTimeRunnable;
import cc.oilslug.settings.MessageSettings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PlayTime extends JavaPlugin {

    public static PlayTime INSTANCE;
    public DataManager dataManager;

    public MysqlUtil mysqlUtil;

    public ScheduledExecutorService executorService;

    public Map<UUID, Long> cachedPlaytime;

    @Override
    public void onEnable() {
        INSTANCE = this;
        cachedPlaytime = new HashMap<>();
        dataManager = new DataManager();
        executorService = Executors.newSingleThreadScheduledExecutor();

        /*
            Setting up the config
         */
        saveResource("config.yml", false);

        /*
            Setting up the bukkit events and runnable
         */
        Bukkit.getPluginManager().registerEvents(new BukkitListeners(), this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new PlayTimeRunnable(), 0, 20);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AutoSaveRunnable(), 0, 20 * 60); // Auto-saves all data to the database every minuet

        /*
            Configure and set up the mysql database
         */
        final FileConfiguration configuration = getConfig();
        mysqlUtil = new MysqlUtil(configuration.getString("mysql.host"), configuration.getString("mysql.username"),configuration.getString("mysql.password"),configuration.getString("mysql.database"), configuration.getInt("mysql.port"));

        /*
            Setup playtime's message settings
         */
        MessageSettings.prefix = configuration.getString("messages.prefix");

        /*
            Setup the playtime commands
         */
        getCommand("playtime").setExecutor(new PlayTimeCommand());
        getCommand("topplaytime").setExecutor(new TopPlayTimeCommand());
        getCommand("modifyplaytime").setExecutor(new AdminPlayTimeCommand());

        /*
            Add all the online players so they dont need to reconnect
         */
        for (Player player : Bukkit.getOnlinePlayers()) {
            dataManager.addPlayerData(player.getUniqueId());
        }

    }

    @Override
    public void onDisable() {
        updateDatabase();
    }

    /*
        Updates all the user's data in the database
     */
    public void updateDatabase() {
        Bukkit.getConsoleSender().sendMessage("Saving all playertime...");
        executorService.execute(() -> {
            dataManager.toList().forEach(data -> {
                if(data.inDatabase) {
                    mysqlUtil.executeQueryBoolean("UPDATE `playtime_users` SET `playtime`=" + data.playTime + " WHERE `uuid`='" + data.getUuid().toString() + "'");
                } else {
                    data.inDatabase = true;
                    mysqlUtil.executeQueryBoolean("INSERT INTO `playtime_users`(`uuid`, `playtime`) VALUES ('" + data.getUuid().toString() + "'," + data.playTime + ")");
                }
            });
        });
        Bukkit.getConsoleSender().sendMessage("Saved all playertime!");
    }

}
