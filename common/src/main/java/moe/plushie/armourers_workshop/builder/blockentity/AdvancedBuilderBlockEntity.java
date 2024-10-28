package moe.plushie.armourers_workshop.builder.blockentity;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.common.IBlockEntityHandler;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableBlockEntity;
import moe.plushie.armourers_workshop.core.data.UserNotifications;
import moe.plushie.armourers_workshop.core.math.OpenItemTransforms;
import moe.plushie.armourers_workshop.core.math.Rectangle3f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentExporter;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentImporter;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentListeners;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentProvider;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentSynchronizer;
import moe.plushie.armourers_workshop.core.skin.serializer.exception.TranslatableException;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class AdvancedBuilderBlockEntity extends UpdatableBlockEntity implements IBlockEntityHandler, SkinDocumentProvider {

    private AABB renderBoundingBox;

    public final Vector3f carmeOffset = new Vector3f();
    public final Vector3f carmeRot = new Vector3f();
    public final Vector3f carmeScale = new Vector3f(1, 1, 1);

    public Vector3f offset = new Vector3f(0, 12, 0);

    private final SkinDocument document = new SkinDocument();

    public Vector3f getRenderOrigin() {
        var pos = getBlockPos();
        return new Vector3f(
                pos.getX() + offset.getX() + 0.5f,
                pos.getY() + offset.getY() + 0.5f,
                pos.getZ() + offset.getZ() + 0.5f
        );
    }

    public AdvancedBuilderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.document.addListener(new SkinDocumentListeners.Updater(this));
    }

    @Override
    public void readAdditionalData(IDataSerializer serializer) {
        document.deserialize(serializer);
    }

    @Override
    public void writeAdditionalData(IDataSerializer serializer) {
        document.serialize(serializer);
    }

    public void importToNode(String identifier, Skin skin, SkinDocumentNode node) {
        // manual sync this changes, because it changed by server.
        var synchronizer = new SkinDocumentSynchronizer(this, false);
        document.addListener(synchronizer);
        node.setSkin(new SkinDescriptor(identifier, skin.getType()));
        document.removeListener(synchronizer);
        if (skin.getItemTransforms() != null) {
            importToSettings(skin.getItemTransforms(), node);
        }
    }

    private void importToSettings(OpenItemTransforms itemTransforms, SkinDocumentNode node) {
        var newItemTransforms = new OpenItemTransforms();
        if (document.getItemTransforms() != null) {
            newItemTransforms.putAll(document.getItemTransforms());
        }
        var overrideNames = SkinUtils.getItemOverrides(node.getType());
        if (!overrideNames.isEmpty()) {
            overrideNames.forEach(name -> itemTransforms.forEach((type, transform) -> newItemTransforms.put(name + ";" + type, transform)));
        } else {
            newItemTransforms.putAll(itemTransforms);
        }
        // manual sync this changes, because it changed by server.
        var synchronizer = new SkinDocumentSynchronizer(this, false);
        document.addListener(synchronizer);
        document.setItemTransforms(newItemTransforms);
        document.removeListener(synchronizer);
    }

    public void importToDocument(String identifier, Skin skin) {
        BlockUtils.performBatch(() -> {
            var importer = new SkinDocumentImporter(document);
            document.reset();
            document.setItemTransforms(skin.getItemTransforms());
            importer.execute(identifier, skin);
        });
    }

    public void exportFromDocument(ServerPlayer player, GameProfile profile) {
        var exporter = new SkinDocumentExporter(document);
        exporter.setItemTransforms(document.getItemTransforms());
        EnvironmentExecutor.runOnBackground(() -> () -> {
            try {
                var skin = exporter.execute(player, profile);
                player.server.execute(() -> {
                    var identifier = SkinLoader.getInstance().saveSkin("", skin);
                    var descriptor = new SkinDescriptor(identifier, skin.getType());
                    var itemStack = descriptor.asItemStack();
                    player.giveItem(itemStack);
                });
            } catch (TranslatableException exception) {
                UserNotifications.sendErrorMessage(exception.getComponent(), player);
            }
        });
    }

    @Override
    public SkinDocument getDocument() {
        return document;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AABB getRenderBoundingBox(BlockState blockState) {
        if (renderBoundingBox != null) {
            return renderBoundingBox;
        }
        var s = 16f;
        var origin = getRenderOrigin();
        var rect = new Rectangle3f(origin.getX() - s / 2, origin.getY() - s / 2, origin.getZ() - s / 2, s, s, s);
        renderBoundingBox = new AABB(rect.getMinX(), rect.getMinY(), rect.getMinZ(), rect.getMaxX(), rect.getMaxY(), rect.getMaxZ());
        return renderBoundingBox;
    }
}
