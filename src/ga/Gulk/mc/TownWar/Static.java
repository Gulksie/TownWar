package ga.Gulk.mc.TownWar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class Static {

    public static Inventory publicDonkey;
    public static ArrayList<Town> towns;
    public static int nextID;
    public static HashMap<UUID, ArrayList<Integer>> playerInvites;
    public static String version;
    public static Logger logger;

    public static String redText = ChatColor.RED.toString();

    static {
        publicDonkey = Bukkit.createInventory(null, 27, "Public donkey");
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

    public static String invToString(Inventory inv) { //https://gist.github.com/aadnk/8138186
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inv.getSize());

            // Save every element in the list
            for (int i = 0; i < inv.getSize(); i++) {
                dataOutput.writeObject(inv.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save inventory.", ex);
        }
    }

    public static Inventory invFromString(String in) { //see link above
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(in));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            dataInput.close();
            return inventory;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load inventory.", ex);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Unable to load inventory.", ex);
        }
    }

    public static String saveStatics() {
        HashMap<String, Object> data = new HashMap<>();

        data.put("publicDonkey", invToString(publicDonkey));
        String[] townsSr = new String[towns.size()];

        for (int i = 0; i < townsSr.length; i++) {
            townsSr[i] = towns.get(i).generateYAML();
        }

        data.put("towns", townsSr);
        data.put("nextTownID", nextID);

        StringWriter toReturn = new StringWriter();

        new Yaml().dump(data, toReturn);

        return toReturn.toString();
    }

    public static void loadStatics(String in) {
        HashMap<String, Object> data = new Yaml().load(in);

        publicDonkey = invFromString((String)data.get("publicDonkey"));

        for (String i : (ArrayList<String>)data.get("towns")) {
            towns.add(Town.townFromYaml(i));
        }

        nextID = (Integer)data.get("nextTownID");
    }
}
