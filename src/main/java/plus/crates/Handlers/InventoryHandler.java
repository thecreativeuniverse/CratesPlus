package plus.crates.Handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import plus.crates.CratesPlus;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryHandler implements Listener {

    private static final Set<UUID> opened = ConcurrentHashMap.newKeySet();
    private final CratesPlus cratesPlus;

    public InventoryHandler(CratesPlus cratesPlus) {
        this.cratesPlus = cratesPlus;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        opened.remove(event.getPlayer().getUniqueId());
    }

    public void open(Player player, Inventory inventory) {
        player.openInventory(inventory);
        opened.add(player.getUniqueId());
    }

    public void openChild(Player player, Inventory inventory) {
        player.closeInventory();
        Bukkit.getScheduler().runTaskLater(cratesPlus, () -> open(player, inventory), 2L);
    }

    public boolean isOpened(UUID uuid) {
        return opened.contains(uuid);
    }

    public void onClose(UUID uuid) {
        opened.remove(uuid);
    }

}
