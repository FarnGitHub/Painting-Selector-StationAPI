package farn.paintingSelector.data;

import net.minecraft.entity.player.PlayerEntity;

public record PaintingPlacementData(int x, int y, int z, int itemSlot, int side) {

    public boolean isInPlaceableRange(PlayerEntity player) {
        return player.getSquaredDistance(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0;
    }
}
