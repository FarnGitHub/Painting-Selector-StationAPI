package farn.paintingSelector.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import farn.paintingSelector.PaintingSelectorImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PaintingItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PaintingItem.class)
public class PaintingItemMixin {

    @WrapMethod(method="useOnBlock")
    public boolean useOnBlock(ItemStack stack, PlayerEntity player, World world, int x, int y, int z, int side, Operation<Boolean> original) {
        return PaintingSelectorImpl.usePainting(player, world, x,y,z, side);
    }
}
