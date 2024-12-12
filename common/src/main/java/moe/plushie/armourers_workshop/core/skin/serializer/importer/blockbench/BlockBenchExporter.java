package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Rectangle2f;
import moe.plushie.armourers_workshop.core.math.Rectangle3f;
import moe.plushie.armourers_workshop.core.math.Size2f;
import moe.plushie.armourers_workshop.core.math.Vector2f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationFunction;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationKeyframe;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationLoop;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationPoint;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryVertex;
import moe.plushie.armourers_workshop.core.skin.geometry.collection.SkinGeometrySetV2;
import moe.plushie.armourers_workshop.core.skin.geometry.mesh.SkinMeshFace;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.OptimizeContext;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleData;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinSettings;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock.BedrockExporter;
import moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock.BedrockParticle;
import moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock.BedrockParticleReader;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundData;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundProperties;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureAnimation;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureBox;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureOptions;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureProperties;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransforms;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;
import moe.plushie.armourers_workshop.init.ModLog;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class BlockBenchExporter {

    protected SkinSettings settings = new SkinSettings();
    protected SkinProperties properties = new SkinProperties();


    protected Vector3f offset = Vector3f.ZERO;
    protected Vector3f displayOffset = Vector3f.ZERO;

    protected final BlockBenchPack pack;
    protected final MolangVirtualMachine virtualMachine;

    public BlockBenchExporter(BlockBenchPack pack) {
        this(pack, new MolangVirtualMachine());
    }

    public BlockBenchExporter(BlockBenchPack pack, MolangVirtualMachine virtualMachine) {
        this.pack = pack;
        this.virtualMachine = virtualMachine;
    }

    public Skin export() throws IOException {
        // build bone tree of the outliner.
        var rootBone = new Bone(pack, pack.getRootOutliner(), null);

        // convert to rendering coordinate.
        var poseStack = new OpenPoseStack();
        poseStack.scale(-1, -1, 1);
        poseStack.translate(-offset.getX(), -offset.getY(), -offset.getZ());
        Collections.eachTree(Collections.singleton(rootBone), it -> it.children, it -> it.transform(poseStack));
        rootBone.children.forEach(it -> it.convertToLocal(Vector3f.ZERO));

        // search all used texture by the bone tree.
        var usedTextureIds = new HashSet<Integer>();
        Collections.eachTree(Collections.singleton(rootBone), it -> it.children, it -> {
            it.cubes.forEach(cube -> {
                usedTextureIds.add(cube.uv.getDefaultTextureId());
                cube.uv.forEachTextures((dir, textureId) -> usedTextureIds.add(textureId));
            });
            it.meshes.forEach(mesh -> mesh.faces.forEach(face -> {
                usedTextureIds.add(face.textureId);
            }));
        });

        // load the all used textures into texture set.
        var textureSet = new TextureSet(pack.getResolution(), pack.getTextures(), usedTextureIds);

        // export root bone to root part.
        var rootPart = exportRootPart(rootBone, textureSet);

        // export all item transforms to skin item transforms if needs.
        if (pack.getItemTransforms() != null) {
            var itemTransforms = exportItemTransforms(pack.getItemTransforms());
            settings.setItemTransforms(itemTransforms);
        }

        // export all animations to skin animation.
        var animations = exportAnimations(pack.getAnimations());

        // build the skin.
        var builder = new Skin.Builder(SkinTypes.ADVANCED);
        builder.parts(rootPart.getChildren());
        builder.settings(settings);
        builder.properties(properties);
        builder.animations(animations);
        builder.version(SkinSerializer.Versions.LATEST);
        return builder.build();
    }

    protected SkinPart exportRootPart(Bone bone, TextureSet textureSet) {
        // move all ungroup cubes to the new part.
        var rootPart = exportPart(bone, textureSet);
        if (!rootPart.getGeometries().isEmpty()) {
            var builder = new SkinPart.Builder(SkinPartTypes.ADVANCED);
            builder.geometries(rootPart.getGeometries());
            rootPart.addPart(builder.build());
        }
        return rootPart;
    }

    protected SkinPart exportPart(Bone bone, TextureSet textureSet) {
        var geometries = new SkinGeometrySetV2();
        var children = new ArrayList<SkinPart>();

        bone.children.forEach(it -> children.add(exportPart(it, textureSet)));

        bone.cubes.forEach(it -> geometries.addBox(exportCube(it, textureSet)));
        bone.meshes.forEach(it -> geometries.addMesh(exportMesh(it, textureSet)));
        bone.locators.forEach(it -> children.add(exportLocator(it)));

        var builder = new SkinPart.Builder(SkinPartTypes.ADVANCED);
        builder.name(bone.name);
        builder.transform(OpenTransform3f.create(bone.origin, bone.rotation, Vector3f.ONE, bone.pivot, Vector3f.ZERO));
        builder.children(children);
        builder.geometries(geometries);
        return builder.build();
    }

    protected SkinPart exportLocator(Locator locator) {
        var builder = new SkinPart.Builder(SkinPartTypes.ADVANCED_LOCATOR);
        builder.name(locator.name);
        builder.transform(OpenTransform3f.create(locator.origin, locator.rotation, Vector3f.ONE));
        return builder.build();
    }

    protected SkinGeometrySetV2.Box exportCube(Cube cube, TextureSet texture) {
        float x = cube.origin.getX();
        float y = cube.origin.getY();
        float z = cube.origin.getZ();

        float w = cube.size.getX();
        float h = cube.size.getY();
        float d = cube.size.getZ();

        float inflate = cube.inflate;

        var skyBox = texture.read(cube);
        if (inflate != 0) {
            // after inflate, the cube size and texture size has been diff,
            // so we need to split per-face, it means each face will save separately.
            skyBox = skyBox.separated();
        }

        var rect = new Rectangle3f(x, y, z, w, h, d).inflate(inflate);
        var transform = OpenTransform3f.create(Vector3f.ZERO, cube.rotation, Vector3f.ONE, cube.pivot, Vector3f.ZERO);
        return new SkinGeometrySetV2.Box(rect, transform, skyBox);
    }

    protected SkinGeometrySetV2.Mesh exportMesh(Mesh mesh, TextureSet texture) {
        var faces = new ArrayList<SkinMeshFace>();
        var transform = OpenTransform3f.create(mesh.origin, mesh.rotation, Vector3f.ONE, Vector3f.ZERO, Vector3f.ZERO);
        var defaultTexturePos = new SkinTexturePos[1];
        var sequence = new AtomicInteger();
        mesh.faces.stream().sorted(Comparator.comparingInt(it -> it.vertices.size())).forEachOrdered(it -> {
            // ignore all not use texture face.
            var texturePos = texture.read(it);
            if (texturePos == null) {
                return;
            }
            var faceId = faces.size();
            var vertices = new ArrayList<SkinGeometryVertex>();
            it.vertices.forEach(it2 -> {
                var vertexId = sequence.getAndIncrement();
                var position = it2.position;
                var normal = it2.normal;
                var textureCoords = it2.textureCoords;
                vertices.add(new SkinGeometryVertex(vertexId, position, normal, textureCoords));
                TextureResolution.applyBoundary(texturePos.getProvider(), textureCoords.getX(), textureCoords.getY());
            });
            faces.add(new SkinMeshFace(faceId, transform, texturePos, vertices));
            defaultTexturePos[0] = texturePos;
        });
        return new SkinGeometrySetV2.Mesh(transform, defaultTexturePos[0], faces);
    }

    protected OpenItemTransforms exportItemTransforms(Map<String, BlockBenchDisplay> transforms) {
        var fullItemTransforms = new LinkedHashMap<>(transforms);
        var itemTransforms = new OpenItemTransforms();
        for (var value : OpenItemDisplayContext.values()) {
            if (!fullItemTransforms.containsKey(value.getName())) {
                fullItemTransforms.put(value.getName(), new BlockBenchDisplay(Vector3f.ZERO, Vector3f.ZERO, Vector3f.ONE));
            }
        }
        fullItemTransforms.forEach((name, transform) -> {
            var translation = transform.getTranslation().scaling(-1, -1, 1);
            var rotation = transform.getRotation().scaling(-1, -1, 1);
            var scale = transform.getScale();
            var transform1 = OpenTransform3f.create(translation, rotation, scale);
            // for identity transform, since it's the default value, we don't need to save it.
            if (!transform1.isIdentity()) {
                itemTransforms.put(name, transform1);
            }
        });
        // add display offset
        if (!displayOffset.equals(Vector3f.ZERO) && !itemTransforms.isEmpty()) {
            var translation = displayOffset.scaling(-1, -1, 1);
            var rotation = Vector3f.ZERO;
            var scale = Vector3f.ONE;
            itemTransforms.setOffset(OpenTransform3f.create(translation, rotation, scale));
        }
        return itemTransforms;
    }

    protected List<SkinAnimation> exportAnimations(List<BlockBenchAnimation> allAnimations) {
        var results = new ArrayList<SkinAnimation>();
        var animator = new Animator(getVirtualMachine());
        allAnimations.forEach(animation -> {
            var name = animation.getName();
            var duration = animation.getDuration();
            var loop = animator.toAnimationLoop(animation.getLoop());
            var values = animator.toAnimationKeyframes(animation.getAnimators());
            if (values.isEmpty()) {
                return;
            }
            results.add(new SkinAnimation(name, duration, loop, values));
        });
        return results;
    }


    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }

    public Vector3f getOffset() {
        return offset;
    }

    public void setDisplayOffset(Vector3f displayOffset) {
        this.displayOffset = displayOffset;
    }

    public Vector3f getDisplayOffset() {
        return displayOffset;
    }

    public SkinSettings getSettings() {
        return settings;
    }

    public SkinProperties getProperties() {
        return properties;
    }

    public MolangVirtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    protected static class Bone {

        public String id;
        public String name;

        public Vector3f origin;

        public Vector3f pivot;
        public Vector3f rotation;

        public Bone parent;
        public ArrayList<Bone> children = new ArrayList<>();

        public ArrayList<Cube> cubes = new ArrayList<>();
        public ArrayList<Mesh> meshes = new ArrayList<>();
        public ArrayList<Locator> locators = new ArrayList<>();

        public boolean mirror;

        // https://github.com/JannisX11/blockbench/blob/master/js/io/formats/bedrock.js#L781
        public Bone(BlockBenchPack pack, BlockBenchOutliner outliner, @Nullable Bone parent) {
            this.id = outliner.getUUID();
            this.name = outliner.getName();
            this.mirror = false;

            this.origin = outliner.getOrigin();
            this.pivot = outliner.getOrigin();
            this.rotation = outliner.getRotation();

            this.parent = parent;

            for (var child : outliner.getChildren()) {
                // is a exportable bone?
                if (child instanceof BlockBenchOutliner childOutliner) {
                    if (childOutliner.allowExport()) {
                        children.add(new Bone(pack, childOutliner, this));
                    }
                }
                // is a exportable element?
                if (child instanceof String ref && pack.getObject(ref) instanceof BlockBenchElement element) {
                    if (element.allowExport()) {
                        if (element instanceof BlockBenchCube cube) {
                            cubes.add(new Cube(cube));
                        }
                        if (element instanceof BlockBenchMesh mesh) {
                            meshes.add(new Mesh(mesh));
                        }
                        if (element instanceof BlockBenchLocator locator) {
                            locators.add(new Locator(locator));
                        }
                    }
                }
            }
        }

        public void transform(OpenPoseStack poseStack) {
            origin = origin.transforming(poseStack.last().pose());
            pivot = pivot.transforming(poseStack.last().pose());
            rotation = rotation.transforming(poseStack.last().normal());

            cubes.forEach(it -> it.transform(poseStack));
            meshes.forEach(it -> it.transform(poseStack));
            locators.forEach(it -> it.transform(poseStack));
        }

        public void convertToLocal(Vector3f globalOffset) {
            var newOrigin = origin;

            var poseStack = new OpenPoseStack();
            poseStack.translate(-newOrigin.getX(), -newOrigin.getY(), -newOrigin.getZ());
            transform(poseStack);

            origin = newOrigin.subtracting(globalOffset);
            pivot = Vector3f.ZERO;

            children.forEach(it -> it.convertToLocal(newOrigin));
        }
    }

    protected static class Cube {

        public Vector3f origin;
        public Vector3f size;

        public Vector3f pivot;
        public Vector3f rotation;

        public TextureUV uv;

        public float inflate;
        public boolean mirror = false;

        public Cube(BlockBenchCube cube) {
            this.origin = cube.getFrom();
            this.size = cube.getTo().subtracting(cube.getFrom());
            this.inflate = cube.getInflate();

            this.pivot = cube.getOrigin();
            this.rotation = cube.getRotation();

            this.uv = TextureUV.createUV(cube);
        }

        public void transform(OpenPoseStack poseStack) {
            var rect = new Rectangle3f(origin.getX(), origin.getY(), origin.getZ(), size.getX(), size.getY(), size.getZ());
            rect.mul(poseStack.last().pose());
            origin = new Vector3f(rect.getX(), rect.getY(), rect.getZ());
            size = new Vector3f(rect.getWidth(), rect.getHeight(), rect.getDepth());
            pivot = pivot.transforming(poseStack.last().pose());
            rotation = rotation.transforming(poseStack.last().normal());
        }
    }

    protected static class Mesh {

        public Vector3f origin;
        public Vector3f pivot;
        public Vector3f rotation;

        public final List<MeshFace> faces = new ArrayList<>();

        public Mesh(BlockBenchMesh mesh) {
            this.origin = mesh.getOrigin();
            this.pivot = mesh.getOrigin();
            this.rotation = mesh.getRotation();
            for (var entry : mesh.getFaces().entrySet()) {
                faces.add(new MeshFace(entry.getKey(), entry.getValue(), mesh));
            }
        }

        public void transform(OpenPoseStack poseStack) {
            origin = origin.transforming(poseStack.last().pose());
            pivot = pivot.transforming(poseStack.last().pose());
            rotation = rotation.transforming(poseStack.last().normal());

            // apply a removed translation matrix.
            var fixedPoseStack = poseStack.copy();
            fixedPoseStack.last().pose().setTranslation(0, 0, 0);
            faces.forEach(it -> it.transform(fixedPoseStack));
        }
    }

    // https://github.com/JannisX11/blockbench/blob/2662eff12323c58af0b13a3f685ab3baf75b74c8/js/outliner/mesh.js
    protected static class MeshFace {

        public String id;
        public int textureId;

        public ArrayList<MeshVertex> vertices = new ArrayList<>();

        public MeshFace(String id, BlockBenchMeshFace face, BlockBenchMesh mesh) {
            this.id = id;
            this.textureId = face.getTextureId();
            for (var vertexId : face.getVertices()) {
                var position = mesh.getVertices().get(vertexId);
                var textureCoords = face.getUV().get(vertexId);
                vertices.add(new MeshVertex(id + "/" + vertexId, position, Vector3f.ZERO, textureCoords));
            }
            if (vertices.size() < 3) {
                throw new RuntimeException("error.bb.loadModel.wrongVertexCount");
            }
            sortVertices();
            rebuildNormals();
        }

        public void transform(OpenPoseStack poseStack) {
            vertices.forEach(it -> it.transform(poseStack));
        }

        private void rebuildNormals() {
            var a = vertices.get(1).position.subtracting(vertices.get(0).position);
            var b = vertices.get(2).position.subtracting(vertices.get(0).position);
            var n = a.crossing(b).normalizing();
            vertices.forEach(it -> it.normal = n);
        }

        private void sortVertices() {
            if (vertices.size() < 4) {
                return;
            }
            // https://github.com/JannisX11/blockbench/blob/2662eff12323c58af0b13a3f685ab3baf75b74c8/js/outliner/mesh.js#L184
            var sortedVertices = new ArrayList<MeshVertex>();
            if (_test(vertices.get(1), vertices.get(2), vertices.get(0), vertices.get(3))) {
                sortedVertices.add(vertices.get(2));
                sortedVertices.add(vertices.get(0));
                sortedVertices.add(vertices.get(1));
                sortedVertices.add(vertices.get(3));
            } else if (_test(vertices.get(0), vertices.get(1), vertices.get(2), vertices.get(3))) {
                sortedVertices.add(vertices.get(0));
                sortedVertices.add(vertices.get(2));
                sortedVertices.add(vertices.get(1));
                sortedVertices.add(vertices.get(3));
            } else {
                sortedVertices.addAll(vertices);
            }
            vertices = sortedVertices;
        }

        // Test if point "check" is on the other side of the line between "base1" and "base2", compared to "top"
        private boolean _test(MeshVertex base1, MeshVertex base2, MeshVertex top, MeshVertex check) {
            // Construct a plane with coplanar points "base1" and "base2" with a normal towards "top"
            var normal = _line_closestPointToPoint(base1.position, base2.position, top.position);
            normal = normal.subtracting(top.position);
            float distance = _plane_distanceToPoint(normal, base2.position, check.position);
            return distance > 0;
        }

        // normal = Line3(base1, base2).closestPointToPoint(top, false)
        private Vector3f _line_closestPointToPoint(Vector3f start, Vector3f end, Vector3f point) {
            var delta = end.subtracting(start);
            // Line3.closestPointToPointParameter
            var startEnd2 = delta.dot(delta);
            var startEnd_startP = delta.dot(point.subtracting(start));
            float t = startEnd_startP / startEnd2;

            // Line3.closestPointToPoint
            return delta.scaling(t).adding(start);
        }

        // distance = Plane(normal, base2).distanceToPoint(check)
        private float _plane_distanceToPoint(Vector3f normal, Vector3f point, Vector3f check) {
            // Plane.setFromNormalAndCoplanarPoint
            float constant = -point.dot(normal);
            // Plane.distanceToPoint
            return normal.dot(check) + constant;
        }
    }

    protected static class MeshVertex {

        public String id;
        public Vector3f position;
        public Vector3f normal;
        public Vector2f textureCoords;

        public MeshVertex(String id, Vector3f position, Vector3f normal, Vector2f textureCoords) {
            this.id = id;
            this.position = position;
            this.normal = normal;
            this.textureCoords = textureCoords;
        }

        public void transform(OpenPoseStack poseStack) {
            position = position.transforming(poseStack.last().pose());
            normal = normal.transforming(poseStack.last().normal());
        }

        public MeshVertex copy() {
            return new MeshVertex(id, position, normal, textureCoords);
        }
    }

    protected static class Locator {

        public String name;

        public Vector3f origin;
        public Vector3f rotation;

        public Locator(BlockBenchLocator locator) {
            this.name = locator.getName();
            this.origin = locator.getPosition();
            this.rotation = locator.getRotation();
        }

        public void transform(OpenPoseStack poseStack) {
            origin = origin.transforming(poseStack.last().pose());
            rotation = rotation.transforming(poseStack.last().normal());
        }
    }

    protected static class Animator {

        private final MolangVirtualMachine virtualMachine;

        public Animator(MolangVirtualMachine virtualMachine) {
            this.virtualMachine = virtualMachine;
        }

        public Map<String, List<SkinAnimationKeyframe>> toAnimationKeyframes(List<BlockBenchAnimator> animators) {
            var results = new LinkedHashMap<String, List<SkinAnimationKeyframe>>();
            for (var animator : animators) {
                var keyframes = results.computeIfAbsent(animator.getName(), k -> new ArrayList<>());
                for (var keyframe : animator.getKeyframes()) {
                    var time = keyframe.getTime();
                    var channel = keyframe.getName();
                    var function = toAnimationFunction(keyframe);
                    var points = toAnimationPoints(keyframe, animator);
                    if (!points.isEmpty()) {
                        keyframes.add(new SkinAnimationKeyframe(time, channel, function, points));
                    }
                }
            }
            return results;
        }

        public List<SkinAnimationPoint> toAnimationPoints(BlockBenchKeyframe keyframe, BlockBenchAnimator animator) {
            var points = new ArrayList<SkinAnimationPoint>();
            var channel = keyframe.getName();
            switch (animator.getType()) {
                case "bone" -> {
                    for (var point : keyframe.getPoints()) {
                        var x = toAnimationPoint(point.getOrDefault("x", 0));
                        var y = toAnimationPoint(point.getOrDefault("y", 0));
                        var z = toAnimationPoint(point.getOrDefault("z", 0));
                        if (channel.equals("position")) {
                            y = toAnimationNegativePoint(y);
                        }
                        points.add(new SkinAnimationPoint.Bone(x, y, z));
                    }
                }
                case "effect" -> {
                    switch (channel) {
                        case "timeline" -> {
                            for (var point : keyframe.getPoints()) {
                                var script = toAnimationScript(point.get("script"));
                                if (script == null || script.isEmpty()) {
                                    continue; // ignore empty instruct.
                                }
                                points.add(new SkinAnimationPoint.Instruct(script));
                            }
                        }
                        case "sound" -> {
                            for (var point : keyframe.getPoints()) {
                                var effect = Objects.safeCast(point.get("effect"), String.class);
                                var filePath = Objects.safeCast(point.get("file"), String.class);
                                if (effect == null && filePath == null) {
                                    continue; // ignore empty sound.
                                }
                                var soundBuilder = new SoundBuilder(virtualMachine);
                                var sound = soundBuilder.build(effect, filePath);
                                if (sound == null) {
                                    continue; // can't resolve the sound.
                                }
                                points.add(sound);
                            }
                        }
                        case "particle" -> {
                            for (var point : keyframe.getPoints()) {
                                var effect = Objects.safeCast(point.get("effect"), String.class);
                                var locator = Objects.safeCast(point.get("locator"), String.class);
                                var script = Objects.safeCast(point.get("script"), String.class);
                                var filePath = Objects.safeCast(point.get("file"), String.class);
                                if (effect == null && filePath == null) {
                                    continue; // ignore empty sound.
                                }
                                var particleBuilder = new ParticleBuilder(virtualMachine);
                                var particle = particleBuilder.build(effect, locator, filePath, script);
                                if (particle == null) {
                                    continue; // can't resolve the particle.
                                }
                                points.add(particle);
                            }
                        }
                        default -> ModLog.warn("a unknown effect channel of '{}'", channel);
                    }
                }
                default -> ModLog.warn("a unknown type of '{}'", animator.getType());
            }
            return points;
        }

        public Object toAnimationPoint(Object value) {
            if (value instanceof String script) {
                try {
                    // for blank script, we assume it to be a 0
                    if (script.isEmpty()) {
                        return 0f;
                    }
                    var expr = virtualMachine.compile(script);
                    if (expr.isMutable()) {
                        return script;
                    }
                    return (float) expr.compute(OptimizeContext.DEFAULT);
                } catch (Exception exception) {
                    //throw new RuntimeException("can't parse \"" + script + "\" in model!", exception);
                    exception.printStackTrace();
                }
            }
            if (value instanceof Number number) {
                return number.floatValue();
            }
            return 0f;
        }

        public Object toAnimationNegativePoint(Object point) {
            if (point instanceof String script) {
                point = "-(" + script + ")";
            } else if (point instanceof Number number) {
                point = -number.floatValue();
            }
            return point;
        }

        public SkinAnimationLoop toAnimationLoop(String value) {
            return switch (value) {
                case "once" -> SkinAnimationLoop.NONE;
                case "hold" -> SkinAnimationLoop.LAST_FRAME;
                case "loop" -> SkinAnimationLoop.LOOP;
                default -> SkinAnimationLoop.LOOP; // missing
            };
        }

        public static SkinAnimationFunction toAnimationFunction(BlockBenchKeyframe keyframe) {
            return switch (keyframe.getInterpolation()) {
                case "bezier" -> SkinAnimationFunction.bezier(keyframe.getParameters());
                case "linear" -> SkinAnimationFunction.linear();
                case "step" -> SkinAnimationFunction.step();
                case "smooth" -> SkinAnimationFunction.smooth();
                default -> SkinAnimationFunction.linear(); // missing
            };
        }

        private String toAnimationScript(Object value) {
            try {
                if (value instanceof String script) {
                    var expr = virtualMachine.compile(script);
                    if (expr.isMutable()) {
                        return script;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }

        protected static class SoundBuilder {

            private final MolangVirtualMachine virtualMachine;

            public SoundBuilder(MolangVirtualMachine virtualMachine) {
                this.virtualMachine = virtualMachine;
            }

            public SkinAnimationPoint.Sound build(String effect, String filePath) {
                // mod_id:sound_id|volume|pitch
                if (effect != null && effect.contains(":")) {
                    var properties = resolveSoundProperties(effect);
                    var soundProvider = new SkinSoundData(null, Unpooled.EMPTY_BUFFER, properties);
                    return new SkinAnimationPoint.Sound(effect, soundProvider);
                }
                var soundBytes = resolveSoundData(filePath);
                if (soundBytes == null) {
                    ModLog.warn("can't load data of: '{}', file: '{}'", effect, filePath);
                    return null;
                }
                // file_name|volume|pitch
                var fileName = FileUtils.getBaseName(filePath);
                var properties = resolveSoundProperties(effect);
                var soundProvider = new SkinSoundData(null, soundBytes, properties);
                // must provide a name.
                if (effect == null || effect.isEmpty()) {
                    effect = fileName;
                }
                return new SkinAnimationPoint.Sound(effect, soundProvider);
            }

            private SkinSoundProperties resolveSoundProperties(String name) {
                // can't read properties from the effect?
                if (name == null || !name.contains("|")) {
                    return SkinSoundProperties.EMPTY;
                }
                // mod_id:sound_name|volume|pitch
                var parts = name.split("\\|");
                var properties = new SkinSoundProperties();
                try {
                    if (parts.length > 1) {
                        properties.setVolume(Float.parseFloat(parts[1]));
                    }
                    if (parts.length > 2) {
                        properties.setPitch(Float.parseFloat(parts[2]));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return properties;
            }

            private ByteBuf resolveSoundData(String path) {
                try {
                    if (path == null || path.isEmpty()) {
                        return null;
                    }
                    var bytes = StreamUtils.readFileToByteArray(new File(path));
                    return Unpooled.wrappedBuffer(bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        protected static class ParticleBuilder {

            private final MolangVirtualMachine virtualMachine;

            public ParticleBuilder(MolangVirtualMachine virtualMachine) {
                this.virtualMachine = virtualMachine;
            }

            public SkinAnimationPoint.Particle build(String effect, String locator, String filePath, String script) {
                // mod_id:sound_id|volume|pitch
                if (effect != null && effect.contains(":")) {
                    ModLog.warn("can't support builtin particle '{}' now", effect);
                    return null;
                }
                var resolvedParticle = resolveParticleFile(filePath);
                if (resolvedParticle == null) {
                    ModLog.warn("can't load data of: '{}', file: '{}'", effect, filePath);
                    return null;
                }
                // file_name|volume|pitch
                var fileName = FileUtils.getBaseName(filePath);
                var particleProvider = exportParticle(resolvedParticle);
                // must provide a name.
                if (effect == null || effect.isEmpty()) {
                    effect = fileName;
                }
                return new SkinAnimationPoint.Particle(effect, locator, resolveScript(script), particleProvider);
            }

            protected SkinParticleData exportParticle(BedrockParticle particle) {
                var exporter = new BedrockExporter(null, virtualMachine);
                return exporter.exportParticle(particle);
            }

            private BedrockParticle resolveParticleFile(String path) {
                try {
                    if (path == null || path.isEmpty()) {
                        return null;
                    }
                    var reader = new BedrockParticleReader(new File(path));
                    return reader.readPack();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            private String resolveScript(String script) {
                try {
                    if (script == null) {
                        return null;
                    }
                    var expr = virtualMachine.compile(script);
                    if (!expr.isMutable()) {
                        return null; // no needs.
                    }
                    return script;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    protected static class TextureSet {

        private static final String PATTERN = "^(.+)_([nes]+)(\\.\\w+)?$";

        private final Size2f resolution;
        private final List<BlockBenchTexture> inputs;
        private final HashMap<Integer, SkinTextureData> allTexture = new HashMap<>();
        private final HashMap<String, SkinTextureData> loadedTextures = new HashMap<>();

        protected SkinTextureData textureData;
        protected SkinTextureData defaultTextureData;

        public TextureSet(Size2f resolution, List<BlockBenchTexture> textureInputs, HashSet<Integer> usedTextureIds) throws IOException {
            this.resolution = resolution;
            this.inputs = textureInputs;
            this.load(usedTextureIds);
        }

        public void load(HashSet<Integer> usedTextureIds) throws IOException {
            for (var textureId : usedTextureIds) {
                // ignore invalid textures.
                if (textureId < 0 || textureId >= inputs.size()) {
                    continue;
                }
                var texture = inputs.get(textureId);
                var data = loadTextureData(texture);
                allTexture.put(textureId, data);
                if (defaultTextureData == null) {
                    defaultTextureData = data;
                }
            }
            textureData = defaultTextureData;
            if (textureData == null) {
                throw new IOException("error.bb.loadModel.noTexture");
            }
        }

        public SkinTextureData loadTextureData(BlockBenchTexture texture) throws IOException {
            var data = resolveTextureData(texture);
            var variants = new ArrayList<SkinTextureData>();
            var parentName = texture.getName().replaceAll(PATTERN, "$1$3");
            var parentAttributes = getTextureAttributes(texture.getName());
            // single texture model: bedrock_entity/bedrock_entity_old/geckolib_armour/geckolib_entity/geckolib_block/modded_entity/optifine_entity
            // some models only support single texture, so load additional textures by special file names.
            for (var childTexture : inputs) {
                var childName = childTexture.getName().replaceAll(PATTERN, "$1$3");
                if (!childName.equals(parentName) || childTexture == texture) {
                    continue;
                }
                var childAttributes = getTextureAttributes(childTexture.getName());
                if (!childAttributes.containsAll(parentAttributes)) {
                    continue;
                }
                var childData = resolveTextureData(childTexture);
                if (data.getProperties().isEmissive()) {
                    // when the parent texture is emissive texture, the child texture must is emissive texture.
                    childData.getProperties().setEmissive(true);
                }
                variants.add(childData);
            }
            data.setVariants(variants);
            TextureResolution.apply(data);
            return data;
        }

        public SkinTextureBox read(Cube cube) {
            var uv = cube.uv;
            var size = cube.size;
            var textureData = getTextureData(uv);
            var skyBox = new SkinTextureBox(size.getX(), size.getY(), size.getZ(), cube.mirror, uv.getBase(), textureData);
            uv.forEach((dir, rect) -> {
                skyBox.putTextureRect(dir, rect);
                skyBox.putTextureProvider(dir, getTextureData(uv, dir));
            });
            uv.forEachRotations((dir, rot) -> {
                var options = new SkinTextureOptions();
                options.setRotation(rot);
                skyBox.putTextureOptions(dir, options);
            });
            // check texture coords is beyond?
            for (var dir : OpenDirection.values()) {
                var pos = skyBox.getTexture(dir);
                if (pos != null) {
                    TextureResolution.applyBoundary(pos.getProvider(), pos.getU(), pos.getV());
                }
            }
            return skyBox;
        }

        public SkinTexturePos read(MeshFace meshFace) {
            var textureData = allTexture.get(meshFace.textureId);
            if (textureData != null) {
                return new SkinTexturePos(0, 0, 0, 0, textureData);
            }
            return null;
        }


        protected SkinTextureData getTextureData(TextureUV uv) {
            return allTexture.get(uv.getDefaultTextureId());
        }

        protected SkinTextureData getTextureData(TextureUV uv, OpenDirection dir) {
            return allTexture.get(uv.getTextureId(dir));
        }

        private SkinTextureData resolveTextureData(BlockBenchTexture texture) throws IOException {
            var textureData = loadedTextures.get(texture.getUUID());
            if (textureData != null) {
                return textureData;
            }
            var str = texture.getSource();
            var parts = str.split(";base64,");
            if (parts.length != 2) {
                throw new IOException("error.bb.loadModel.textureNotSupported");
            }
            var imageBytes = Base64.getDecoder().decode(parts[1]);
            var imageFrame = resolveTextureFrame(texture, imageBytes);
            var size = resolveTextureSize(texture, imageFrame);
            var animation = resolveTextureAnimation(texture, imageFrame);
            var properties = resolveTextureProperties(texture);
            textureData = new SkinTextureData(texture.getName(), size.getWidth(), size.getHeight(), animation, properties);
            textureData.load(Unpooled.wrappedBuffer(imageBytes));
            loadedTextures.put(texture.getUUID(), textureData);
            return textureData;
        }

        private int resolveTextureFrame(BlockBenchTexture texture, byte[] imageBytes) throws IOException {
            // in new version block bench provides image size.
            var imageSize = texture.getImageSize();
            if (imageSize == null) {
                var image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                imageSize = new Size2f(image.getWidth(), image.getHeight());
            }
            var textureSize = resolveTextureSize(texture, 1);
            if (textureSize.width == 0 || textureSize.height == 0) {
                return 0;
            }
            // image(32, 640) => uv(128, 128) => frame(20)
            // image(63, 2574) => uv(63, 66) => frame(39)
            // image(256, 256) => uv(128, 128) => frame(1)
            var scaleHeight = OpenMath.floori(imageSize.width * (textureSize.height / textureSize.width));
            var height = OpenMath.floori(imageSize.height);
            var frame = OpenMath.floori(imageSize.height / scaleHeight);
            if (frame * scaleHeight == height) {
                return frame;
            }
            return 0;
        }

        private Size2f resolveTextureSize(BlockBenchTexture texture, int frameCount) {
            var width = resolution.getWidth();
            var height = resolution.getHeight();
            // in new version block bench provides texture size.
            if (texture.getTextureSize() != null) {
                width = texture.getTextureSize().getWidth();
                height = texture.getTextureSize().getHeight();
            }
            if (frameCount > 1) {
                height *= frameCount;
            }
            return new Size2f(width, height);
        }

        private SkinTextureAnimation resolveTextureAnimation(BlockBenchTexture texture, int frameCount) {
            if (frameCount > 1) {
                var time = texture.getFrameTime() * 50; // 1/20s
                var interpolate = texture.getFrameInterpolate();
                var mode = texture.getFrameMode();
                return new SkinTextureAnimation(time, frameCount, mode, interpolate);
            }
            return SkinTextureAnimation.EMPTY;
        }

        private SkinTextureProperties resolveTextureProperties(BlockBenchTexture texture) {
            var properties = texture.getProperties();
            for (var attrib : getTextureAttributes(texture.getName())) {
                switch (attrib) {
                    case "n" -> properties.setNormal(true);
                    case "e" -> properties.setEmissive(true);
                    case "s" -> properties.setSpecular(true);
                }
            }
            return properties;
        }

        private Collection<String> getTextureAttributes(String name) {
            var attrib = name.replaceAll(PATTERN, "$2");
            if (attrib.equals(name)) {
                return Collections.emptyList();
            }
            var results = new HashSet<String>();
            for (byte ch : attrib.getBytes(StandardCharsets.UTF_8)) {
                results.add(String.valueOf((char) ch));
            }
            return results;
        }
    }

    protected static class TextureUV {

        public static final TextureUV EMPTY = new TextureUV();

        private final Vector2f base;

        private EnumMap<OpenDirection, Integer> rotations;
        private final EnumMap<OpenDirection, Rectangle2f> rects = new EnumMap<>(OpenDirection.class);

        private int defaultTextureId = -1;
        private EnumMap<OpenDirection, Integer> textureIds;

        public TextureUV() {
            this.base = null;
        }

        public TextureUV(Vector2f uv) {
            this.base = uv;
        }

        public static TextureUV createUV(BlockBenchCube element) {
            // box texture
            if (element.isBoxUV() && !element.isMirrorUV() && isAlignedSize(element)) {
                var uv = new TextureUV(element.getUVOffset());
                element.getFaces().forEach((dir, face) -> {
                    uv.setDefaultTextureId(face.getTextureId());
                    uv.setRotation(dir, face.getRotation());
                });
                return uv;
            }
            // per-face texture
            var uv = new TextureUV(null);
            uv.setDefaultTextureId(-1); // default not use any texture.
            element.getFaces().forEach((dir, face) -> {
                if (face.getTextureId() < 0) {
                    return;
                }
                var rect = face.getRect();
                if (dir == OpenDirection.UP) {
                    var fixedRect = rect.copy();
                    fixedRect.setX(rect.getMaxX());
                    fixedRect.setY(rect.getMaxY());
                    fixedRect.setWidth(-rect.getWidth());
                    fixedRect.setHeight(-rect.getHeight());
                    rect = fixedRect;
                }
                if (dir == OpenDirection.DOWN) {
                    var fixedRect = rect.copy();
                    fixedRect.setX(rect.getMaxX());
                    fixedRect.setWidth(-rect.getWidth());
                    rect = fixedRect;
                }
                uv.put(dir, rect);
                uv.setRotation(dir, face.getRotation());
                uv.setTextureId(dir, face.getTextureId());
            });
            return uv;
        }

        // If the element is not a aligned size, the texture box needs to be rounded down.
        public static boolean isAlignedSize(BlockBenchCube element) {
            var size = element.getFrom().subtracting(element.getTo());
            return (size.getX() % 1 == 0) && (size.getY() % 1 == 0) && (size.getZ() % 1 == 0);
        }

        public void forEach(BiConsumer<OpenDirection, Rectangle2f> consumer) {
            if (base == null) {
                rects.forEach(consumer);
            }
        }

        public void forEachRotations(BiConsumer<OpenDirection, Integer> consumer) {
            if (rotations != null) {
                rotations.forEach(consumer);
            }
        }

        public void forEachTextures(BiConsumer<OpenDirection, Integer> consumer) {
            if (textureIds != null) {
                textureIds.forEach(consumer);
            }
        }

        public void put(OpenDirection dir, Rectangle2f rect) {
            rects.put(dir, rect);
        }

        public void setRotation(OpenDirection dir, int rotation) {
            if (rotation == 0) {
                return;
            }
            if (rotations == null) {
                rotations = new EnumMap<>(OpenDirection.class);
            }
            rotations.put(dir, rotation);
        }

        public int getRotation(OpenDirection dir) {
            if (rotations != null) {
                return rotations.getOrDefault(dir, 0);
            }
            return 0;
        }

        public Vector2f getBase() {
            return base;
        }

        @Nullable
        public Rectangle2f getRect(OpenDirection dir) {
            return rects.get(dir);
        }

        public void setTextureId(OpenDirection dir, int textureId) {
            if (textureIds == null) {
                textureIds = new EnumMap<>(OpenDirection.class);
            }
            textureIds.put(dir, textureId);
        }

        public int getTextureId(OpenDirection dir) {
            if (textureIds != null) {
                return textureIds.getOrDefault(dir, defaultTextureId);
            }
            return defaultTextureId;
        }

        public void setDefaultTextureId(int defaultTextureId) {
            this.defaultTextureId = defaultTextureId;
        }

        public int getDefaultTextureId() {
            return defaultTextureId;
        }
    }

    protected static class TextureResolution {

        public static void apply(SkinTextureData data) {
            var base = by(data);
            var variants = data.getVariants();
            if (variants.isEmpty()) {
                data.setVariants(Collections.emptyList());
                return;
            }
            var secondaryTextures = new LinkedHashMap<Integer, SkinTextureData>();
            var additionalTextures = new LinkedHashMap<Integer, List<SkinTextureData>>();
            secondaryTextures.put(base & 0xf0, data);
            for (var variant : variants) {
                var key = by(variant);
                if ((key & 0x0f) == 0) {
                    // is secondary texture.
                    secondaryTextures.putIfAbsent(key & 0xf0, variant);
                } else {
                    // is additional texture.
                    additionalTextures.computeIfAbsent(key & 0xf0, k -> new ArrayList<>()).add(variant);
                }
            }
            secondaryTextures.forEach((key, provider) -> {
                var used = by(provider) & 0x0f;
                var values = additionalTextures.getOrDefault(key, new ArrayList<>());
                var iterator = values.iterator();
                while (iterator.hasNext()) {
                    var ck = by(iterator.next()) & 0x0f;
                    if ((used & ck) == ck) {
                        iterator.remove();
                    }
                    used |= ck;
                }
                provider.setVariants(values);
            });
            var newVariants = new ArrayList<>(secondaryTextures.values());
            newVariants.remove(data);
            newVariants.addAll(data.getVariants());
            data.setVariants(newVariants);
        }

        public static int by(SkinTextureData data) {
            int key = 0;
            var properties = data.getProperties();
            if (properties.isEmissive()) {
                key |= 0x10;
            }
            if (properties.isParticle()) {
                key |= 0x20;
            }
            if (properties.isNormal()) {
                key |= 0x01;
            }
            if (properties.isSpecular()) {
                key |= 0x02;
            }
            return key;
        }

        public static void applyBoundary(SkinTextureData textureProvider, float u, float v) {
            // the uv is over boundary?
            if (u < 0 || v < 0 || u > textureProvider.getWidth() || v > textureProvider.getHeight()) {
                var properties = (SkinTextureProperties) textureProvider.getProperties();
                properties.setClampToEdge(true);
            }
        }
    }
}
