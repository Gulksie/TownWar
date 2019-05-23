package ga.Gulk.mc.TownWar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Static {

    public static Inventory publicDonkey;
    public static ArrayList<Inventory> donkeys;
    public static ArrayList<Town> towns;
    public static int nextID;
    public static HashMap<UUID, ArrayList<Integer>> playerInvites;
    public static String version;

    public static String redText = ChatColor.RED.toString();

    static {
        publicDonkey = Bukkit.createInventory(null, 27, "Public donkey");
        donkeys = new ArrayList<>();
        towns = new ArrayList<>();
        nextID = Integer.MIN_VALUE;

        playerInvites = new HashMap<>();
    }

    public static Town[] getTowns(Player player) {
        ArrayList<Town> playerTowns = new ArrayList<>();

        for (Town i : towns) {
            for (UUID j : i.getMembers()) {
                if (j.equals(player.getUniqueId())) {
                    playerTowns.add(i);
                    break;
                }
            }
        }

        return playerTowns.toArray(new Town[playerTowns.size()]);
    }

    public static Town getTown(String name) {
        for (Town i : towns) {
            if (i.name.equals(name)) {
                return i;
            }
        }

        return null;
    }

    public static Town getTown(Integer ID) {
        for (Town i : towns) {
            if (new Integer(i.getID()).intValue() == ID.intValue())
                return i;
        }

        return null;
    }

    public static boolean noPerms(Player p) {
        p.sendMessage(redText + "You do not have the required permissions to use this command.");
        return true;
    }
}
