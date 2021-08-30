package cc.oilslug.impl.runnables;

import cc.oilslug.PlayTime;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSaveRunnable extends BukkitRunnable {

    @Override
    public void run() {
        PlayTime.INSTANCE.updateDatabase();
    }
}
