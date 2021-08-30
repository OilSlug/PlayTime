package cc.oilslug.impl.runnables;

import cc.oilslug.PlayTime;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayTimeRunnable extends BukkitRunnable {

    @Override
    public void run() {
        PlayTime.INSTANCE.executorService.execute(() -> {
            PlayTime.INSTANCE.dataManager.toList().forEach(data -> {
                data.playTime++;
            });
        });
    }
}
