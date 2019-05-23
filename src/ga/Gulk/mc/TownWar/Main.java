package ga.Gulk.mc.TownWar;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin {
    Logger log;

    @Override
    public void onEnable() {
        log = getLogger();

        getServer().getPluginManager().registerEvents(new TWEventListener(), this);
        getCommand("donkey").setExecutor(new DonkeyCommand());
        getCommand("town").setExecutor(new TownCommand());

        Static.version = getDescription().getVersion();

        log.info("Town War enabled.");
    }

    @Override
    public void onDisable() {
        log.info("Town War disabled.");
    }
}
