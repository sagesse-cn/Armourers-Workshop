package moe.plushie.armourers_workshop.init.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class ExecuteCommandPacket extends CustomPacket {

    private static final List<Class<?>> SUPPORTED_CLASSES = Collections.immutableList(builder -> {
        builder.add(ModDebugger.class);
        builder.add(ModConfig.Client.class);
    });

    private final Class<?> object;
    private final Mode mode;
    private final String key;
    private final Object value;

    public ExecuteCommandPacket(Class<?> object, String key, Object value, Mode mode) {
        this.object = object;
        this.mode = mode;
        this.key = key;
        this.value = value;
    }

    public ExecuteCommandPacket(IFriendlyByteBuf buffer) {
        this.object = readClass(buffer);
        this.mode = buffer.readEnum(Mode.class);
        this.key = buffer.readUtf();
        this.value = readObject(buffer);
    }


    public static ExecuteCommandPacket set(Class<?> obj, String key, Object value) {
        return new ExecuteCommandPacket(obj, key, value, Mode.SET);
    }

    public static ExecuteCommandPacket get(Class<?> obj, String key) {
        return new ExecuteCommandPacket(obj, key, null, Mode.GET);
    }

    public static ExecuteCommandPacket invoke(Class<?> obj, String key, Object... args) {
        return new ExecuteCommandPacket(obj, key, args, Mode.INVOKE);
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeUtf(object.getName());
        buffer.writeEnum(mode);
        buffer.writeUtf(key);
        writeObject(buffer, value);
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        try {
            Object data = value;
            switch (mode) {
                case GET: {
                    var field = object.getField(key);
                    data = field.get(object);
                    break;
                }
                case SET: {
                    var field = object.getField(key);
                    field.set(object, data);
                    break;
                }
                case INVOKE: {
//                    Method method = object.getMethod(key);
//                    data = method.invoke(object);
//                    if (data == null) {
//                        data = "void";
//                    }
                    break;
                }
            }
            player.sendSystemMessage(Component.literal(key + " = " + data));
            // auto-apply debug config when change
            if (ModDebugger.class == object) {
                ModDebugger.apply();
            }
            // auto-save config when change
            if (ModConfig.Client.class == object) {
                ModConfigSpec.CLIENT.save();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Object readObject(IFriendlyByteBuf buffer) {
        if (mode != Mode.SET) {
            return null;
        }
        try (var inputStream = new ObjectInputStream(new ByteBufInputStream(buffer.asByteBuf()))) {
            return inputStream.readObject();
        } catch (Exception exception) {
            return exception;
        }
    }

    private void writeObject(IFriendlyByteBuf buffer, Object object) {
        if (mode != Mode.SET) {
            return;
        }
        try (var outputStream = new ObjectOutputStream(new ByteBufOutputStream(buffer.asByteBuf()))) {
            outputStream.writeObject(object);
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }
    }

    private Class<?> readClass(IFriendlyByteBuf buffer) {
        var name = buffer.readUtf();
        for (var clazz : SUPPORTED_CLASSES) {
            if (name.equals(clazz.getName())) {
                return clazz;
            }
        }
        return null;
    }

    public enum Mode {
        SET, GET, INVOKE
    }
}
