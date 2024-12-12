package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleData;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleMaterial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChunkParticleData {

    private SkinParticleData particle;

    public ChunkParticleData() {
    }

    public ChunkParticleData(SkinParticleData particle) {
        this.particle = particle;
    }

    public void readFromStream(ChunkInputStream stream) throws IOException {
        var file = stream.readFile();
        var context = stream.getContext();
        var inputStream = new DataInputStream(new ByteBufInputStream(file.getBytes()));
        this.particle = readContentFromStream(file.getName(), new ChunkInputStream() {

            @Override
            public DataInputStream getInputStream() {
                return inputStream;
            }

            @Override
            public ChunkContext getContext() {
                return context;
            }
        });
    }

    public void writeToStream(ChunkOutputStream stream) throws IOException {
        var bytes = Unpooled.buffer(1024);
        var context = stream.getContext();
        var outputStream = new DataOutputStream(new ByteBufOutputStream(bytes));
        writeContentToStream(particle, new ChunkOutputStream() {

            @Override
            public DataOutputStream getOutputStream() {
                return outputStream;
            }

            @Override
            public ChunkContext getContext() {
                return context;
            }
        });
        outputStream.close();
        stream.writeFile(ChunkFile.particle(particle.getName(), bytes));
    }

    private SkinParticleData readContentFromStream(String name, ChunkInputStream stream) throws IOException {
        var textureData = new ChunkTextureData();
        var material = stream.readEnum(SkinParticleMaterial.class);
        textureData.readFromStream(stream);
        //return new SkinParticleData(name, material, textureData.getTexture());
        throw new IOException("not implemented yet");
    }

    private void writeContentToStream(SkinParticleData particle, ChunkOutputStream stream) throws IOException {
        var textureData = new ChunkTextureData(particle.getTexture());
        textureData.setId(1);
        textureData.freeze(0, 0, p -> null);
        stream.writeEnum(particle.getMaterial());
        textureData.writeToStream(stream);
        throw new IOException("not implemented yet");
    }

    public SkinParticleData getParticle() {
        return particle;
    }
}
