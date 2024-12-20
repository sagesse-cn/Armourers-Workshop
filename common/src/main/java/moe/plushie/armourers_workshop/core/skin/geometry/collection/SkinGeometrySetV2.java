package moe.plushie.armourers_workshop.core.skin.geometry.collection;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometry;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometrySet;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.geometry.mesh.SkinMesh;
import moe.plushie.armourers_workshop.core.skin.geometry.mesh.SkinMeshFace;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureBox;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SkinGeometrySetV2 extends SkinGeometrySet<SkinGeometry> {

    private final ArrayList<SkinGeometry> entities = new ArrayList<>();

    public void addBox(Box box) {
        entities.add(box);
    }

    public void addMesh(Mesh mesh) {
        entities.add(mesh);
    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public SkinGeometry get(int index) {
        return entities.get(index);
    }

    @Override
    public Collection<ISkinGeometryType> getSupportedTypes() {
        return Collections.singleton(SkinGeometryTypes.CUBE);
    }

    public static class Box extends SkinCube {

        private final SkinTextureBox skyBox;

        public Box(OpenRectangle3f boundingBox, OpenTransform3f transform, SkinTextureBox skyBox) {
            this.transform = transform;
            this.boundingBox = boundingBox;
            this.skyBox = skyBox;
        }

        @Override
        public ISkinGeometryType getType() {
            return SkinGeometryTypes.CUBE;
        }

        @Override
        public SkinPaintColor getPaintColor(OpenDirection dir) {
            return SkinPaintColor.WHITE;
        }

        @Override
        public SkinTexturePos getTexture(OpenDirection dir) {
            return skyBox.getTexture(dir);
        }

        @Override
        public SkinCubeFace getFace(OpenDirection dir) {
            if (getTexture(dir) != null) {
                return super.getFace(dir);
            }
            return null;
        }
    }

    public static class Mesh extends SkinMesh {

        private final List<SkinMeshFace> faces;

        public Mesh(OpenTransform3f transform, SkinTexturePos texturePos, List<SkinMeshFace> faces) {
            this.transform = transform;
            this.texturePos = texturePos;
            this.faces = faces;
        }

        @Override
        public List<SkinMeshFace> getFaces() {
            return faces;
        }
    }
}
