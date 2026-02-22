package farn.paintingSelector.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PaintingSelectInventory implements Inventory {
    public int x,y,z, slot, side;
    private PlayerEntity player;

    public PaintingSelectInventory() {
    }

    public PaintingSelectInventory(PlayerEntity player, int x, int y, int z, int side) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
        this.slot = player.inventory.selectedSlot;
        this.side = side;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public ItemStack getStack(int slot) {
        if(player == null) return null;
        return player.getHand();
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if(player == null) return null;
        return player.getHand();
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
    }

    @Override
    public String getName() {
        return "Painting Screen";
    }

    @Override
    public int getMaxCountPerStack() {
        return Item.PAINTING.getMaxCount();
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.getSquaredDistance(x,y,z) < 64.0D;
    }
}
