package farn.paintingSelector;

import farn.paintingSelector.data.PaintingPlacementData;
import farn.paintingSelector.screen.PaintingSelectingScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

public class PaintingSelectorImpl {

    public static void handlePlacing(PlayerEntity player, MessagePacket messagePacket) {
        if (!player.world.isRemote) {
            PaintingPlacementData placement = player.paintSelect_getPaintingData();
            if(placement != null) {
                if(placement.canReach(player)) {
                    PaintingVariants painting = getPainting(messagePacket.strings[0]);

                    if (painting != null) {
                        if (summon(player.world, create(player.world, placement, painting)))
                            player.inventory.removeStack(placement.itemSlot(), 1);
                        else
                            player.sendMessage("Failed to place painting");
                    } else
                        player.sendMessage("Painting doesn't exist");
                } else
                    player.sendMessage("Too far away from placement position");
                setPlacement(player, null);
            } else
                player.sendMessage("Can't get painting placement data");
        }
    }

    @SuppressWarnings("all")
    public static void openScreen(PlayerEntity playerEntity, MessagePacket messagePacket) {
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            openScreen();
    }

    @SuppressWarnings("all")
    @Environment(EnvType.CLIENT)
    public static void openScreen() {
        Minecraft.INSTANCE.setScreen(new PaintingSelectingScreen());
    }

    public static boolean use(PlayerEntity player, World world, int x, int y, int z, int side) {
        if(!world.isRemote) {
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
            setPlacement(player, new PaintingPlacementData(x,y,z, player.inventory.selectedSlot, newSide));
            PacketHelper.sendTo(player, new MessagePacket(PaintingSelectorStationAPI.NAMESPACE.id("open_painting_screen")));
        }
        return true;
    }

    private static PaintingEntity create(World world, PaintingPlacementData handler, PaintingVariants painting) {
        PaintingEntity paintingEntity = new PaintingEntity(world);
        paintingEntity.attachmentX = handler.x();
        paintingEntity.attachmentY = handler.y();
        paintingEntity.attachmentZ = handler.z();
        paintingEntity.variant = painting;
        paintingEntity.setFacing(handler.side());
        return paintingEntity;
    }

    private static boolean summon(World world, PaintingEntity paintingEntity) {
        return paintingEntity.canStayAttached() && world.spawnEntity(paintingEntity);
    }

    private static PaintingVariants getPainting(String name) {
        try {
            return PaintingVariants.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static void setPlacement(PlayerEntity player, PaintingPlacementData data) {
        player.paintSelect_setPaintingData(data);
    }
}
