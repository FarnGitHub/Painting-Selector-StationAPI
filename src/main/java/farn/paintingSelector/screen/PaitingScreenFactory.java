package farn.paintingSelector.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;

public class PaitingScreenFactory implements GuiHandler.ScreenFactory {
    @Override
    public Screen create(PlayerEntity player, Inventory inventory, MessagePacket packet) {
        return new PaintingSelectingScreen((PaintingSelectInventory)  inventory);
    }
}
