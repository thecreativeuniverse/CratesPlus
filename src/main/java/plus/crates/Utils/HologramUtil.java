package plus.crates.Utils;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.micrlink.individualholograms.IndividualHolograms;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;
import plus.crates.Crate;
import plus.crates.CratesPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HologramUtil {
    private HashMap<String, com.gmail.filoghost.holographicdisplays.api.Hologram> holograms = new HashMap<>();
    protected CratesPlus cratesPlus;
    private boolean shown_hologram_deprecated_warning = false;

    public HologramUtil(CratesPlus cratesPlus) {
        this.cratesPlus = cratesPlus;
    }

    public void createHologram(Location location, ArrayList<String> lines, Crate crate) {
        if (cratesPlus.useIndividualHolograms()) {
            IndividualHolograms.get().getHologramManager().createNewHologram("" + location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ(), location.clone().add(0, -1, 0), lines);
        } else if (cratesPlus.useHolographicDisplays()) {
            com.gmail.filoghost.holographicdisplays.api.Hologram hologram = HologramsAPI.createHologram(cratesPlus, location.clone().add(0, 1.25, 0));
            for (String line : lines) {
                hologram.appendTextLine(line);
            }
            holograms.put("" + location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ(), hologram);
        } else {
            if (cratesPlus.getBukkitVersion().equals("1.8") || cratesPlus.getBukkitVersion().startsWith("1.8.") || cratesPlus.getBukkitVersion().equals("1.9") || cratesPlus.getBukkitVersion().startsWith("1.9.")) {
                Hologram hologram = new Hologram(location, lines);
                crate.addHologram(location.getBlock().getLocation(), hologram);
                hologram.displayAll();
            } else if (!shown_hologram_deprecated_warning) {
                shown_hologram_deprecated_warning = true;
                cratesPlus.getLogger().warning("Only 1.8 - 1.9 supports the built in holograms, please use HolographicDisplays or Individual Holograms for holograms to work");
            }
        }
    }

    public void removeHologram(Location location) {
        if (cratesPlus.useIndividualHolograms()) {
            IndividualHolograms.get().getHologramManager().removeHologram("" + location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ());
        } else if (cratesPlus.useHolographicDisplays()) {
            if (holograms.containsKey("" + location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ())) {
                holograms.get("" + location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ()).delete();
                holograms.remove("" + location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ());
            }
        }
    }

}
