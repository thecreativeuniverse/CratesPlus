package plus.crates.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSUtil {

    @Deprecated
    public static BlockPos getBlockPosition(Player player) {
        return getBlockPosition(player.getLocation());
    }

    public static BlockPos getBlockPosition(Location location) {
        return new BlockPos(location.getX(), location.getY(), location.getZ());
    }

    public static void sendPacket(Player player, Packet packet) {
        try {
            ((CraftPlayer) player).getHandle().connection.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}