package farn.paintingSelector.mixin;

import farn.paintingSelector.data.PaintingPlacementPlayer;
import farn.paintingSelector.data.PaintingPlacementData;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PaintingPlacementPlayer {
    @Unique
    PaintingPlacementData paintingData;

    @Override
    public PaintingPlacementData paintingSelect_getPaintingData() {
        return paintingData;
    }

    @Override
    public void paintingSelect_setPaintingData(PaintingPlacementData paintingSelectHandler) {
        this.paintingData = paintingSelectHandler;
    }

}
