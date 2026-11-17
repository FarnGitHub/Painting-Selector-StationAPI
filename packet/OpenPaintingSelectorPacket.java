package farn.paintingSelector.packet;

import farn.paintingSelector.screen.PaintingSelectorScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
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

public class OpenPaintingSelectorPacket extends Packet implements ManagedPacket<OpenPaintingSelectorPacket> {
    public int x,y,z;
    public byte side;

    public static final PacketType<OpenPaintingSelectorPacket> TYPE = PacketType.builder(true, false, OpenPaintingSelectorPacket::new).build();

    @Environment(EnvType.CLIENT)
    public OpenPaintingSelectorPacket(int x, int y, int z, byte side) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
    }

    public OpenPaintingSelectorPacket() {
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            this.x = stream.readInt();
            this.y = stream.readInt();
            this.z = stream.readInt();
            this.side = stream.readByte();
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            applyClient(PlayerHelper.getPlayerFromGame());
    }

    @Environment(EnvType.CLIENT)
    public void applyClient(PlayerEntity player) {
        Minecraft.INSTANCE.setScreen(new PaintingSelectorScreen(player, x, y, z, side));
    }

    @Override
    public int size() {
        return 13;
    }

    @Override
    public @NotNull PacketType<OpenPaintingSelectorPacket> getType() {
        return TYPE;
    }
}
