package plus.crates.Handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plus.crates.Crate;
import plus.crates.CratesPlus;
import plus.crates.Utils.LegacyMaterial;
import plus.crates.Winning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsHandler {
    private CratesPlus cratesPlus;
    private Inventory settings;
    private Inventory crates;
    private HashMap<String, String> lastCrateEditing = new HashMap<>();

    public SettingsHandler(CratesPlus cratesPlus) {
        this.cratesPlus = cratesPlus;
        setupSettingsInventory();
        setupCratesInventory();
    }

    public void setupSettingsInventory() {
        settings = Bukkit.createInventory(null, 9, "CratesPlus Settings");

        ItemStack itemStack;
        ItemMeta itemMeta;
        List<String> lore;


        /** Crates */

        itemStack = new ItemStack(Material.CHEST);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Edit Crates");
        lore = new ArrayList<>();
        lore.add("");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        settings.setItem(2, itemStack);


        /** Reload Config */

        Material material;
        try {
            material = Material.valueOf("BARRIER");
        } catch (Exception i) {
            material = LegacyMaterial.REDSTONE_TORCH_ON.getMaterial();
        }

        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Reload Config");
        lore = new ArrayList<>();
        lore.add("");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        settings.setItem(6, itemStack);
    }

    public void setupCratesInventory() {
        crates = Bukkit.createInventory(null, 54, "Crates");

        ItemStack itemStack;
        ItemMeta itemMeta;

        for (Map.Entry<String, Crate> entry : cratesPlus.getConfigHandler().getCrates().entrySet()) {
            Crate crate = entry.getValue();

            itemStack = new ItemStack(Material.CHEST);
            itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(crate.getName(true));
            itemStack.setItemMeta(itemMeta);
            crates.addItem(itemStack);
        }
    }

    public void openSettings(final Player player) {
        Bukkit.getScheduler().runTaskLater(cratesPlus, new Runnable() {
            @Override
            public void run() {
                cratesPlus.getInventoryHandler().open(player, settings);
            }
        }, 1L);
    }

    public void openCrates(final Player player) {
        Bukkit.getScheduler().runTaskLater(cratesPlus, new Runnable() {
            @Override
            public void run() {
                cratesPlus.getInventoryHandler().open(player, crates);
            }
        }, 1L);
    }

    public void openCrateWinnings(final Player player, String crateName) {
        Crate crate = cratesPlus.getConfigHandler().getCrates().get(crateName.toLowerCase());
        if (crate == null) {
            player.sendMessage(ChatColor.RED + "Unable to find " + crateName + " crate");
            return;
        }

        if (crate.containsCommandItem()) {
            player.sendMessage(ChatColor.RED + "You can not currently edit a crate in the GUI which has command items");
            player.closeInventory();
            return;
        }

        final Inventory inventory = Bukkit.createInventory(null, 54, "Edit " + crate.getName(false) + " Crate Winnings");

        for (Winning winning : crate.getWinnings()) {
            inventory.addItem(winning.getWinningItemStack());
        }

        Bukkit.getScheduler().runTaskLater(cratesPlus, new Runnable() {
            @Override
            public void run() {
                cratesPlus.getInventoryHandler().open(player, inventory);
            }
        }, 1L);

    }

    public void openCrate(final Player player, String crateName) {
        Crate crate = cratesPlus.getConfigHandler().getCrates().get(crateName.toLowerCase());
        if (crate == null) {
            return; // TODO Error handling here
        }

        final Inventory inventory = Bukkit.createInventory(null, 9, "Edit " + crate.getName(false) + " Crate");

        ItemStack itemStack;
        ItemMeta itemMeta;
        List<String> lore;


        /** Rename Crate */

        itemStack = new ItemStack(Material.NAME_TAG);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "Rename Crate");
        lore = new ArrayList<>();
        lore.add("");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(1, itemStack);


        /** Edit Crate Winnings */

        itemStack = new ItemStack(Material.DIAMOND);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "Edit Crate Winnings");
        lore = new ArrayList<>();
        lore.add("");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(3, itemStack);


        /** Edit Crate Color */

        itemStack = new ItemStack(LegacyMaterial.WOOL.getMaterial(), 1, (short) 3);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "Edit Crate Color");
        lore = new ArrayList<>();
        lore.add("");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(5, itemStack);


        /** Delete Crate */

        Material material;

        try {
            material = Material.valueOf("BARRIER");
        } catch (Exception i) {
            material = LegacyMaterial.REDSTONE_TORCH_ON.getMaterial();
        }

        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "Delete Crate");
        lore = new ArrayList<>();
        lore.add("");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(7, itemStack);

        Bukkit.getScheduler().runTaskLater(cratesPlus, new Runnable() {
            @Override
            public void run() {
                cratesPlus.getInventoryHandler().open(player, inventory);
            }
        }, 1L);

    }

    public HashMap<String, String> getLastCrateEditing() {
        return lastCrateEditing;
    }

}
