package farn.paintingSelector.packet;

import farn.paintingSelector.PaintingSelector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlacePaintingPacket extends Packet implements ManagedPacket<PlacePaintingPacket> {
    public int x,y,z;
    public byte side;
    public String painting;

    public static final PacketType<PlacePaintingPacket> TYPE = PacketType.builder(false, true, PlacePaintingPacket::new).build();

    @Environment(EnvType.CLIENT)
    public PlacePaintingPacket(int x, int y, int z, byte side, String painting) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.painting = painting;
    }

    public PlacePaintingPacket() {
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            this.x = stream.readInt();
            this.y = stream.readInt();
            this.z = stream.readInt();
            this.side = stream.readByte();
            this.painting = stream.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(this.x);
            stream.writeInt(this.y);
            stream.writeInt(this.z);
            stream.writeByte(this.side);
            stream.writeUTF(this.painting);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void apply(NetworkHandler handler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(handler);
        PaintingSelector.placePainting(player, x, y, z, side, painting);
    }

    @Override
    public int size() {
        return 13 + (painting == null ? 0 : painting.length());
    }

    @Override
    public @NotNull PacketType<PlacePaintingPacket> getType() {
        return TYPE;
    }
}
