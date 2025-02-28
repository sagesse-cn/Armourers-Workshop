package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IBlockBuilder;
import moe.plushie.armourers_workshop.builder.block.AdvancedBuilderBlock;
import moe.plushie.armourers_workshop.builder.block.ArmourerBlock;
import moe.plushie.armourers_workshop.builder.block.BoundingBoxBlock;
import moe.plushie.armourers_workshop.builder.block.ColorMixerBlock;
import moe.plushie.armourers_workshop.builder.block.OutfitMakerBlock;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterialColor;
import moe.plushie.armourers_workshop.core.block.DyeTableBlock;
import moe.plushie.armourers_workshop.core.block.HologramProjectorBlock;
import moe.plushie.armourers_workshop.core.block.SkinnableBlock;
import moe.plushie.armourers_workshop.core.block.SkinningTableBlock;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.library.block.GlobalSkinLibraryBlock;
import moe.plushie.armourers_workshop.library.block.SkinLibraryBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.ToIntFunction;

@SuppressWarnings("unused")
public class ModBlocks {

    public static final IRegistryHolder<Block> HOLOGRAM_PROJECTOR = normal(HologramProjectorBlock::new).lightLevel(lit(13)).noOcclusion().strength(5f, 1200f).build("hologram-projector");
    public static final IRegistryHolder<Block> SKINNABLE = half(SkinnableBlock::new).lightLevel(lit(15)).forceSolid().noOcclusion().dynamicShape().build("skinnable");

    public static final IRegistryHolder<Block> DYE_TABLE = half(DyeTableBlock::new).build("dye-table");
    public static final IRegistryHolder<Block> SKINNING_TABLE = half(SkinningTableBlock::new).build("skinning-table");

    public static final IRegistryHolder<Block> SKIN_LIBRARY_CREATIVE = half(SkinLibraryBlock::new).build("skin-library-creative");
    public static final IRegistryHolder<Block> SKIN_LIBRARY = half(SkinLibraryBlock::new).build("skin-library");
    public static final IRegistryHolder<Block> SKIN_LIBRARY_GLOBAL = half(GlobalSkinLibraryBlock::new).build("skin-library-global");

    public static final IRegistryHolder<Block> OUTFIT_MAKER = half(OutfitMakerBlock::new).build("outfit-maker");
    public static final IRegistryHolder<Block> COLOR_MIXER = normal(ColorMixerBlock::new).bind(() -> RenderType::cutout).build("colour-mixer");
    public static final IRegistryHolder<Block> ARMOURER = normal(ArmourerBlock::new).build("armourer");
    public static final IRegistryHolder<Block> ADVANCED_SKIN_BUILDER = half(AdvancedBuilderBlock::new).build("advanced-skin-builder");

    public static final IRegistryHolder<Block> SKIN_CUBE = half(SkinCubeBlock::new).build("skin-cube");
    public static final IRegistryHolder<Block> SKIN_CUBE_GLASS = glass(SkinCubeBlock::new).build("skin-cube-glass");
    public static final IRegistryHolder<Block> SKIN_CUBE_GLOWING = half(SkinCubeBlock::new).lightLevel(15).build("skin-cube-glowing");
    public static final IRegistryHolder<Block> SKIN_CUBE_GLASS_GLOWING = glass(SkinCubeBlock::new).lightLevel(15).build("skin-cube-glass-glowing");

    public static final IRegistryHolder<Block> BOUNDING_BOX = glass(BoundingBoxBlock::new).noDrops().noCollission().build("bounding-box");

    private static ToIntFunction<BlockState> lit(int level) {
        return state -> state.getValue(SkinnableBlock.LIT) ? level : 0;
    }

    private static IBlockBuilder<Block> create(Function<BlockBehaviour.Properties, Block> supplier, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor) {
        return BuilderManager.getInstance().createBlockBuilder(supplier, material, materialColor).strength(1.5f, 6.f);
    }

    private static IBlockBuilder<Block> normal(Function<BlockBehaviour.Properties, Block> supplier) {
        return create(supplier, AbstractBlockMaterial.STONE, AbstractBlockMaterialColor.NONE);
    }

    private static IBlockBuilder<Block> half(Function<BlockBehaviour.Properties, Block> supplier) {
        return normal(supplier).noOcclusion().bind(() -> RenderType::cutout);
    }

    private static IBlockBuilder<Block> glass(Function<BlockBehaviour.Properties, Block> supplier) {
        return create(supplier, AbstractBlockMaterial.GLASS, AbstractBlockMaterialColor.NONE).noOcclusion().bind(() -> RenderType::translucent);
    }

    public static void init() {
    }
}
