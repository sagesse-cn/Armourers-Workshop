package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

public enum ChunkType {
    SKIN("SKIN"),
    GEOMETRY_DATA("CCBO"),
    PAINT_DATA("PADT"),
    PREVIEW_DATA("VCBO"),
    ANIMATION_DATA("ANIM"),
    PALETTE_DATA("PALE"),
    FILE_DATA("FILE"),
    PROPERTIES("PPTS"),
    SKIN_PART("SKPR"),
    SKIN_PART_NAME("PRNM"),
    MARKER("PRMK"),
    SKIN_SETTINGS("SET4");

    private final String name;

    ChunkType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

