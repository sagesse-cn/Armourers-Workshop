package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.core.math.OpenRectangle2f;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.util.List;
import java.util.Map;

public class JointShape {

    private final OpenRectangle3f rect;
    private final ITransform3f transform;
    private final List<JointShape> children;
    private final Map<OpenDirection, OpenRectangle2f> uvs;

    public JointShape(OpenVector3f origin, OpenVector3f size, float inflate, ITransform3f transform, Map<OpenDirection, OpenRectangle2f> uvs, List<JointShape> children) {
        float x = origin.x() - inflate;
        float y = origin.y() - inflate;
        float z = origin.z() - inflate;
        float w = size.x() + inflate * 2;
        float h = size.y() + inflate * 2;
        float d = size.z() + inflate * 2;
        this.rect = new OpenRectangle3f(x, y, z, w, h, d);
        this.transform = transform;
        this.children = children;
        this.uvs = uvs;
    }

    public OpenRectangle2f getUV(OpenDirection dir) {
        if (uvs != null) {
            return uvs.get(dir);
        }
        return null;
    }

    public List<JointShape> children() {
        return children;
    }

    public ITransform3f transform() {
        return transform;
    }

    public OpenRectangle3f bounds() {
        return rect;
    }
}
