package ga.Gulk.mc.TownWar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;


public class TWEventListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent event) {
        Entity clickedEntity = event.getRightClicked();
        if (event.getHand() == EquipmentSlot.HAND && clickedEntity instanceof Donkey) {
            for (Town i : Static.towns) {
                if (i.getDonkeyEntity().equals(clickedEntity.getUniqueId())) {
                    event.setCancelled(true);
                    event.getPlayer().openInventory(i.getDonkey());
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Donkey) {
            for (Town i : Static.towns) {
                if (event.getEntity().getUniqueId().equals(i.getDonkeyEntity())) {
                    if (((Donkey) event.getEntity()).getHealth() - event.getDamage() <= 0) { //donkey died
                        i.onDonkeyDeath(event.getEntity().getLocation());

                        for (UUID j : i.getMembers()) {
                            Player player = Bukkit.getPlayer(j);

                            if (player != null) {
                                player.sendMessage(ChatColor.YELLOW + i.getName() + "'s donkey died!");
                            }
                        }
                    } else {
                        for (UUID j : i.getMembers()) {
                            Player player = Bukkit.getPlayer(j);

                            if (player != null) {
                                player.sendMessage(ChatColor.YELLOW + i.getName() + "'s donkey is taking damage!");
                            }
                        }
                    }

                    break;
                }
            }
        }
    }
}
