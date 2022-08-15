package plus.crates.Utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import plus.crates.Events.PlayerInputEvent;

import java.util.List;

public class SignInputHandler {
    private static final String HANDLER_NAME = "update_sign";


    public static void injectNetty(final JavaPlugin plugin, final Player player) {
        try {
            final Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;

            if (channel != null) {
                if (channel.pipeline().get(HANDLER_NAME) != null) return; // Already exists

                channel.pipeline().addAfter("decoder", HANDLER_NAME, new MessageToMessageDecoder<ServerboundSignUpdatePacket>() {
                    @Override
                    protected void decode(ChannelHandlerContext channelHandlerContext, ServerboundSignUpdatePacket packet, List list) throws Exception {
                        String[] lines = packet.getLines();
                        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(new PlayerInputEvent(player, lines)));
                        list.add(packet);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ejectNetty(Player player) {
        try {
            Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
            if (channel != null) {
                if (channel.pipeline().get(HANDLER_NAME) != null) {
                    channel.pipeline().remove(HANDLER_NAME);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
