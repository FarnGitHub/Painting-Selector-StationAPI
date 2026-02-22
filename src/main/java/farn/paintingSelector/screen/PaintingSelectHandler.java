package farn.paintingSelector.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;

public class PaintingSelectHandler extends ScreenHandler {
    public PaintingSelectInventory paintingSelectInventory;

    public PaintingSelectHandler(PaintingSelectInventory inventory) {
        super();
        paintingSelectInventory = inventory;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return paintingSelectInventory.canPlayerUse(player);
    }
}
