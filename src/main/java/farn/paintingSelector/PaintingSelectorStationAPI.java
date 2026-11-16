package farn.paintingSelector;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
public class PaintingSelectorStationAPI {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @SuppressWarnings("unused")
    @EventListener
    public static void registerPacket(MessageListenerRegistryEvent event) {
        event.register(NAMESPACE.id("place_painting"), PaintingSelectorImpl::handlePlacePainting);
    }

}
