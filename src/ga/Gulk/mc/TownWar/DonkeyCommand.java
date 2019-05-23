package ga.Gulk.mc.TownWar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static ga.Gulk.mc.TownWar.Static.*;

public class DonkeyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 0) {
                String townName = args[0];
                Town town = getTown(townName);

                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("spawn") && town.getOwner().equals(player.getUniqueId())) {
                        if (town.isSpawnedDonkey()) {
                            player.sendMessage(redText + "Your town already has a donkey!");
                            return true;
                        } else {
                            town.spawnDonkey(player);
                            player.sendMessage("Spawned new donkey at your location");

                            return true;
                        }
                    }
                }

                boolean done = false;

                for (Town i : getTowns(player)) {
                    if (townName.equalsIgnoreCase(i.getName())) {
                        if (!i.isSpawnedDonkey()) {
                            player.sendMessage(redText + "This town doesn't have a donkey spawned!");
                            return true;
                        }
                        player.sendMessage("Opening " + townName + " donkey");
                        player.openInventory(i.getDonkey());
                        done = true;
                        break;
                    }
                }

                if (!done) {
                    player.sendMessage("Could not find that town. Opening public donkey");
                    player.openInventory(publicDonkey);
                }

                return true;
            } else {
                player.sendMessage("Could not find that town. Opening public donkey");
                player.openInventory(publicDonkey);
                return true;
            }
        }
        return false;
    }
}
