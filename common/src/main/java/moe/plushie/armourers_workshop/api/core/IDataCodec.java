package moe.plushie.armourers_workshop.api.core;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("unused")
public interface IDataCodec<A> {

    Codec<A> codec();

    static <T> IDataCodec<T> wrap(Codec<T> codec) {
        return () -> codec;
    }

    default IDataCodec<List<A>> listOf() {
        return wrap(codec().listOf());
    }

    default <S> IDataCodec<S> xmap(final Function<? super A, ? extends S> to, final Function<? super S, ? extends A> from) {
        return wrap(codec().xmap(to, from));
    }

    default <T> IDataCodec<A> either(IDataCodec<T> alternative, Function<T, A> converter) {
        return wrap(Codec.either(codec(), alternative.codec()).xmap(either -> either.map(v -> v, converter), Either::left));
    }

    default <T extends IDataSerializable.Immutable> IDataCodec<T> serializer(Function<? super IDataSerializer, ? extends T> factory) {
        return xmap(tag -> {
            var serializer = AbstractDataSerializer.wrap((CompoundTag) tag);
            return factory.apply(serializer);
        }, it -> {
            var tag = new CompoundTag();
            it.serialize(AbstractDataSerializer.wrap(tag));
            // noinspection unchecked
            return (A) tag;
        });
    }


    IDataCodec<Boolean> BOOL = wrap(Codec.BOOL);

    IDataCodec<Byte> BYTE = wrap(Codec.BYTE);

    IDataCodec<Short> SHORT = wrap(Codec.SHORT);

    IDataCodec<Integer> INT = wrap(Codec.INT);

    IDataCodec<Long> LONG = wrap(Codec.LONG);

    IDataCodec<Float> FLOAT = wrap(Codec.FLOAT);

    IDataCodec<Double> DOUBLE = wrap(Codec.DOUBLE);

    IDataCodec<String> STRING = wrap(Codec.STRING);


    IDataCodec<ByteBuffer> BYTE_BUFFER = wrap(Codec.BYTE_BUFFER);


    IDataCodec<UUID> UUID = INT.listOf().xmap(it -> {
        long l = (long) it.get(0) << 32 | (long) it.get(1) & 0xffffffffL;
        long m = (long) it.get(2) << 32 | (long) it.get(3) & 0xffffffffL;
        return new UUID(l, m);
    }, it -> {
        var result = new ArrayList<Integer>();
        long l = it.getMostSignificantBits();
        long m = it.getLeastSignificantBits();
        result.add((int) (l >> 32));
        result.add((int) l);
        result.add((int) (m >> 32));
        result.add((int) m);
        return result;
    });


    IDataCodec<BlockPos> BLOCK_POS = wrap(BlockPos.CODEC).either(LONG, BlockPos::of);

    IDataCodec<CompoundTag> COMPOUND_TAG = wrap(CompoundTag.CODEC);

    IDataCodec<ItemStack> ITEM_STACK = wrap(ItemStack.CODEC);

}
