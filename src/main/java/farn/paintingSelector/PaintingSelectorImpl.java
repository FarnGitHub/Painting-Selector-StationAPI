package farn.paintingSelector;

import farn.paintingSelector.screen.PaintingSelectingScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.util.SideUtil;

public class PaintingSelectorImpl {

    public static void handlePlacePainting(PlayerEntity player, MessagePacket messagePacket) {
        if (player.world.isRemote || messagePacket.ints == null || messagePacket.strings == null) return;

        ItemStack stack = player.inventory.getSelectedItem();
        if(stack == null || stack.itemId != Item.PAINTING.id) {
            player.sendMessage("Must have painting item");
            return;
        }

        int x = messagePacket.ints[0];
        int y = messagePacket.ints[1];
        int z = messagePacket.ints[2];
        int side = messagePacket.ints[3];
        if(player.getSquaredDistance(x + 0.5, y + 0.5, z + 0.5) > 64.0) {
            player.sendMessage("Too far away from placement position");
            return;
        }

        PaintingVariants painting = getPaintingFromName(messagePacket.strings[0]);
        if (painting == null) {
            player.sendMessage("Painting doesn't exist");
            return;
        }

        if (summonPainting(player.world,x,y,z,side,painting)) {
            player.inventory.removeStack(player.inventory.selectedSlot, 1);
        } else
            player.sendMessage("Failed to place painting or the painting is too large");

    }

    public static boolean usePainting(int x, int y, int z, int side) {
        int newSide;
        switch (side) {
            case 4:
                newSide = 1;
                break;
            case 3:
                newSide = 2;
                break;
            case 2:
                newSide = 0;
                break;
            case 5:
                newSide = 3;
                break;
            default: return false;
        }
        SideUtil.runClient(() -> openScreen(x,y,z, newSide));
        return true;
    }

    @Environment(EnvType.CLIENT)
    private static void openScreen(int x, int y, int z, int side) {
        Minecraft.INSTANCE.setScreen(new PaintingSelectingScreen(x, y, z, side));
    }

    private static boolean summonPainting(World world, int x, int y, int z, int side, PaintingVariants painting) {
        PaintingEntity paintingEntity = new PaintingEntity(world);
        paintingEntity.attachmentX = x;
        paintingEntity.attachmentY = y;
        paintingEntity.attachmentZ = z;
        paintingEntity.variant = painting;
        paintingEntity.setFacing(side);
        return paintingEntity.canStayAttached() && world.spawnEntity(paintingEntity);
    }

    private static PaintingVariants getPaintingFromName(String name) {
        try {
            return PaintingVariants.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
