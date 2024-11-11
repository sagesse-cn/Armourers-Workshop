package moe.plushie.armourers_workshop.init.network;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.data.TickTracker;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.DataPackManager;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.UUID;

public class UpdateContextPacket extends CustomPacket {

    private UUID owner = null;
    private ByteBuf buffer = null;

    private final int flags;

    public UpdateContextPacket(int flags, UUID player) {
        this.flags = flags;
        this.owner = player;
    }

    public UpdateContextPacket(IFriendlyByteBuf buffer) {
        this.flags = buffer.readByte();
        this.buffer = buffer.asByteBuf();
        this.buffer.retain();
    }

    public static UpdateContextPacket all(Player player) {
        return new UpdateContextPacket(0x07, player.getUUID());
    }

    public static UpdateContextPacket config() {
        return new UpdateContextPacket(0x02, null);
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeByte(flags);
        if ((flags & 0x01) != 0) {
            buffer.writeUUID(ModContext.t2(owner));
            buffer.writeUUID(ModContext.t3(owner));
            buffer.writeFloat(TickTracker.server().animationTicks());
            buffer.writeUtf(ModConstants.MOD_NET_ID);
        }
        if ((flags & 0x02) != 0) {
            buffer.writeNbt(getConfig());
        }
        if ((flags & 0x04) != 0) {
            buffer.writeNbt(getDataPack());
        }
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        if (buffer == null) {
            return;
        }
        var reader = IFriendlyByteBuf.wrap(buffer);
        if ((flags & 0x01) != 0) {
            ModContext.init(reader.readUUID(), reader.readUUID());
            TickTracker.client().setAnimationTicks(reader.readFloat());
            checkNetworkVersion(reader.readUtf());
        }
        if ((flags & 0x02) != 0) {
            setConfig(reader.readNbt());
        }
        if ((flags & 0x04) != 0) {
            setDataPack(reader.readNbt());
        }
        buffer.release();
        buffer = null;
    }


    private void setConfig(CompoundTag tag) {
        if (tag == null) {
            return;
        }
        var fields = new HashMap<String, Object>();
        var properties = new SkinProperties(tag);
        for (var entry : properties.entrySet()) {
            fields.put(entry.getKey(), entry.getValue());
        }
        ModConfigSpec.COMMON.apply(fields);
    }

    private CompoundTag getConfig() {
        if (EnvironmentManager.isDedicatedServer()) {
            var properties = new SkinProperties();
            ModConfigSpec.COMMON.snapshot().forEach(properties::put);
            return properties.serializeNBT();
        }
        return null;
    }

    private void setDataPack(CompoundTag tag) {

    }

    private CompoundTag getDataPack() {
        return null;
    }

    private void checkNetworkVersion(String version) {
        if (!version.equals(ModConstants.MOD_NET_ID)) {
            ModLog.warn("network protocol conflict, server: {}, client: {}", version, ModConstants.MOD_NET_ID);
        }
    }
}
