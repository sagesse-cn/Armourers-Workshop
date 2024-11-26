package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.skin.paint.texture.ITextureProvider;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.Vector2f;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintType;
import moe.plushie.armourers_workshop.core.skin.paint.texture.TextureData;
import moe.plushie.armourers_workshop.core.skin.paint.texture.TextureOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class ChunkColorSection {

    protected int index = 0;
    protected int size = 0;
    protected int colorIndexBytes = 1;
    protected int textureIndexBytes = 4;
    protected boolean resolved = false;

    protected final int usedBytes; // 0 is texture
    protected final SkinPaintType paintType;

    public ChunkColorSection(int count, int colorBytes, SkinPaintType paintType) {
        this.size = count;
        this.usedBytes = colorBytes;
        this.paintType = paintType;
    }

    public abstract void writeToStream(IOutputStream stream) throws IOException;

    public void freeze(int index) {
        this.index = index;
        this.resolved = true;
    }

    public void freezeIndex(int colorUsedIndex, int textureUsedIndex) {
        this.colorIndexBytes = colorUsedIndex;
        this.textureIndexBytes = textureUsedIndex;
    }

    public abstract SkinPaintColor getColor(int index);

    public ChunkTextureData.TextureRef getTexture(Vector2f pos) {
        var list = getTextureList(pos);
        if (list != null) {
            return list.get(pos, this);
        }
        return null;
    }

    protected abstract ChunkTextureData getTextureList(Vector2f pos);

    public boolean isResolved() {
        return resolved;
    }

    public boolean isTexture() {
        return usedBytes == 0;
    }

    public int getStartIndex() {
        return index;
    }

    public int getEndIndex() {
        return index + size;
    }

    public int getSize() {
        return size;
    }

    public int getUsedBytes() {
        return usedBytes;
    }

    public SkinPaintType getPaintType() {
        return paintType;
    }

    public static class Immutable extends ChunkColorSection {

        private byte[] buffers;
        private ChunkTextureData[] textureLists;

        public Immutable(int total, int usedBytes, SkinPaintType paintType) {
            super(total, usedBytes, paintType);
        }

        public void readFromStream(IInputStream stream) throws IOException {
            if (usedBytes != 0) {
                buffers = new byte[usedBytes * size];
                stream.read(buffers);
            }
            if (isTexture()) {
                textureLists = new ChunkTextureData[size];
                for (int i = 0; i < size; ++i) {
                    var list = new ChunkTextureData();
                    list.readFromStream(stream);
                    textureLists[i] = list;
                }
                // restore the parent -> child.
                for (var parent : textureLists) {
                    var variants = new ArrayList<ITextureProvider>(parent.provider.getVariants());
                    for (var child : textureLists) {
                        if (parent.id == child.parentId) {
                            variants.add(child.provider);
                        }
                    }
                    if (parent.provider instanceof TextureData textureData) {
                        textureData.setVariants(variants);
                    }
                }
            }
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            if (buffers != null) {
                stream.write(buffers);
            }
            if (textureLists != null) {
                for (var list : textureLists) {
                    list.writeToStream(stream);
                }
            }
        }

        @Override
        public SkinPaintColor getColor(int offset) {
            int value = 0;
            for (int i = 0; i < usedBytes; ++i) {
                value = (value << 8) | (buffers[offset * usedBytes + i]) & 0xff;
            }
            return SkinPaintColor.of(value, getPaintType());
        }

        @Override
        public ChunkTextureData getTextureList(Vector2f pos) {
            if (textureLists == null) {
                return null;
            }
            for (var list : textureLists) {
                if (list.contains(pos)) {
                    return list;
                }
            }
            return null;
        }
    }

    public static class Mutable extends ChunkColorSection {

        private final ArrayList<Integer> colorLists = new ArrayList<>();

        private final LinkedHashMap<Integer, ColorRef> indexes = new LinkedHashMap<>();
        private final LinkedHashMap<ITextureProvider, ChunkTextureData> textureLists = new LinkedHashMap<>();

        public Mutable(int colorBytes, SkinPaintType paintType) {
            super(0, colorBytes, paintType);
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            for (int color : colorLists) {
                stream.writeFixedInt(color, usedBytes);
            }
            for (var list : textureLists.values()) {
                list.writeToStream(stream);
            }
        }

        @Override
        public void freeze(int index) {
            // we need to reorder all textures and save it.
            if (!textureLists.isEmpty()) {
                float x = 0;
                float y = 0;
                float lineHeight = 0;
                var lists = new ArrayList<>(textureLists.values());
                int columns = OpenMath.ceili(Math.sqrt(lists.size()));
                for (int i = 0, col = 0; i < lists.size(); ++i) {
                    var list = lists.get(i);
                    // add into line
                    list.freeze(x, y, textureLists::get);
                    var usedRect = list.getUsedRect();
                    lineHeight = Math.max(lineHeight, usedRect.getHeight());
                    x += usedRect.getWidth() + 16f;
                    if (++col < columns) {
                        continue;
                    }
                    // return to next line
                    y += lineHeight + 16f;
                    x = 0;
                    lineHeight = 0;
                    col = 0;
                }
            }
            size = colorLists.size() + textureLists.size();
            super.freeze(index);
        }

        @Override
        public SkinPaintColor getColor(int offset) {
            int value = colorLists.get(offset);
            return SkinPaintColor.of(value, getPaintType());
        }

        public ColorRef putColor(int value) {
            // if the transparent channel not used, clear it.
            if (usedBytes == 3) {
                value |= 0xff000000;
            }
            return indexes.computeIfAbsent(value, k -> {
                var ref = new ColorRef(this, colorLists.size());
                colorLists.add(k);
                return ref;
            });
        }

        public ChunkTextureData.TextureRef putTexture(Vector2f uv, ITextureProvider provider) {
            // we're also adding all variant textures.
            var textureList = getOrCreateTextureList(provider);
            Collections.eachTree(provider.getVariants(), ITextureProvider::getVariants, this::getOrCreateTextureList);
            return textureList.add(uv, this);
        }

        public ChunkTextureData.OptionsRef putTextureOptions(TextureOptions options) {
            return new ChunkTextureData.OptionsRef(this, options);
        }

        @Override
        protected ChunkTextureData getTextureList(Vector2f pos) {
            for (var list : textureLists.values()) {
                if (list.contains(pos)) {
                    return list;
                }
            }
            return null;
        }

        protected ChunkTextureData getOrCreateTextureList(ITextureProvider provider) {
            // ..
            return textureLists.computeIfAbsent(provider, it -> {
                var list = new ChunkTextureData(it);
                list.id = textureLists.size() + 1;
                return list;
            });
        }
    }

    public static class ColorRef implements ChunkVariable {

        private final int value;
        private final ChunkColorSection section;

        public ColorRef(ChunkColorSection section, int value) {
            this.value = value;
            this.section = section;
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeFixedInt(section.getStartIndex() + value, section.colorIndexBytes);
        }

        @Override
        public boolean freeze() {
            return section.isResolved();
        }
    }

}

