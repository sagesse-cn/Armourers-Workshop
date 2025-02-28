package moe.plushie.armourers_workshop.core.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;
import moe.plushie.armourers_workshop.init.ModConfig;
import net.minecraft.world.entity.player.Player;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ResponseSkinPacket extends CustomPacket {

    private final String identifier;
    private final Mode mode;
    private final boolean compress;
    private final Exception exp;
    private final Skin skin;

    public ResponseSkinPacket(String identifier, Skin skin, Exception exp) {
        this.identifier = identifier;
        this.exp = exp;
        this.skin = skin;
        this.mode = skin != null ? Mode.STREAM : Mode.EXCEPTION;
        this.compress = ModConfig.Common.enableServerCompressesSkins;
    }

    public ResponseSkinPacket(IFriendlyByteBuf buffer) {
        this.identifier = buffer.readUtf();
        this.mode = buffer.readEnum(Mode.class);
        this.compress = buffer.readBoolean();
        this.exp = readException(buffer);
        this.skin = readSkinStream(buffer);
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeUtf(identifier);
        buffer.writeEnum(mode);
        buffer.writeBoolean(compress);
        writeException(buffer, exp);
        writeSkinStream(buffer, skin);
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        SkinLoader.getInstance().addSkin(identifier, skin, exp);
    }

    private Exception readException(IFriendlyByteBuf buffer) {
        if (mode != Mode.EXCEPTION) {
            return null;
        }
        InputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            inputStream = createInputStream(buffer);
            objectInputStream = new ObjectInputStream(inputStream);
            return (Exception) objectInputStream.readObject();
        } catch (Exception exception) {
            return exception;
        } finally {
            StreamUtils.closeQuietly(objectInputStream, inputStream);
        }
    }

    private void writeException(IFriendlyByteBuf buffer, Exception exception) {
        if (mode != Mode.EXCEPTION) {
            return;
        }
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            outputStream = createOutputStream(buffer);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(exception);
        } catch (Exception exception1) {
            exception1.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(objectOutputStream, outputStream);
        }
    }

    private Skin readSkinStream(IFriendlyByteBuf buffer) {
        if (mode != Mode.STREAM) {
            return null;
        }
        try (var inputStream = createInputStream(buffer)) {
            return SkinSerializer.readFromStream(null, inputStream);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private void writeSkinStream(IFriendlyByteBuf buffer, Skin skin) {
        if (mode != Mode.STREAM) {
            return;
        }
        try (var outputStream = createOutputStream(buffer)) {
            SkinSerializer.writeToStream(skin, null, outputStream);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private InputStream createInputStream(IFriendlyByteBuf buffer) throws Exception {
        var inputStream = new ByteBufInputStream(buffer.asByteBuf());
        if (this.compress) {
            return new GZIPInputStream(inputStream);
        }
        return inputStream;
    }

    private OutputStream createOutputStream(IFriendlyByteBuf buffer) throws Exception {
        var outputStream = new ByteBufOutputStream(buffer.asByteBuf());
        if (this.compress) {
            return new GZIPOutputStream(outputStream);
        }
        return outputStream;
    }

    public enum Mode {
        EXCEPTION, STREAM
    }
}
