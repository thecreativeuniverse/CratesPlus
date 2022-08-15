package plus.crates.Utils;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a spawn egg that can be used to spawn mobs. Only for 1.9+, from https://gist.github.com/tastybento/6053eeb68c1c19d33540
 * Updated to use reflection instead of direct NMS.
 *
 * @author tastybento & Connor Linfoot
 */
public class SpawnEggNBT {
    private EntityType type;

    public SpawnEggNBT(EntityType type) {
        this.type = type;
    }

    public EntityType getSpawnedType() {
        return type;
    }

    public void setSpawnedType(EntityType type) {
        if (type.isAlive()) {
            this.type = type;
        }
    }

    public ItemStack toItemStack(int amount) {
        return new ItemStack(Material.valueOf(String.format("%s_SPAWN_EGG", type.name().toUpperCase())), amount);
    }

    public static SpawnEggNBT fromItemStack(ItemStack itemStack) {
        if (itemStack == null || !itemStack.getType().name().contains("SPAWN_EGG")) return null;
        return new SpawnEggNBT(EntityType.valueOf(itemStack.getType().name().replace("_SPAWN_EGG", "")));
    }

}