package ga.Gulk.mc.TownWar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class Town {

    String name;
    int ID;
    UUID owner;
    Inventory donkey;
    UUID donkeyEntity;
    boolean spawnedDonkey;
    ArrayList<UUID> members;

    public Town(Player owner, String name) {
        this.owner = owner.getUniqueId();
        this.name = name;

        ID = Static.nextID;
        Static.nextID++;

        members = new ArrayList<>();
        members.add(this.owner);

        donkey = Bukkit.createInventory(null, 27, name + " town donkey");
        spawnedDonkey = false;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public UUID getOwner() {
        return owner;
    }

    public Inventory getDonkey() {
        return donkey;
    }

    public UUID getDonkeyEntity() {
        return donkeyEntity;
    }

    public boolean isSpawnedDonkey() {
        return spawnedDonkey;
    }

    public UUID[] getMembers() {
        return members.toArray(new UUID[members.size()]);
    }

    public void deleteTown() {
        //gets rid of all the town's stuff, the town will have to be removed from the array somewhere else
    }

    public void addMember(UUID player) {
        members.add(player);
    }

    public void spawnDonkey(Player spawnOn) {
        Donkey literalDonkey = (Donkey)(spawnOn.getWorld().spawnEntity(spawnOn.getLocation(), EntityType.DONKEY));
        literalDonkey.setAI(false);
        literalDonkey.setTamed(true);
        literalDonkey.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        literalDonkey.setCarryingChest(true);

        literalDonkey.setCustomName(getName() + "'s donkey");

        donkeyEntity = literalDonkey.getUniqueId();
        spawnedDonkey = true;
    }

    public void onDonkeyDeath(Location deathLocation) {
        for (ItemStack item : donkey.getContents()) {
            if (item != null) {
                deathLocation.getWorld().dropItemNaturally(deathLocation, item.clone());
            }
        }

        donkey.clear();
        spawnedDonkey = false;
    }
}
