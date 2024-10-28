package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.level.block.state.BlockBehaviour;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterialColor;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.Map;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[1.20, )")
public class Constructor {

    private static final Map<AbstractBlockMaterial, NoteBlockInstrument> INSTRUMENTS = Collections.immutableMap(builder -> {
        builder.put(AbstractBlockMaterial.STONE, NoteBlockInstrument.BASEDRUM);
        builder.put(AbstractBlockMaterial.GLASS, NoteBlockInstrument.HAT);
    });

    private static final Map<AbstractBlockMaterial, SoundType> SOUNDS = Collections.immutableMap(builder -> {
        builder.put(AbstractBlockMaterial.STONE, SoundType.STONE);
        builder.put(AbstractBlockMaterial.GLASS, SoundType.GLASS);
    });

    private static final Map<AbstractBlockMaterialColor, MapColor> MATERIAL_COLORS = Collections.immutableMap(builder -> {
        builder.put(AbstractBlockMaterialColor.NONE, MapColor.NONE);
    });

    @Extension
    public static class Properties {

        public static BlockBehaviour.Properties of(@ThisClass Class<?> clazz, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor) {
            NoteBlockInstrument instrument = INSTRUMENTS.get(material);
            SoundType soundType = SOUNDS.get(material);
            MapColor mapColor = MATERIAL_COLORS.get(materialColor);
            return BlockBehaviour.Properties.of().instrument(instrument).sound(soundType).mapColor(mapColor);
        }
    }
}
