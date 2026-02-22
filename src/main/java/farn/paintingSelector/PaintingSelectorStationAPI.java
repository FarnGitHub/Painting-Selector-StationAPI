package farn.paintingSelector;

import farn.paintingSelector.screen.PaintingSelectInventory;
import farn.paintingSelector.screen.PaitingScreenFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
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
    }

    @EventListener
    public void registerScreenHandler(GuiHandlerRegistryEvent event) {
        event.register(NAMESPACE.id("open_painting_screen"), new GuiHandler(new PaitingScreenFactory(), PaintingSelectInventory::new));
    }

}
