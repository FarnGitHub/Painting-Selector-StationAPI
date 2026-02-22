package farn.paintingSelector;

import farn.paintingSelector.screen.PaintingSelectHandler;
import farn.paintingSelector.screen.PaintingSelectInventory;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.ClientWorld;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;

public class PaintingSelectorImpl {

    public static void handlePlacePainting(PlayerEntity player, MessagePacket messagePacket) {
        if (player.world.isRemote) return;
        if(!(player.currentScreenHandler instanceof PaintingSelectHandler handler)) {
            player.sendMessage("Not in the painting selector's screen");
            return;
        }
        if(!handler.canUse(player)) {
            player.sendMessage("Too far away from placement position");
            return;
        }
        PaintingVariants painting = getPaintingFromName(messagePacket.strings[0]);
        if (painting == null) {
            player.sendMessage("Painting doesn't exist");
            return;
        }

        if (summonPaintingEntity(
                player.world,
                createPaintingEntity(
                        player.world,
                        handler.paintingSelectInventory,
                        painting))) {
            player.inventory.removeStack(handler.paintingSelectInventory.slot, 1);
        } else
            player.sendMessage("Failed to place painting or the painting is too large");

    }

    public static boolean usePainting(PlayerEntity player, World world, int x, int y, int z, int side) {

        if(!world.isRemote) {
            //prevent crash when either server or client ScreenHandler desync
            if(player.currentScreenHandler != player.playerScreenHandler) {
                player.closeHandledScreen();
            }

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
            PaintingSelectInventory paintingSelectInventory = new PaintingSelectInventory(
                    player, x,y,z, newSide);
            GuiHelper.openGUI(player,
                    PaintingSelectorStationAPI.NAMESPACE.id("open_painting_screen"),
                    paintingSelectInventory, new PaintingSelectHandler(paintingSelectInventory));
        }
        return true;
    }

    private static PaintingEntity createPaintingEntity(World world, PaintingSelectInventory handler, PaintingVariants painting) {
        PaintingEntity paintingEntity = new PaintingEntity(world);
        paintingEntity.attachmentX = handler.x;
        paintingEntity.attachmentY = handler.y;
        paintingEntity.attachmentZ = handler.z;
        paintingEntity.variant = painting;
        paintingEntity.setFacing(handler.side);
        return paintingEntity;
    }

    private static boolean summonPaintingEntity(World world, PaintingEntity paintingEntity) {
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
