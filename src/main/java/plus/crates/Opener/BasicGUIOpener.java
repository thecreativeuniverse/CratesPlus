package plus.crates.Opener;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plus.crates.Crate;
import plus.crates.CratesPlus;
import plus.crates.Winning;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class BasicGUIOpener extends Opener implements Listener {

    private static final Set<Material> GLASS_PANES = Set.of(
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.GREEN_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE
    );

    private CratesPlus cratesPlus;
    private HashMap<UUID, Integer> tasks = new HashMap<>();
    private HashMap<UUID, Inventory> guis = new HashMap<>();
    private int length = 5;

    public BasicGUIOpener(CratesPlus cratesPlus) {
        super(cratesPlus, "BasicGUI");
        this.cratesPlus = cratesPlus;
    }

    @Override
    public void doSetup() {
        FileConfiguration config = getOpenerConfig();
        if (!config.isSet("Length")) {
            config.set("Length", cratesPlus.getConfigHandler().getCrateGUITime());
            try {
                config.save(getOpenerConfigFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        length = config.getInt("Length");
        cratesPlus.getServer().getPluginManager().registerEvents(this, cratesPlus);
    }

    @Override
    public void doOpen(final Player player, final Crate crate, Location blockLocation) {
        final Inventory winGUI;
        final Integer[] timer = {0};
        final Integer[] currentItem = new Integer[1];

        Random random = new Random();
        int max = crate.getWinnings().size() - 1;
        int min = 0;
        currentItem[0] = random.nextInt((max - min) + 1) + min;
        winGUI = Bukkit.createInventory(null, 45, crate.getColor() + crate.getName() + " Win");
        guis.put(player.getUniqueId(), winGUI);
        cratesPlus.getInventoryHandler().open(player, winGUI);
        final int maxTimeTicks = length * 10;
        tasks.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimerAsynchronously(cratesPlus, new Runnable() {
            public void run() {
                if (!player.isOnline()) {
                    finish(player);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key " + player.getName() + " " + crate.getName() + " 1");
                    Bukkit.getScheduler().cancelTask(tasks.get(player.getUniqueId()));
                    return;
                }
                Integer i = 0;
                while (i < 45) {
                    if (i == 22) {
                        i++;
                        if (crate.getWinnings().size() == currentItem[0])
                            currentItem[0] = 0;
                        final Winning winning;
                        if (timer[0] == maxTimeTicks) {
                            winning = getWinning(crate);
                        } else {
                            winning = crate.getWinnings().get(currentItem[0]);
                        }

                        final ItemStack currentItemStack = winning.getPreviewItemStack();
                        if (timer[0] == maxTimeTicks) {
                            winning.runWin(player);
                        }
                        winGUI.setItem(22, currentItemStack);

                        currentItem[0]++;
                        continue;
                    }
                    ItemStack itemStack = new ItemStack(GLASS_PANES.stream().skip(cratesPlus.getCrateHandler().randInt(0, GLASS_PANES.size() - 1)).findFirst().orElse(Material.RED_STAINED_GLASS_PANE), 1);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (timer[0] == maxTimeTicks) {
                        itemMeta.setDisplayName(ChatColor.RESET + "Winner!");
                    } else {
                        Sound sound;
                        try {
                            sound = Sound.valueOf("NOTE_PIANO");
                        } catch (Exception e) {
                            try {
                                sound = Sound.valueOf("BLOCK_NOTE_HARP");
                            } catch (Exception ee) {
                                try {
                                    sound = Sound.valueOf("BLOCK_NOTE_BLOCK_HARP");
                                } catch (Exception eee) {
                                    return; // This should never happen!
                                }
                            }
                        }
                        final Sound finalSound = sound;
                        Bukkit.getScheduler().runTask(cratesPlus, new Runnable() {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTitle() != null && player.getOpenInventory().getTitle().contains(" Win"))
                                    player.playSound(player.getLocation(), finalSound, (float) 0.2, 2);
                            }
                        });
                        itemMeta.setDisplayName(ChatColor.RESET + "Rolling...");
                    }
                    itemStack.setItemMeta(itemMeta);
                    winGUI.setItem(i, itemStack);
                    i++;
                }
                if (timer[0] == maxTimeTicks) {
                    finish(player);
                    Bukkit.getScheduler().cancelTask(tasks.get(player.getUniqueId()));
                    return;
                }
                timer[0]++;
            }
        }, 0L, 2L).getTaskId());
    }

    @Override
    public void doReopen(Player player, Crate crate, Location location) {
        cratesPlus.getInventoryHandler().openChild(player, guis.get(player.getUniqueId()));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!cratesPlus.getInventoryHandler().isOpened(event.getWhoClicked().getUniqueId())) return;
        if (title.contains(" Win") && !title.contains("Edit ")) {
            if (event.getInventory().getType() == InventoryType.CHEST && event.getSlot() != 22 || event.getCurrentItem() != null) {
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
            }
        }
    }

}
