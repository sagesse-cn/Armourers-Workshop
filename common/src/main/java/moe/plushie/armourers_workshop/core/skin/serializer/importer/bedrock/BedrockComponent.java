package moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock;

import moe.plushie.armourers_workshop.core.math.Size2i;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.MolangExpression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BedrockComponent {

    protected static class EmitterInitialization extends BedrockComponent {

        private final MolangExpression creation;
        private final MolangExpression update;

        public EmitterInitialization(MolangExpression creation, MolangExpression update) {
            this.creation = creation;
            this.update = update;
        }

        public MolangExpression getCreation() {
            return creation;
        }

        public MolangExpression getUpdate() {
            return update;
        }

        protected static class Builder extends BedrockComponent.Builder {

            private MolangExpression creation;
            private MolangExpression update;

            public void creation(MolangExpression creation) {
                this.creation = creation;
            }

            public void update(MolangExpression update) {
                this.update = update;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterInitialization(creation, update);
            }
        }
    }

    protected static class EmitterLocalSpace extends BedrockComponent {

        private final boolean position;
        private final boolean rotation;
        private final boolean velocity;

        public EmitterLocalSpace(boolean position, boolean rotation, boolean velocity) {
            this.position = position;
            this.rotation = rotation;
            this.velocity = velocity;
        }

        public boolean isPosition() {
            return position;
        }

        public boolean isRotation() {
            return rotation;
        }

        public boolean isVelocity() {
            return velocity;
        }

        protected static class Builder extends BedrockComponent.Builder {

            boolean position = false;
            boolean rotation = false;
            boolean velocity = false;

            public void position(boolean position) {
                this.position = position;
            }

            public void rotation(boolean rotation) {
                this.rotation = rotation;
            }

            public void velocity(boolean velocity) {
                this.velocity = velocity;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterLocalSpace(position, rotation, velocity);
            }
        }
    }

    protected static class EmitterSteadyRate extends BedrockComponent {

        private final MolangExpression spawnRate;
        private final MolangExpression maxParticles;

        public EmitterSteadyRate(MolangExpression spawnRate, MolangExpression maxParticles) {
            this.spawnRate = spawnRate;
            this.maxParticles = maxParticles;
        }

        public MolangExpression getSpawnRate() {
            return spawnRate;
        }

        public MolangExpression getMaxParticles() {
            return maxParticles;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression spawnRate;
            MolangExpression maxParticles;

            public void spawnRate(MolangExpression spawnRate) {
                this.spawnRate = spawnRate;
            }

            public void maxParticles(MolangExpression maxParticles) {
                this.maxParticles = maxParticles;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterSteadyRate(spawnRate, maxParticles);
            }
        }
    }

    protected static class EmitterInstantRate extends BedrockComponent {

        private final MolangExpression particles;

        public EmitterInstantRate(MolangExpression particles) {
            this.particles = particles;
        }

        public MolangExpression getParticles() {
            return particles;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression particles;

            public void particles(MolangExpression particles) {
                this.particles = particles;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterInstantRate(particles);
            }
        }
    }

    protected static class EmitterManualRate extends BedrockComponent {

        private final MolangExpression maxParticles;

        public EmitterManualRate(MolangExpression maxParticles) {
            this.maxParticles = maxParticles;
        }

        public MolangExpression getMaxParticles() {
            return maxParticles;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression maxParticles;

            public void maxParticles(MolangExpression maxParticles) {
                this.maxParticles = maxParticles;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterManualRate(maxParticles);
            }
        }
    }

    protected static class EmitterEventLifetime extends BedrockComponent {

        private final List<String> creation;
        private final List<String> expiration;

        private final Map<String, List<String>> timelineEvents;
        private final Map<String, List<String>> travelDistanceEvents;
        private final Map<Float, List<String>> travelDistanceLoopEvents;

        public List<String> getCreation() {
            return creation;
        }

        public List<String> getExpiration() {
            return expiration;
        }

        public Map<String, List<String>> getTimelineEvents() {
            return timelineEvents;
        }

        public Map<String, List<String>> getTravelDistanceEvents() {
            return travelDistanceEvents;
        }

        public Map<Float, List<String>> getTravelDistanceLoopEvents() {
            return travelDistanceLoopEvents;
        }

        public EmitterEventLifetime(List<String> creation, List<String> expiration, Map<String, List<String>> timelineEvents, Map<String, List<String>> travelDistanceEvents, Map<Float, List<String>> travelDistanceLoopEvents) {
            this.creation = creation;
            this.expiration = expiration;
            this.timelineEvents = timelineEvents;
            this.travelDistanceEvents = travelDistanceEvents;
            this.travelDistanceLoopEvents = travelDistanceLoopEvents;
        }

        protected static class Builder extends BedrockComponent.Builder {

            List<String> creation = Collections.emptyList();
            List<String> expiration = Collections.emptyList();

            Map<String, List<String>> timelineEvents = new LinkedHashMap<>();
            Map<String, List<String>> travelDistanceEvents = new LinkedHashMap<>();
            Map<Float, List<String>> travelDistanceLoopEvents = new LinkedHashMap<>();


            public void creation(List<String> creation) {
                this.creation = creation;
            }

            public void expiration(List<String> expiration) {
                this.creation = expiration;
            }

            public void timeline(String atTime, List<String> events) {
                this.timelineEvents.put(atTime, events);
            }

            public void travelDistance(String atTime, List<String> events) {
                this.travelDistanceEvents.put(atTime, events);
            }

            public void travelDistanceLoop(float atTime, List<String> events) {
                this.travelDistanceLoopEvents.put(atTime, events);
            }

            @Override
            public BedrockComponent build() {
                return new EmitterEventLifetime(creation, expiration, timelineEvents, travelDistanceEvents, travelDistanceLoopEvents);
            }
        }
    }

    protected static class EmitterLoopingLifetime extends BedrockComponent {

        private final MolangExpression activeTime;
        private final MolangExpression sleepTime;

        public EmitterLoopingLifetime(MolangExpression activeTime, MolangExpression sleepTime) {
            this.activeTime = activeTime;
            this.sleepTime = sleepTime;
        }

        public MolangExpression getActiveTime() {
            return activeTime;
        }

        public MolangExpression getSleepTime() {
            return sleepTime;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression activeTime;
            MolangExpression sleepTime;

            public void activeTime(MolangExpression activeTime) {
                this.activeTime = activeTime;
            }

            public void sleepTime(MolangExpression sleepTime) {
                this.sleepTime = sleepTime;
            }


            @Override
            public BedrockComponent build() {
                return new EmitterLoopingLifetime(activeTime, sleepTime);
            }
        }
    }

    protected static class EmitterOnceLifetime extends BedrockComponent {

        private final MolangExpression activeTime;

        public EmitterOnceLifetime(MolangExpression activeTime) {
            this.activeTime = activeTime;
        }

        public MolangExpression getActiveTime() {
            return activeTime;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression activeTime;

            public void activeTime(MolangExpression activeTime) {
                this.activeTime = activeTime;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterOnceLifetime(activeTime);
            }
        }
    }

    protected static class EmitterExpressionLifetime extends BedrockComponent {

        private final MolangExpression activation;
        private final MolangExpression expiration;

        public EmitterExpressionLifetime(MolangExpression activation, MolangExpression expiration) {
            this.activation = activation;
            this.expiration = expiration;
        }

        public MolangExpression getActivation() {
            return activation;
        }

        public MolangExpression getExpiration() {
            return expiration;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression activation;
            MolangExpression expiration;

            public void activation(MolangExpression activation) {
                this.activation = activation;
            }

            public void expiration(MolangExpression expiration) {
                this.expiration = expiration;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterExpressionLifetime(activation, expiration);
            }
        }
    }

    protected static class EmitterPointShape extends BedrockComponent {

        private final MolangExpression offsetX;
        private final MolangExpression offsetY;
        private final MolangExpression offsetZ;

        public EmitterPointShape(MolangExpression offsetX, MolangExpression offsetY, MolangExpression offsetZ) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
        }

        public MolangExpression getOffsetX() {
            return offsetX;
        }

        public MolangExpression getOffsetY() {
            return offsetY;
        }

        public MolangExpression getOffsetZ() {
            return offsetZ;
        }

        protected static class Builder extends ShapeBuilder {

            @Override
            public BedrockComponent build() {
                return new EmitterPointShape(offsetX, offsetY, offsetZ);
            }
        }
    }

    protected static class EmitterSphereShape extends BedrockComponent {

        private final MolangExpression offsetX;
        private final MolangExpression offsetY;
        private final MolangExpression offsetZ;

        private final MolangExpression radius;
        private final boolean surfaceOnly;
        private final Object direction;

        public EmitterSphereShape(MolangExpression offsetX, MolangExpression offsetY, MolangExpression offsetZ, MolangExpression radius, boolean surfaceOnly, Object direction) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.radius = radius;
            this.surfaceOnly = surfaceOnly;
            this.direction = direction;
        }

        public MolangExpression getOffsetX() {
            return offsetX;
        }

        public MolangExpression getOffsetY() {
            return offsetY;
        }

        public MolangExpression getOffsetZ() {
            return offsetZ;
        }

        public MolangExpression getRadius() {
            return radius;
        }

        public boolean isSurfaceOnly() {
            return surfaceOnly;
        }

        public Object getDirection() {
            return direction;
        }

        protected static class Builder extends ShapeBuilder {

            @Override
            public BedrockComponent build() {
                return new EmitterSphereShape(offsetX, offsetY, offsetZ, radius, surfaceOnly, direction);
            }
        }
    }

    protected static class EmitterBoxShape extends BedrockComponent {

        private final MolangExpression offsetX;
        private final MolangExpression offsetY;
        private final MolangExpression offsetZ;

        private final MolangExpression sizeWidth;
        private final MolangExpression sizeHeight;
        private final MolangExpression sizeDepth;

        private final boolean surfaceOnly;
        private final Object direction;

        public EmitterBoxShape(MolangExpression offsetX, MolangExpression offsetY, MolangExpression offsetZ, MolangExpression sizeWidth, MolangExpression sizeHeight, MolangExpression sizeDepth, boolean surfaceOnly, Object direction) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.sizeWidth = sizeWidth;
            this.sizeHeight = sizeHeight;
            this.sizeDepth = sizeDepth;
            this.surfaceOnly = surfaceOnly;
            this.direction = direction;
        }

        public MolangExpression getOffsetX() {
            return offsetX;
        }

        public MolangExpression getOffsetY() {
            return offsetY;
        }

        public MolangExpression getOffsetZ() {
            return offsetZ;
        }

        public MolangExpression getSizeWidth() {
            return sizeWidth;
        }

        public MolangExpression getSizeHeight() {
            return sizeHeight;
        }

        public MolangExpression getSizeDepth() {
            return sizeDepth;
        }

        public boolean isSurfaceOnly() {
            return surfaceOnly;
        }

        public Object getDirection() {
            return direction;
        }

        protected static class Builder extends ShapeBuilder {

            private MolangExpression sizeWidth;
            private MolangExpression sizeHeight;
            private MolangExpression sizeDepth;

            public void width(MolangExpression sizeWidth) {
                this.sizeWidth = sizeWidth;
            }

            public void height(MolangExpression sizeHeight) {
                this.sizeHeight = sizeHeight;
            }

            public void depth(MolangExpression sizeDepth) {
                this.sizeDepth = sizeDepth;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterBoxShape(offsetX, offsetY, offsetZ, sizeWidth, sizeHeight, sizeDepth, surfaceOnly, direction);
            }
        }
    }

    protected static class EmitterDiscShape extends BedrockComponent {

        private final MolangExpression offsetX;
        private final MolangExpression offsetY;
        private final MolangExpression offsetZ;

        private final MolangExpression radius;

        private final MolangExpression planeNormalX;
        private final MolangExpression planeNormalY;
        private final MolangExpression planeNormalZ;

        private final boolean surfaceOnly;
        private final Object direction;

        public EmitterDiscShape(MolangExpression offsetX, MolangExpression offsetY, MolangExpression offsetZ, MolangExpression radius, MolangExpression planeNormalX, MolangExpression planeNormalY, MolangExpression planeNormalZ, boolean surfaceOnly, Object direction) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.radius = radius;
            this.planeNormalX = planeNormalX;
            this.planeNormalY = planeNormalY;
            this.planeNormalZ = planeNormalZ;
            this.surfaceOnly = surfaceOnly;
            this.direction = direction;
        }

        public MolangExpression getOffsetX() {
            return offsetX;
        }

        public MolangExpression getOffsetY() {
            return offsetY;
        }

        public MolangExpression getOffsetZ() {
            return offsetZ;
        }

        public MolangExpression getRadius() {
            return radius;
        }

        public MolangExpression getPlaneNormalX() {
            return planeNormalX;
        }

        public MolangExpression getPlaneNormalY() {
            return planeNormalY;
        }

        public MolangExpression getPlaneNormalZ() {
            return planeNormalZ;
        }

        public boolean isSurfaceOnly() {
            return surfaceOnly;
        }

        public Object getDirection() {
            return direction;
        }

        protected static class Builder extends ShapeBuilder {

            private MolangExpression planeNormalX;
            private MolangExpression planeNormalY;
            private MolangExpression planeNormalZ;

            public void planeNormalX(MolangExpression planeNormalX) {
                this.planeNormalX = planeNormalX;
            }

            public void planeNormalY(MolangExpression planeNormalY) {
                this.planeNormalY = planeNormalY;
            }

            public void planeNormalZ(MolangExpression planeNormalZ) {
                this.planeNormalZ = planeNormalZ;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterDiscShape(offsetX, offsetY, offsetZ, radius, planeNormalX, planeNormalY, planeNormalZ, surfaceOnly, direction);
            }
        }
    }

    protected static class EmitterEntityShape extends BedrockComponent {

        private final boolean surfaceOnly;
        private final Object direction;

        public EmitterEntityShape(boolean surfaceOnly, Object direction) {
            this.surfaceOnly = surfaceOnly;
            this.direction = direction;
        }

        public boolean isSurfaceOnly() {
            return surfaceOnly;
        }

        public Object getDirection() {
            return direction;
        }

        protected static class Builder extends ShapeBuilder {

            @Override
            public BedrockComponent build() {
                return new EmitterEntityShape(surfaceOnly, direction);
            }
        }
    }

    protected static class ParticleInitialization extends BedrockComponent {

        private final MolangExpression update;
        private final MolangExpression render;

        public ParticleInitialization(MolangExpression update, MolangExpression render) {
            this.update = update;
            this.render = render;
        }

        public MolangExpression getUpdate() {
            return update;
        }

        public MolangExpression getRender() {
            return render;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression update;
            MolangExpression render;

            public void update(MolangExpression update) {
                this.update = update;
            }

            public void render(MolangExpression render) {
                this.render = render;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleInitialization(update, render);
            }
        }
    }

    protected static class ParticleInitialSpeed extends BedrockComponent {

        private final MolangExpression speed;

        public ParticleInitialSpeed(MolangExpression speed) {
            this.speed = speed;
        }

        public MolangExpression getSpeed() {
            return speed;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression speed;

            public void speed(MolangExpression speed) {
                this.speed = speed;
            }


            @Override
            public BedrockComponent build() {
                return new ParticleInitialSpeed(speed);
            }
        }

    }

    protected static class ParticleInitialSpin extends BedrockComponent {

        private final MolangExpression rotation;
        private final MolangExpression rotationRate;

        public ParticleInitialSpin(MolangExpression rotation, MolangExpression rotationRate) {
            this.rotation = rotation;
            this.rotationRate = rotationRate;
        }

        public MolangExpression getRotation() {
            return rotation;
        }

        public MolangExpression getRotationRate() {
            return rotationRate;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression rotation;
            MolangExpression rotationRate;

            public void rotation(MolangExpression rotation) {
                this.rotation = rotation;
            }

            public void rotationRate(MolangExpression rotationRate) {
                this.rotationRate = rotationRate;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleInitialSpin(rotation, rotationRate);
            }
        }

    }

    protected static class ParticleEventLifetime extends BedrockComponent {
        private final List<String> creation;
        private final List<String> expiration;
        private final Map<String, List<String>> timelineEvents;

        public ParticleEventLifetime(List<String> creation, List<String> expiration, Map<String, List<String>> timelineEvents) {
            this.creation = creation;
            this.expiration = expiration;
            this.timelineEvents = timelineEvents;
        }

        public List<String> getCreation() {
            return creation;
        }

        public List<String> getExpiration() {
            return expiration;
        }

        public Map<String, List<String>> getTimelineEvents() {
            return timelineEvents;
        }

        protected static class Builder extends BedrockComponent.Builder {

            List<String> creation = Collections.emptyList();
            List<String> expiration = Collections.emptyList();
            Map<String, List<String>> timelineEvents = new LinkedHashMap<>();

            public void creation(List<String> creation) {
                this.creation = creation;
            }

            public void expiration(List<String> expiration) {
                this.creation = expiration;
            }

            public void timeline(String atTime, List<String> events) {
                this.timelineEvents.put(atTime, events);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleEventLifetime(creation, expiration, timelineEvents);
            }
        }
    }

    protected static class ParticleExpressLifetime extends BedrockComponent {

        private final MolangExpression maxAge;
        private final MolangExpression expiration;

        public ParticleExpressLifetime(MolangExpression maxAge, MolangExpression expiration) {
            this.maxAge = maxAge;
            this.expiration = expiration;
        }

        public MolangExpression getMaxAge() {
            return maxAge;
        }

        public MolangExpression getExpiration() {
            return expiration;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression maxAge;
            MolangExpression expiration;

            public void maxAge(MolangExpression maxAge) {
                this.maxAge = maxAge;
            }

            public void expiration(MolangExpression expiration) {
                this.expiration = expiration;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleExpressLifetime(maxAge, expiration);
            }
        }
    }

    protected static class ParticleKillInPlaneLifetime extends BedrockComponent {

        private final List<Float> parameters;

        public ParticleKillInPlaneLifetime(List<Float> parameters) {
            this.parameters = parameters;
        }

        public List<Float> getParameters() {
            return parameters;
        }

        protected static class Builder extends BedrockComponent.Builder {

            List<Float> parameters = new ArrayList<>();

            public void add(float value) {
                this.parameters.add(value);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleKillInPlaneLifetime(parameters);
            }
        }
    }

    protected static class ParticleKillInBlocksLifetime extends BedrockComponent {

        private final List<String> blocks;

        public ParticleKillInBlocksLifetime(List<String> blocks) {
            this.blocks = blocks;
        }

        public List<String> getBlocks() {
            return blocks;
        }

        protected static class Builder extends BedrockComponent.Builder {

            List<String> blocks = new ArrayList<>();

            public void add(String block) {
                this.blocks.add(block);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleKillInBlocksLifetime(blocks);
            }
        }
    }

    protected static class ParticleOnlyInBlocksLifetime extends BedrockComponent {

        private final List<String> blocks;

        public ParticleOnlyInBlocksLifetime(List<String> blocks) {
            this.blocks = blocks;
        }

        public List<String> getBlocks() {
            return blocks;
        }

        protected static class Builder extends BedrockComponent.Builder {

            List<String> blocks = new ArrayList<>();

            public void add(String block) {
                this.blocks.add(block);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleOnlyInBlocksLifetime(blocks);
            }
        }
    }

    protected static class ParticleCollisionMotion extends BedrockComponent {

        private final MolangExpression enabled;

        private final float collisionDrag;
        private final float collisionRadius;
        private final float coefficientOfRestitution;

        private final boolean expireOnContact;

        private final Map<Float, String> events;

        public ParticleCollisionMotion(MolangExpression enabled, float collisionDrag, float collisionRadius, float coefficientOfRestitution, boolean expireOnContact, Map<Float, String> events) {
            this.enabled = enabled;
            this.collisionDrag = collisionDrag;
            this.collisionRadius = collisionRadius;
            this.coefficientOfRestitution = coefficientOfRestitution;
            this.expireOnContact = expireOnContact;
            this.events = events;
        }

        public MolangExpression getEnabled() {
            return enabled;
        }

        public float getCollisionDrag() {
            return collisionDrag;
        }

        public float getCollisionRadius() {
            return collisionRadius;
        }

        public float getCoefficientOfRestitution() {
            return coefficientOfRestitution;
        }

        public boolean isExpireOnContact() {
            return expireOnContact;
        }

        public Map<Float, String> getEvents() {
            return events;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression enabled;

            float collisionDrag;
            float collisionRadius;
            float coefficientOfRestitution;

            boolean expireOnContact = false;

            Map<Float, String> events = new LinkedHashMap<>();


            public void enabled(MolangExpression enabled) {
                this.enabled = enabled;
            }

            public void collisionDrag(float collisionDrag) {
                this.collisionDrag = collisionDrag;
            }

            public void collisionRadius(float collisionRadius) {
                this.collisionRadius = collisionRadius;
            }

            public void coefficientOfRestitution(float coefficientOfRestitution) {
                this.coefficientOfRestitution = coefficientOfRestitution;
            }

            public void expireOnContact(boolean expireOnContact) {
                this.expireOnContact = expireOnContact;
            }

            public void event(float minSpeed, String event) {
                this.events.put(minSpeed, event);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleCollisionMotion(enabled, collisionDrag, collisionRadius, coefficientOfRestitution, expireOnContact, events);
            }
        }
    }

    protected static class ParticleDynamicMotion extends BedrockComponent {

        private final MolangExpression linearAccelerationX;
        private final MolangExpression linearAccelerationY;
        private final MolangExpression linearAccelerationZ;

        private final MolangExpression linearDragCoefficient;
        private final MolangExpression rotationAcceleration;
        private final MolangExpression rotationDragCoefficient;

        public ParticleDynamicMotion(MolangExpression linearAccelerationX, MolangExpression linearAccelerationY, MolangExpression linearAccelerationZ, MolangExpression linearDragCoefficient, MolangExpression rotationAcceleration, MolangExpression rotationDragCoefficient) {
            this.linearAccelerationX = linearAccelerationX;
            this.linearAccelerationY = linearAccelerationY;
            this.linearAccelerationZ = linearAccelerationZ;
            this.linearDragCoefficient = linearDragCoefficient;
            this.rotationAcceleration = rotationAcceleration;
            this.rotationDragCoefficient = rotationDragCoefficient;
        }

        public MolangExpression getLinearAccelerationX() {
            return linearAccelerationX;
        }

        public MolangExpression getLinearAccelerationY() {
            return linearAccelerationY;
        }

        public MolangExpression getLinearAccelerationZ() {
            return linearAccelerationZ;
        }

        public MolangExpression getLinearDragCoefficient() {
            return linearDragCoefficient;
        }

        public MolangExpression getRotationAcceleration() {
            return rotationAcceleration;
        }

        public MolangExpression getRotationDragCoefficient() {
            return rotationDragCoefficient;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression linearAccelerationX;
            MolangExpression linearAccelerationY;
            MolangExpression linearAccelerationZ;

            MolangExpression linearDragCoefficient;
            MolangExpression rotationAcceleration;
            MolangExpression rotationDragCoefficient;

            public void linearAccelerationX(MolangExpression linearAccelerationX) {
                this.linearAccelerationX = linearAccelerationX;
            }

            public void linearAccelerationY(MolangExpression linearAccelerationY) {
                this.linearAccelerationY = linearAccelerationY;
            }

            public void linearAccelerationZ(MolangExpression linearAccelerationZ) {
                this.linearAccelerationZ = linearAccelerationZ;
            }

            public void linearDragCoefficient(MolangExpression linearDragCoefficient) {
                this.linearDragCoefficient = linearDragCoefficient;
            }

            public void rotationAcceleration(MolangExpression rotationAcceleration) {
                this.rotationAcceleration = rotationAcceleration;
            }

            public void rotationDragCoefficient(MolangExpression rotationDragCoefficient) {
                this.rotationDragCoefficient = rotationDragCoefficient;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleDynamicMotion(linearAccelerationX, linearAccelerationY, linearAccelerationZ, linearDragCoefficient, rotationAcceleration, rotationDragCoefficient);
            }
        }
    }

    protected static class ParticleParametricMotion extends BedrockComponent {

        private final MolangExpression relativePositionX;
        private final MolangExpression relativePositionY;
        private final MolangExpression relativePositionZ;

        private final MolangExpression directionX;
        private final MolangExpression directionY;
        private final MolangExpression directionZ;

        private final MolangExpression rotation;

        public ParticleParametricMotion(MolangExpression relativePositionX, MolangExpression relativePositionY, MolangExpression relativePositionZ, MolangExpression directionX, MolangExpression directionY, MolangExpression directionZ, MolangExpression rotation) {
            this.relativePositionX = relativePositionX;
            this.relativePositionY = relativePositionY;
            this.relativePositionZ = relativePositionZ;
            this.directionX = directionX;
            this.directionY = directionY;
            this.directionZ = directionZ;
            this.rotation = rotation;
        }

        public MolangExpression getRelativePositionX() {
            return relativePositionX;
        }

        public MolangExpression getRelativePositionY() {
            return relativePositionY;
        }

        public MolangExpression getRelativePositionZ() {
            return relativePositionZ;
        }

        public MolangExpression getDirectionX() {
            return directionX;
        }

        public MolangExpression getDirectionY() {
            return directionY;
        }

        public MolangExpression getDirectionZ() {
            return directionZ;
        }

        public MolangExpression getRotation() {
            return rotation;
        }

        protected static class Builder extends BedrockComponent.Builder {

            MolangExpression relativePositionX;
            MolangExpression relativePositionY;
            MolangExpression relativePositionZ;

            MolangExpression directionX;
            MolangExpression directionY;
            MolangExpression directionZ;

            MolangExpression rotation;

            public void relativePositionX(MolangExpression relativePositionX) {
                this.relativePositionX = relativePositionX;
            }

            public void relativePositionY(MolangExpression relativePositionY) {
                this.relativePositionY = relativePositionY;
            }

            public void relativePositionZ(MolangExpression relativePositionZ) {
                this.relativePositionZ = relativePositionZ;
            }


            public void directionX(MolangExpression directionX) {
                this.directionX = directionX;
            }

            public void directionY(MolangExpression directionY) {
                this.directionY = directionY;
            }

            public void directionZ(MolangExpression directionZ) {
                this.directionZ = directionZ;
            }

            public void rotation(MolangExpression rotation) {
                this.rotation = rotation;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleParametricMotion(relativePositionX, relativePositionY, relativePositionZ, directionX, directionY, directionZ, rotation);
            }
        }
    }

    protected static class ParticleLightingAppearance extends BedrockComponent {

        protected static class Builder extends BedrockComponent.Builder {

            @Override
            public BedrockComponent build() {
                return new ParticleLightingAppearance();
            }
        }
    }

    protected static class ParticleBillboardAppearance extends BedrockComponent {

        private final MolangExpression width;
        private final MolangExpression height;
        private final String facingCameraMode;

        private final Size2i textureSize;

        private final MolangExpression u;
        private final MolangExpression v;

        private final MolangExpression uvWidth;
        private final MolangExpression uvHeight;

        private final MolangExpression uvStepX;
        private final MolangExpression uvStepY;

        private final boolean useAnimation;
        private final int fps;
        private final MolangExpression maxFrame;
        private final boolean stretchToLifetime;
        private final boolean loop;

        public ParticleBillboardAppearance(MolangExpression width, MolangExpression height, String facingCameraMode, Size2i textureSize, MolangExpression u, MolangExpression v, MolangExpression uvWidth, MolangExpression uvHeight, MolangExpression uvStepX, MolangExpression uvStepY, boolean useAnimation, int fps, MolangExpression maxFrame, boolean stretchToLifetime, boolean loop) {
            this.width = width;
            this.height = height;
            this.facingCameraMode = facingCameraMode;
            this.textureSize = textureSize;
            this.u = u;
            this.v = v;
            this.uvWidth = uvWidth;
            this.uvHeight = uvHeight;
            this.uvStepX = uvStepX;
            this.uvStepY = uvStepY;
            this.useAnimation = useAnimation;
            this.fps = fps;
            this.maxFrame = maxFrame;
            this.stretchToLifetime = stretchToLifetime;
            this.loop = loop;
        }

        public MolangExpression getWidth() {
            return width;
        }

        public MolangExpression getHeight() {
            return height;
        }

        public String getFacingCameraMode() {
            return facingCameraMode;
        }

        public Size2i getTextureSize() {
            return textureSize;
        }

        public MolangExpression getU() {
            return u;
        }

        public MolangExpression getV() {
            return v;
        }

        public MolangExpression getUvWidth() {
            return uvWidth;
        }

        public MolangExpression getUvHeight() {
            return uvHeight;
        }

        public MolangExpression getUvStepX() {
            return uvStepX;
        }

        public MolangExpression getUvStepY() {
            return uvStepY;
        }

        public boolean isUseAnimation() {
            return useAnimation;
        }

        public int getFps() {
            return fps;
        }

        public MolangExpression getMaxFrame() {
            return maxFrame;
        }

        public boolean isStretchToLifetime() {
            return stretchToLifetime;
        }

        public boolean isLoop() {
            return loop;
        }

        protected static class Builder extends BedrockComponent.Builder {

            private MolangExpression width;
            private MolangExpression height;

            private String facingCameraMode;

            private int textureWidth = 0;
            private int textureHeight = 0;

            private MolangExpression u;
            private MolangExpression v;
            private MolangExpression uvWidth;
            private MolangExpression uvHeight;
            private MolangExpression uvStepX;
            private MolangExpression uvStepY;

            private boolean useAnimation;
            private int fps;
            private MolangExpression maxFrame;
            private boolean stretchToLifetime;
            private boolean loop;

            public void width(MolangExpression width) {
                this.width = width;
            }

            public void height(MolangExpression height) {
                this.height = height;
            }

            public void facingCameraMode(String facingCameraMode) {
                this.facingCameraMode = facingCameraMode;
            }

            public void textureWidth(int textureWidth) {
                this.textureWidth = textureWidth;
            }

            public void textureHeight(int textureHeight) {
                this.textureHeight = textureHeight;
            }

            public void u(MolangExpression u) {
                this.u = u;
            }

            public void v(MolangExpression v) {
                this.v = v;
            }


            public void uvWidth(MolangExpression uvWidth) {
                this.uvWidth = uvWidth;
            }

            public void uvHeight(MolangExpression uvHeight) {
                this.uvHeight = uvHeight;
            }

            public void uvStepX(MolangExpression uvStepX) {
                this.uvStepX = uvStepX;
            }

            public void uvStepY(MolangExpression uvStepY) {
                this.uvStepY = uvStepY;
            }


            public void useAnimation(boolean useAnimation) {
                this.useAnimation = useAnimation;
            }

            public void fps(int fps) {
                this.fps = fps;
            }

            public void maxFrame(MolangExpression maxFrame) {
                this.maxFrame = maxFrame;
            }

            public void stretchToLifetime(boolean stretchToLifetime) {
                this.stretchToLifetime = stretchToLifetime;
            }

            public void loop(boolean loop) {
                this.loop = loop;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleBillboardAppearance(width, height, facingCameraMode, new Size2i(textureWidth, textureHeight), u, v, uvWidth, uvHeight, uvStepX, uvStepY, useAnimation, fps, maxFrame, stretchToLifetime, loop);
            }
        }
    }

    protected static class ParticleTintingAppearance extends BedrockComponent {

        private final List<MolangExpression> values;
        private final MolangExpression interpolant;
        private final Map<String, String> gradientValues;

        public ParticleTintingAppearance(List<MolangExpression> values, MolangExpression interpolant, Map<String, String> gradientValues) {
            this.values = values;
            this.interpolant = interpolant;
            this.gradientValues = gradientValues;
        }

        public List<MolangExpression> getValues() {
            return values;
        }

        public MolangExpression getInterpolant() {
            return interpolant;
        }

        public Map<String, String> getGradientValues() {
            return gradientValues;
        }

        protected static class Builder extends BedrockComponent.Builder {

            private MolangExpression interpolant;
            private final List<MolangExpression> values = new ArrayList<>();
            private final Map<String, String> gradientValues = new LinkedHashMap<>();

            public void addColor(MolangExpression expression) {
                this.values.add(expression);
            }

            public void interpolant(MolangExpression interpolant) {
                this.interpolant = interpolant;
            }

            public void addColor(String progress, String value) {
                this.gradientValues.put(progress, value);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleTintingAppearance(values, interpolant, gradientValues);
            }
        }
    }

    protected static abstract class Builder {

        public abstract BedrockComponent build();
    }

    protected static abstract class ShapeBuilder extends BedrockComponent.Builder {

        MolangExpression offsetX;
        MolangExpression offsetY;
        MolangExpression offsetZ;

        Object direction;
        MolangExpression radius;
        boolean surfaceOnly;

        public void offsetX(MolangExpression offsetX) {
            this.offsetX = offsetX;
        }

        public void offsetY(MolangExpression offsetY) {
            this.offsetY = offsetY;
        }

        public void offsetZ(MolangExpression offsetZ) {
            this.offsetZ = offsetZ;
        }

        public void direction(Object direction) {
            this.direction = direction;
        }

        public void radius(MolangExpression radius) {
            this.radius = radius;
        }

        public void surfaceOnly(boolean surfaceOnly) {
            this.surfaceOnly = surfaceOnly;
        }
    }
}
