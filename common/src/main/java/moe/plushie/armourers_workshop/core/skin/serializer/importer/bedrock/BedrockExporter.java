package moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock;


import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.OptimizeContext;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleData;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleFacing;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleMaterial;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.EmitterInitialLocalSpace;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.EmitterInitialization;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime.EmitterEventLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime.EmitterExpressionLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime.EmitterLoopingLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime.EmitterOnceLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate.EmitterInstantRate;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate.EmitterManualRate;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate.EmitterSteadyRate;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterBoxShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterDiscShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterEntityShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterPointShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterShapeDirection;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterSphereShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.ParticleInitialSpeed;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.ParticleInitialSpin;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.ParticleInitialization;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance.ParticleBillboardAppearance;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance.ParticleLightingAppearance;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance.ParticleTintingAppearance;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime.ParticleEventLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime.ParticleExpressLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime.ParticleKillInBlocksLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime.ParticleKillInPlaneLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime.ParticleOnlyInBlocksLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion.ParticleCollisionMotion;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion.ParticleDynamicMotion;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion.ParticleParametricMotion;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.utils.OpenExpression;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import moe.plushie.armourers_workshop.init.ModConstants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class BedrockExporter {

    protected final BedrockPack pack;
    protected final MolangVirtualMachine virtualMachine;

    public BedrockExporter(BedrockPack pack, MolangVirtualMachine virtualMachine) {
        this.pack = pack;
        this.virtualMachine = virtualMachine;
    }


    public SkinParticleData exportParticle(BedrockParticle particle) {
        var name = particle.getName();
        var meterial = toParticleMaterial(particle.getMaterial());
        var texutre = toParticleTexture(particle.getTexture());

        var components = new ArrayList<SkinParticleComponent>();
        for (var component : particle.getComponents().values()) {
            components.add(exportParticleComponent(component));
        }

        // TODO: no impl @SAGESSE
        //particle.getCurves();
        //particle.getEvents();

        return new SkinParticleData(name, meterial, texutre, components);
    }

    protected SkinParticleComponent exportParticleComponent(BedrockComponent component) {

        if (component instanceof BedrockComponent.EmitterInitialization comp) {
            var creation = convertToExpression(comp.getCreation());
            var update = convertToExpression(comp.getUpdate());
            return new EmitterInitialization(creation, update);
        }
        if (component instanceof BedrockComponent.EmitterLocalSpace comp) {
            var position = comp.isPosition();
            var rotation = comp.isRotation();
            var velocity = comp.isVelocity();
            return new EmitterInitialLocalSpace(position, rotation, velocity);
        }

        if (component instanceof BedrockComponent.EmitterSteadyRate comp) {
            var spawnRate = convertToFloatExpression(comp.getSpawnRate());
            var maxParticles = convertToIntExpression(comp.getMaxParticles());
            return new EmitterSteadyRate(spawnRate, maxParticles);
        }
        if (component instanceof BedrockComponent.EmitterInstantRate comp) {
            var particles = convertToIntExpression(comp.getParticles());
            return new EmitterInstantRate(particles);
        }
        if (component instanceof BedrockComponent.EmitterManualRate comp) {
            var maxParticles = convertToIntExpression(comp.getMaxParticles());
            return new EmitterManualRate(maxParticles);
        }


        if (component instanceof BedrockComponent.EmitterEventLifetime comp) {
            var creation = comp.getCreation();
            var expiration = comp.getExpiration();
            var timelineEvents = new LinkedHashMap<Float, List<String>>();
            var travelDistanceEvents = new LinkedHashMap<Float, List<String>>();
            var travelDistanceLoopEvents = comp.getTravelDistanceLoopEvents();
            comp.getTimelineEvents().forEach((key, value) -> timelineEvents.put(Float.parseFloat(key), value));
            comp.getTravelDistanceEvents().forEach((key, value) -> travelDistanceEvents.put(Float.parseFloat(key), value));
            return new EmitterEventLifetime(creation, expiration, timelineEvents, travelDistanceEvents, travelDistanceLoopEvents);
        }
        if (component instanceof BedrockComponent.EmitterExpressionLifetime comp) {
            var activation = convertToExpression(comp.getActivation());
            var expiration = convertToExpression(comp.getExpiration());
            return new EmitterExpressionLifetime(activation, expiration);
        }
        if (component instanceof BedrockComponent.EmitterLoopingLifetime comp) {
            var activeTime = convertToFloatExpression(comp.getActiveTime());
            var sleepTime = convertToFloatExpression(comp.getSleepTime());
            return new EmitterLoopingLifetime(activeTime, sleepTime);
        }
        if (component instanceof BedrockComponent.EmitterOnceLifetime comp) {
            var activeTime = convertToFloatExpression(comp.getActiveTime());
            return new EmitterOnceLifetime(activeTime);
        }

        if (component instanceof BedrockComponent.EmitterBoxShape comp) {
            var offsetX = convertToFloatExpression(comp.getOffsetX());
            var offsetY = convertToFloatExpression(comp.getOffsetY());
            var offsetZ = convertToFloatExpression(comp.getOffsetZ());
            var width = convertToFloatExpression(comp.getSizeWidth());
            var height = convertToFloatExpression(comp.getSizeHeight());
            var depth = convertToFloatExpression(comp.getSizeDepth());
            var surfaceOnly = comp.isSurfaceOnly();
            var direction = toParticleDirection(comp.getDirection());
            return new EmitterBoxShape(offsetX, offsetY, offsetZ, width, height, depth, direction, surfaceOnly);
        }
        if (component instanceof BedrockComponent.EmitterDiscShape comp) {
            var offsetX = convertToFloatExpression(comp.getOffsetX());
            var offsetY = convertToFloatExpression(comp.getOffsetY());
            var offsetZ = convertToFloatExpression(comp.getOffsetZ());
            var radius = convertToFloatExpression(comp.getRadius());
            var planeNormalX = convertToFloatExpression(comp.getPlaneNormalX());
            var planeNormalY = convertToFloatExpression(comp.getPlaneNormalY());
            var planeNormalZ = convertToFloatExpression(comp.getPlaneNormalZ());
            var surfaceOnly = comp.isSurfaceOnly();
            var direction = toParticleDirection(comp.getDirection());
            return new EmitterDiscShape(offsetX, offsetY, offsetZ, radius, planeNormalX, planeNormalY, planeNormalZ, direction, surfaceOnly);
        }
        if (component instanceof BedrockComponent.EmitterEntityShape comp) {
            var offsetX = convertToFloatExpression(comp.getOffsetX());
            var offsetY = convertToFloatExpression(comp.getOffsetY());
            var offsetZ = convertToFloatExpression(comp.getOffsetZ());
            var surfaceOnly = comp.isSurfaceOnly();
            var direction = toParticleDirection(comp.getDirection());
            return new EmitterEntityShape(offsetX, offsetY, offsetZ, direction, surfaceOnly);
        }
        if (component instanceof BedrockComponent.EmitterPointShape comp) {
            var offsetX = convertToFloatExpression(comp.getOffsetX());
            var offsetY = convertToFloatExpression(comp.getOffsetY());
            var offsetZ = convertToFloatExpression(comp.getOffsetZ());
            var direction = toParticleDirection(comp.getDirection());
            return new EmitterPointShape(offsetX, offsetY, offsetZ, direction);
        }
        if (component instanceof BedrockComponent.EmitterSphereShape comp) {
            var offsetX = convertToFloatExpression(comp.getOffsetX());
            var offsetY = convertToFloatExpression(comp.getOffsetY());
            var offsetZ = convertToFloatExpression(comp.getOffsetZ());
            var radius = convertToFloatExpression(comp.getRadius());
            var surfaceOnly = comp.isSurfaceOnly();
            var direction = toParticleDirection(comp.getDirection());
            return new EmitterSphereShape(offsetX, offsetY, offsetZ, radius, direction, surfaceOnly);
        }

        if (component instanceof BedrockComponent.ParticleInitialization comp) {
            var update = convertToExpression(comp.getUpdate());
            var render = convertToExpression(comp.getRender());
            return new ParticleInitialization(update, render);
        }
        if (component instanceof BedrockComponent.ParticleInitialSpeed comp) {
            var speed = convertToFloatExpression(comp.getSpeed());
            return new ParticleInitialSpeed(speed);
        }
        if (component instanceof BedrockComponent.ParticleInitialSpin comp) {
            var rotation = convertToFloatExpression(comp.getRotation());
            var rotationRate = convertToFloatExpression(comp.getRotationRate());
            return new ParticleInitialSpin(rotation, rotationRate);
        }

        if (component instanceof BedrockComponent.ParticleEventLifetime comp) {
            var creation = comp.getCreation();
            var expiration = comp.getExpiration();
            var timelineEvents = new LinkedHashMap<Float, List<String>>();
            comp.getTimelineEvents().forEach((key, value) -> timelineEvents.put(Float.parseFloat(key), value));
            return new ParticleEventLifetime(creation, expiration, timelineEvents);
        }
        if (component instanceof BedrockComponent.ParticleExpressLifetime comp) {
            var maxAge = convertToIntExpression(comp.getMaxAge());
            var expiration = convertToExpression(comp.getExpiration());
            return new ParticleExpressLifetime(maxAge, expiration);
        }
        if (component instanceof BedrockComponent.ParticleKillInBlocksLifetime comp) {
            var blocks = comp.getBlocks();
            return new ParticleKillInBlocksLifetime(blocks);
        }
        if (component instanceof BedrockComponent.ParticleKillInPlaneLifetime comp) {
            float a = comp.getParameters().get(0);
            float b = comp.getParameters().get(1);
            float c = comp.getParameters().get(2);
            float d = comp.getParameters().get(3);
            return new ParticleKillInPlaneLifetime(a, b, c, d);
        }
        if (component instanceof BedrockComponent.ParticleOnlyInBlocksLifetime comp) {
            var blocks = comp.getBlocks();
            return new ParticleOnlyInBlocksLifetime(blocks);
        }

        if (component instanceof BedrockComponent.ParticleCollisionMotion comp) {
            var enabled = convertToFloatExpression(comp.getEnabled());
            var collisionDrag = comp.getCollisionDrag();
            var collisionRadius = comp.getCollisionRadius();
            var coefficientOfRestitution = comp.getCoefficientOfRestitution();
            var expireOnContact = comp.isExpireOnContact();
            var events = comp.getEvents();
            return new ParticleCollisionMotion(enabled, collisionDrag, collisionRadius, coefficientOfRestitution, expireOnContact, events);
        }
        if (component instanceof BedrockComponent.ParticleDynamicMotion comp) {
            var linearAccelerationX = convertToFloatExpression(comp.getLinearAccelerationX());
            var linearAccelerationY = convertToFloatExpression(comp.getLinearAccelerationY());
            var linearAccelerationZ = convertToFloatExpression(comp.getLinearAccelerationZ());
            var linearDragCoefficient = convertToFloatExpression(comp.getLinearDragCoefficient());
            var rotationAcceleration = convertToFloatExpression(comp.getRotationAcceleration());
            var rotationDragCoefficient = convertToFloatExpression(comp.getRotationDragCoefficient());
            return new ParticleDynamicMotion(linearAccelerationX, linearAccelerationY, linearAccelerationZ, linearDragCoefficient, rotationAcceleration, rotationDragCoefficient);
        }
        if (component instanceof BedrockComponent.ParticleParametricMotion comp) {
            var relativePositionX = convertToFloatExpression(comp.getRelativePositionX());
            var relativePositionY = convertToFloatExpression(comp.getRelativePositionY());
            var relativePositionZ = convertToFloatExpression(comp.getRelativePositionZ());
            var directionX = convertToFloatExpression(comp.getDirectionX());
            var directionY = convertToFloatExpression(comp.getDirectionY());
            var directionZ = convertToFloatExpression(comp.getDirectionZ());
            var rotation = convertToFloatExpression(comp.getRotation());
            return new ParticleParametricMotion(relativePositionX, relativePositionY, relativePositionZ, directionX, directionY, directionZ, rotation);
        }

        if (component instanceof BedrockComponent.ParticleLightingAppearance comp) {
            return new ParticleLightingAppearance();
        }
        if (component instanceof BedrockComponent.ParticleBillboardAppearance comp) {
            var width = convertToFloatExpression(comp.getWidth());
            var height = convertToFloatExpression(comp.getHeight());
            var facingCameraMode = toParticleFacing(comp.getFacingCameraMode());
            var textureSize = comp.getTextureSize();
            var textureCoordsX = convertToFloatExpression(comp.getTextureCoordsX());
            var textureCoordsY = convertToFloatExpression(comp.getTextureCoordsY());
            var textureCoordsWidth = convertToFloatExpression(comp.getTextureCoordsWidth());
            var textureCoordsHeight = convertToFloatExpression(comp.getTextureCoordsHeight());
            var stepX = convertToFloatExpression(comp.getStepX());
            var stepY = convertToFloatExpression(comp.getStepY());
            var isUseAnimation = comp.isUseAnimation();
            var fps = comp.getFps();
            var maxFrame = convertToIntExpression(comp.getMaxFrame());
            var isStretchToLifetime = comp.isStretchToLifetime();
            var isLoop = comp.isLoop();
            return new ParticleBillboardAppearance(width, height, facingCameraMode, textureSize, textureCoordsX, textureCoordsY, textureCoordsWidth, textureCoordsHeight, stepX, stepY, isUseAnimation, fps, maxFrame, isStretchToLifetime, isLoop);
        }
        if (component instanceof BedrockComponent.ParticleTintingAppearance comp) {
            var colors = comp.getValues();
            if (!colors.isEmpty()) {
                var red = convertToFloatExpression(colors.get(0));
                var green = convertToFloatExpression(colors.get(1));
                var blue = convertToFloatExpression(colors.get(2));
                var alpha = convertToFloatExpression(colors.get(3));
                return new ParticleTintingAppearance(red, green, blue, alpha);
            }
            var interpolation = convertToFloatExpression(comp.getInterpolation());
            var gradientColors = new LinkedHashMap<Float, Integer>();
            comp.getGradientValues().forEach((key, value) -> gradientColors.put(Float.parseFloat(key), Integer.parseInt(value)));
            return new ParticleTintingAppearance(interpolation, gradientColors);
        }

        throw new RuntimeException("can't parse particle component!!");
    }

    private SkinParticleMaterial toParticleMaterial(String material) {
        return switch (material) {
            case "particles_alpha" -> SkinParticleMaterial.ALPHA;
            case "particles_add" -> SkinParticleMaterial.ADDITIVE;
            case "particles_blend" -> SkinParticleMaterial.BLEND;
            case "particles_opaque" -> SkinParticleMaterial.OPAQUE;
            default -> throw new IllegalArgumentException("Unknown particle material: " + material);
        };
    }

    private SkinTextureData toParticleTexture(String texture) {
        return switch (texture) {
            case "textures/particle/particles" -> createBuiltinTexture("textures/particle/particles", 128, 128);
            case "textures/particle/campfire_smoke" -> createBuiltinTexture("textures/particle/campfire_smoke", 16, 192);
            case "textures/particle/flame_atlas" -> createBuiltinTexture("textures/particle/flame_atlas", 16, 512);
            case "textures/particle/soul" -> createBuiltinTexture("textures/particle/soul", 16, 176);
            default -> throw new IllegalArgumentException("Unknown particle texture: " + texture);
        };
    }

    private EmitterShapeDirection toParticleDirection(Object direction) {
        if (Objects.equals(direction, "inwards")) {
            return EmitterShapeDirection.outwards();
        }
        if (Objects.equals(direction, "outwards")) {
            return EmitterShapeDirection.outwards();
        }
        if (direction instanceof List<?> value) {
            var x = convertToFloatExpression((OpenExpression) value.get(0));
            var y = convertToFloatExpression((OpenExpression) value.get(1));
            var z = convertToFloatExpression((OpenExpression) value.get(2));
            return EmitterShapeDirection.custom(x, y, z);
        }
        throw new IllegalArgumentException("unknown particle shape direction: " + direction);
    }

    private SkinParticleFacing toParticleFacing(String name) {
        return switch (name) {
            case "rotate_xyz" -> SkinParticleFacing.ROTATE_XYZ;
            case "rotate_y" -> SkinParticleFacing.ROTATE_Y;
            case "lookat_xyz" -> SkinParticleFacing.LOOKAT_XYZ;
            case "lookat_y" -> SkinParticleFacing.LOOKAT_Y;
            case "lookat_direction" -> SkinParticleFacing.LOOKAT_DIRECTION;
            case "direction_x" -> SkinParticleFacing.DIRECTION_X;
            case "direction_y" -> SkinParticleFacing.DIRECTION_Y;
            case "direction_z" -> SkinParticleFacing.DIRECTION_Z;
            case "emitter_transform_xy" -> SkinParticleFacing.EMITTER_TRANSFORM_XY;
            case "emitter_transform_xz" -> SkinParticleFacing.EMITTER_TRANSFORM_XZ;
            case "emitter_transform_yz" -> SkinParticleFacing.EMITTER_TRANSFORM_YZ;
            default -> SkinParticleFacing.ROTATE_XYZ; // unknown.
        };
    }


    private OpenPrimitive convertToExpression(OpenExpression value) {
        var expr = compileExpression(value);
        if (expr != null && expr.isMutable()) {
            return OpenPrimitive.of(value.getExpression());
        }
        return OpenPrimitive.NULL;
    }

    private OpenPrimitive convertToIntExpression(OpenExpression value) {
        var expr = compileExpression(value);
        if (expr != null && expr.isMutable()) {
            return OpenPrimitive.of(value.getExpression());
        }
        if (expr != null) {
            return OpenPrimitive.of(expr.evaluate(OptimizeContext.DEFAULT).getAsInt());
        }
        return OpenPrimitive.NULL;
    }

    private OpenPrimitive convertToFloatExpression(OpenExpression value) {
        var expr = compileExpression(value);
        if (expr != null && expr.isMutable()) {
            return OpenPrimitive.of(value.getExpression());
        }
        if (expr != null) {
            return OpenPrimitive.of((float) expr.compute(OptimizeContext.DEFAULT));
        }
        return OpenPrimitive.NULL;
    }

    private Expression compileExpression(OpenExpression value) {
        try {
            if (value == null) {
                return null;
            }
            var expr = value.getExpression();
            if (!expr.isEmpty()) {
                return virtualMachine.compile(value.getExpression());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private SkinTextureData createBuiltinTexture(String texture, int width, int height) {
        return new SkinTextureData(ModConstants.key(texture).toString(), width, height);
    }
}
