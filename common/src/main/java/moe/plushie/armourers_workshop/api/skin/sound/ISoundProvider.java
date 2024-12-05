package moe.plushie.armourers_workshop.api.skin.sound;

import io.netty.buffer.ByteBuf;

public interface ISoundProvider {

    String getName();

    ByteBuf getBuffer();
}
