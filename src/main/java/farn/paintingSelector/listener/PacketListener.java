package farn.paintingSelector.listener;

import farn.paintingSelector.PaintingSelector;
import farn.paintingSelector.screen.PaintingSelectorScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
public class PacketListener {

    @Entrypoint.Namespace
    public static Namespace NS;

    private static final boolean server = FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;

    @SuppressWarnings("unused")
    @EventListener
    public static void registerPacket(MessageListenerRegistryEvent event) {
        event.register(NS.id("place"), (player, message) -> {
            try {
                int x = message.ints[0];
                int y = message.ints[1];
                int z = message.ints[2];
                byte side = message.bytes[0];
                String art = message.strings[0];
                PaintingSelector.placePainting(player, x, y, z, side, art);
            } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
            }
        });
        event.register(NS.id("open"), (player, message) -> {
            try {
                if(server) return;
                int x = message.ints[0];
                int y = message.ints[1];
                int z = message.ints[2];
                byte side = message.bytes[0];
                openScreen(x, y, z, side);
            } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static void openScreen(int x, int y, int z, byte side) {
        Minecraft.INSTANCE.setScreen(new PaintingSelectorScreen(x, y, z, side));
    }

}
