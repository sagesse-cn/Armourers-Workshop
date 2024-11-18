package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeJS;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.VariableStorage;
import moe.plushie.armourers_workshop.core.skin.animation.molang.runtime.StaticVariableStorage;
import moe.plushie.armourers_workshop.core.utils.LazyOptional;
import moe.plushie.armourers_workshop.init.ModCapabilities;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.DataContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class EntityDataStorage {

    public static EntityImpl of(Entity entity) {
        return DataContainer.lazy(entity, EntityImpl::new);
    }

    public static BlockEntityImpl of(BlockEntity entity) {
        return DataContainer.lazy(entity, BlockEntityImpl::new);
    }

    public static class EntityImpl {

        protected final LazyOptional<SkinWardrobe> wardrobe;
        protected final LazyOptional<SkinWardrobeJS> wardrobeJS;
        protected final LazyOptional<EntityRenderData> renderData;
        protected final LazyOptional<EntityActionSet> actionSet;
        protected final LazyOptional<VariableStorage> variableStorage;

        public EntityImpl(Entity entity) {
            this.wardrobe = LazyOptional.of(() -> ModCapabilities.WARDROBE.get().get(entity));
            this.wardrobeJS = LazyOptional.of(() -> wardrobe.resolve().map(SkinWardrobeJS::new));
            this.renderData = LazyOptional.of(() -> EnvironmentExecutor.callOn(EnvironmentType.CLIENT, () -> () -> new EntityRenderData(entity)));
            this.actionSet = LazyOptional.ofNullable(EntityActionSet::new);
            this.variableStorage = LazyOptional.ofNullable(StaticVariableStorage::new);
        }

        public Optional<SkinWardrobe> getWardrobe() {
            return wardrobe.resolve();
        }

        public Optional<SkinWardrobeJS> getWardrobeJS() {
            return wardrobeJS.resolve();
        }

        @Environment(EnvType.CLIENT)
        public Optional<EntityRenderData> getRenderData() {
            return renderData.resolve();
        }

        public Optional<EntityActionSet> getActionSet() {
            return actionSet.resolve();
        }

        public Optional<VariableStorage> getVariableStorage() {
            return variableStorage.resolve();
        }
    }

    public static class BlockEntityImpl {

        protected final LazyOptional<BlockEntityRenderData> renderData;
        protected final LazyOptional<VariableStorage> variableStorage;

        public BlockEntityImpl(BlockEntity entity) {
            this.renderData = LazyOptional.of(() -> EnvironmentExecutor.callOn(EnvironmentType.CLIENT, () -> () -> new BlockEntityRenderData(entity)));
            this.variableStorage = LazyOptional.ofNullable(StaticVariableStorage::new);
        }

        @Environment(EnvType.CLIENT)
        public Optional<BlockEntityRenderData> getRenderData() {
            return renderData.resolve();
        }

        public Optional<VariableStorage> getVariableStorage() {
            return variableStorage.resolve();
        }
    }
}
