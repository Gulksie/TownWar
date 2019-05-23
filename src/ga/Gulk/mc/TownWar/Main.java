package ga.Gulk.mc.TownWar;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    Logger log;

    @Override
    public void onEnable() {
        log = getLogger();
        Static.logger = log;

        getServer().getPluginManager().registerEvents(new TWEventListener(), this);
        getCommand("donkey").setExecutor(new DonkeyCommand());
        getCommand("town").setExecutor(new TownCommand());

        Static.version = getDescription().getVersion();

        File saveFile = new File(getDataFolder(), "save.yml");

        if (saveFile.exists()) {
            log.info("Trying to load saved data...");
            try {
                Static.loadStatics(new String(Files.readAllBytes(saveFile.toPath())));

                log.info("Loaded data!");
            } catch (IOException ex) {
                throw new IllegalStateException("IOError while trying to load data", ex);
            }
        }

        log.info("Town War enabled.");
    }

    @Override
    public void onDisable() {

        try {
            log.info("Saving data...");
            //save our stuff
            File dataFolder = getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File saveFile = new File(dataFolder, "save.yml");
            saveFile.createNewFile();

            FileWriter writer = new FileWriter(saveFile);
            writer.write(Static.saveStatics());
            writer.flush();
            writer.close();
            log.info("Saved data");
        } catch (IOException ex) {
            throw new IllegalStateException("IOError while trying to save data", ex);
        }


        log.info("Town War disabled.");
    }
}
