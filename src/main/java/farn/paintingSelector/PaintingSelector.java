package farn.paintingSelector;

import farn.paintingSelector.listener.PacketListener;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

public class PaintingSelector {

    public static void placePainting(PlayerEntity player, int x, int y, int z, byte side, String art) {
        ItemStack stack = player.inventory.getSelectedItem();
        if(stack == null || stack.itemId != Item.PAINTING.id) {
            player.sendMessage("Must have painting item");
            return;
        }

        if(player.getSquaredDistance(x + 0.5, y + 0.5, z + 0.5) > 64.0) {
            player.sendMessage("Too far away from placement position");
            return;
        }

        PaintingVariants painting = getFromName(art);
        if (painting == null) {
            player.sendMessage("Painting doesn't exist");
            return;
        }

        if (summonPainting(player.world,x,y,z,side,painting)) {
            player.inventory.removeStack(player.inventory.selectedSlot, 1);
        } else
            player.sendMessage("Failed to place painting or the painting is too large");

    }

    public static boolean usePainting(PlayerEntity player, int x, int y, int z, int side) {
        byte newSide = switch (side) {
            case 4 -> 1;
            case 3 -> 2;
            case 2 -> 0;
            case 5 -> 3;
            default -> -1;
        };
        if(newSide != -1) {
            MessagePacket packet = new MessagePacket(PacketListener.NS.id("open"));
            packet.bytes = new byte[]{newSide};
            packet.ints = new int[]{x,y,z};
            PacketHelper.sendTo(player, packet);
            return true;
        }
        return false;
    }

    private static boolean summonPainting(World world, int x, int y, int z, int side, PaintingVariants painting) {
        PaintingEntity ent = new PaintingEntity(world);
        ent.attachmentX = x;
        ent.attachmentY = y;
        ent.attachmentZ = z;
        ent.variant = painting;
        ent.setFacing(side);
        return ent.canStayAttached() && world.spawnEntity(ent);
    }

    private static PaintingVariants getFromName(String name) {
        try {
            return PaintingVariants.valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }
}
