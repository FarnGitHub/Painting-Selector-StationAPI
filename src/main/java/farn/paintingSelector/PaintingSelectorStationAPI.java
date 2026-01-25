package farn.paintingSelector;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;

public class PaintingSelectorStationAPI {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE = Null.get();

    @EventListener
    public void registerPacket(MessageListenerRegistryEvent event) {
        event.register(NAMESPACE.id("place_painting"), PaintingSelectorImpl::handlePlacePainting);
        event.register(NAMESPACE.id("open_painting_screen"),  PaintingSelectorImpl::handleOpenPaintingScreen);
    }

}
