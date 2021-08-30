package cc.oilslug.api.msql;

import cc.oilslug.PlayTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class MysqlUtil {

    private Connection connection;

    private final String host, username, password, database;
    private final int port;

    public MysqlUtil(String host, String username, String password, String database, int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[PlayTime] Connecting to mysql...");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            initDatabase();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[PlayTime] Connected to mysql!");
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[PlayTime] Failed to connect to mysql.");
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            ResultSet resultSet = connection.prepareStatement(query).executeQuery();
            return resultSet;
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[PlayTime] Failed to excute mysql query!");
        }
        return null;
    }
    public boolean executeQueryBoolean(String query) {
        try {
            boolean resultSet = connection.prepareStatement(query).execute();
            return resultSet;
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[PlayTime] Failed to excute mysql query!");
        }
        return false;
    }

    public void initDatabase() {
        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS `playtime_users` (uuid text, playtime bigint(20))").execute();
        } catch (Exception exception) { //
            // If this is called then the database is probably already setup or the database connection is lost
        }
    }

}
