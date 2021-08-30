package cc.oilslug.api.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataManager {

    private final HashMap<UUID, PlayerData> dataHashMap;

    public DataManager() {
        dataHashMap = new HashMap<>();

    }

    public PlayerData getPlayerData(UUID uuid) {
        return dataHashMap.getOrDefault(uuid, null);
    }

    public void addPlayerData(UUID uuid) {
        if(getPlayerData(uuid) == null) {
            dataHashMap.put(uuid, new PlayerData(uuid));
        }
    }

    public void removePlayerData(UUID uuid) {
        if(getPlayerData(uuid) != null) {
            dataHashMap.remove(uuid);
        }
    }

    // Adds all the player data into a list so we can easily loop through all the players
    public List<PlayerData> toList() {
        return new ArrayList<>(dataHashMap.values());
    }

}
