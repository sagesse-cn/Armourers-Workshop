package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.core.client.render.EntityRendererContext;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;

import java.util.LinkedHashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class SkinRendererManager {

    private static boolean IS_READY = false;

    private static final Map<IEntityTypeProvider<?>, EntityProfile> ENTITIES = new LinkedHashMap<>();


    public static void init() {
        ModEntityProfiles.addListener(SkinRendererManager::update);
        SkinRendererManager.reload();
    }

    public static void reload() {
        var entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        if (entityRenderManager == null) {
            // call again later!!!
            RenderSystem.recordRenderCall(SkinRendererManager::reload);
            return;
        }
        RenderSystem.recordRenderCall(() -> {
            // execute the pending tasks.
            IS_READY = false;
            ENTITIES.forEach(SkinRendererManager::_update);
            IS_READY = true;
        });
    }

    public static void update(IEntityTypeProvider<?> entityType, EntityProfile entityProfile) {
        if (entityProfile != null) {
            if (ENTITIES.containsKey(entityType)) {
                ModLog.debug("Reattach Entity Renderer '{}'", entityType.getRegistryName());
            } else {
                ModLog.debug("Attach Entity Renderer '{}'", entityType.getRegistryName());
            }
            ENTITIES.put(entityType, entityProfile);
        } else {
            ModLog.debug("Detach Entity Renderer '{}'", entityType.getRegistryName());
            ENTITIES.remove(entityType);
        }
        if (IS_READY) {
            RenderSystem.safeCall(() -> _update(entityType, entityProfile));
        }
    }

    private static void _update(IEntityTypeProvider<?> entityType, EntityProfile entityProfile) {
        var resolvedEntityType = entityType.get();
        if (resolvedEntityType == null) {
            return;
        }
        var entityRenderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        if (entityRenderManager == null) {
            return;
        }
        // Add our own custom armor layer to the various player renderers.
        if (resolvedEntityType == EntityType.PLAYER) {
            for (var renderer : entityRenderManager.playerRenderers.values()) {
                if (renderer != null) {
                    var storage = EntityRendererContext.of(renderer);
                    storage.setEntityType(resolvedEntityType);
                    storage.setEntityProfile(entityProfile);
                }
            }
        }
        // Add our own custom armor layer to everything that has an armor layer
        var renderer = entityRenderManager.renderers.get(resolvedEntityType);
        if (renderer != null) {
            var storage = EntityRendererContext.of(renderer);
            storage.setEntityType(resolvedEntityType);
            storage.setEntityProfile(entityProfile);
        }
    }
}
