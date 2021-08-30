package cc.oilslug.api.data;

public class PlayTimeData implements Comparable<PlayTimeData> {

    public String UUID;
    public long playtime;

    public PlayTimeData(String UUID, long playtime) {
        this.UUID = UUID;
        this.playtime = playtime;
    }

    @Override
    public int compareTo(PlayTimeData playTimeData) {
        return (int) (playtime - playTimeData.playtime);
    }
}
