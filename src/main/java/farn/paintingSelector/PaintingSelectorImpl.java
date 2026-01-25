package farn.paintingSelector;

import farn.paintingSelector.data.PaintingPlacementData;
import farn.paintingSelector.data.PaintingPlacementPlayer;
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

    public static void handlePlacePainting(PlayerEntity player, MessagePacket messagePacket) {
        if (!player.world.isRemote) {
            PaintingPlacementData placementData = ((PaintingPlacementPlayer)player).paintingSelect_getPaintingData();
            if(placementData != null) {
                if(!placementData.isInPlaceableRange(player)) {
                    player.sendMessage("Too far away from placement position");
                    setPlacementData(player, null);
                    return;
                }

                PaintingVariants painting = getPaintingFromName(messagePacket.strings[0]);
                if (painting != null) {
                    if (summonPaintingEntity(
                            player.world,
                            createPaintingEntity(player.world, placementData, painting)))
                        player.inventory.removeStack(placementData.itemSlot(), 1);
                    else
                        player.sendMessage("Painting is too large or some other reason");
                } else {
                    player.sendMessage("Can't find that painting");
                }
                setPlacementData(player, null);
            } else {
                player.sendMessage("Can't find painting placement data");
            }
        }
    }

    @SuppressWarnings("all")
    public static void handleOpenPaintingScreen(PlayerEntity playerEntity, MessagePacket messagePacket) {
        if(FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) {
            openPaintingScreen();
        }
    }

    @SuppressWarnings("all")
    @Environment(EnvType.CLIENT)
    public static void openPaintingScreen() {
        ((Minecraft) FabricLoader.getInstance().getGameInstance()).setScreen(new PaintingSelectingScreen());
    }

    public static boolean usePainting(PlayerEntity player, World world, int x, int y, int z, int side) {
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

            setPlacementData(player, new PaintingPlacementData(x,y,z, player.inventory.selectedSlot, newSide));
            PacketHelper.sendTo(player, new MessagePacket(PaintingSelectorStationAPI.NAMESPACE.id("open_painting_screen")));
        }
        return true;
    }

    private static PaintingEntity createPaintingEntity(World world, PaintingPlacementData handler, PaintingVariants painting) {
        PaintingEntity paintingEntity = new PaintingEntity(world);
        paintingEntity.attachmentX = handler.x();
        paintingEntity.attachmentY = handler.y();
        paintingEntity.attachmentZ = handler.z();
        paintingEntity.variant = painting;
        paintingEntity.setFacing(handler.side());
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

    private static void setPlacementData(PlayerEntity player, PaintingPlacementData data) {
        ((PaintingPlacementPlayer) player).paintingSelect_setPaintingData(data);
    }
}
