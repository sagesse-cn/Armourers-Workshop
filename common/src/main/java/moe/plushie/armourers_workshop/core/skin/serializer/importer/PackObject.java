package moe.plushie.armourers_workshop.core.skin.serializer.importer;

import com.google.gson.JsonElement;
import moe.plushie.armourers_workshop.core.math.Rectangle2f;
import moe.plushie.armourers_workshop.core.math.Size2f;
import moe.plushie.armourers_workshop.core.math.Size3f;
import moe.plushie.armourers_workshop.core.math.Vector2f;
import moe.plushie.armourers_workshop.core.math.Vector2i;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer2;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.MolangExpression;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;

public class PackObject implements IODataObject {

    private final JsonElement element;

    public PackObject(IODataObject object) {
        this.element = object.jsonValue();
    }

    @Nullable
    public static PackObject from(PackResource resource) throws IOException {
        if (resource == null) {
            return null;
        }
        try (var inputStream = new BufferedInputStream(resource.getInputStream())) {
            return new PackObject(JsonSerializer.readFromStream(inputStream));
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }

    public MolangExpression expression() {
        if (isNull()) {
            return null;
        }
        return new MolangExpression(stringValue());
    }

    public Vector2i vector2iValue() {
        var values = allValues();
        if (values.size() >= 2) {
            var iterator = values.iterator();
            return new Vector2i(iterator.next().intValue(), iterator.next().intValue());
        }
        return Vector2i.ZERO;
    }

    public Vector2f vector2fValue() {
        var values = allValues();
        if (values.size() >= 2) {
            var iterator = values.iterator();
            return new Vector2f(iterator.next().floatValue(), iterator.next().floatValue());
        }
        return Vector2f.ZERO;
    }

    public Size2f size2fValue() {
        var values = allValues();
        if (values.size() >= 2) {
            var iterator = values.iterator();
            return new Size2f(iterator.next().floatValue(), iterator.next().floatValue());
        }
        return Size2f.ZERO;
    }

    public Rectangle2f rectangle2fValue() {
        var values = allValues();
        if (values.size() >= 4) {
            var iterator = values.iterator();
            var x1 = iterator.next().floatValue();
            var y1 = iterator.next().floatValue();
            var x2 = iterator.next().floatValue();
            var y2 = iterator.next().floatValue();
            return new Rectangle2f(x1, y1, x2 - x1, y2 - y1);
        }
        return Rectangle2f.ZERO;
    }

    public Size3f size3fValue() {
        var values = allValues();
        if (values.size() >= 3) {
            var iterator = values.iterator();
            return new Size3f(iterator.next().floatValue(), iterator.next().floatValue(), iterator.next().floatValue());
        }
        return Size3f.ZERO;
    }

    public Vector3f vector3fValue() {
        var values = allValues();
        if (values.size() >= 3) {
            var iterator = values.iterator();
            return new Vector3f(iterator.next().floatValue(), iterator.next().floatValue(), iterator.next().floatValue());
        }
        return moe.plushie.armourers_workshop.core.math.Vector3f.ZERO;
    }

    public void at(String keyPath, IOConsumer<PackObject> consumer) throws IOException {
        var object = by(keyPath);
        if (object.isNull()) {
            return;
        }
        consumer.accept(new PackObject(by(keyPath)));
    }

    public void each(String keyPath, IOConsumer<PackObject> consumer) throws IOException {
        var object = by(keyPath);
        if (object.isNull()) {
            return;
        }
        for (var value : object.allValues()) {
            consumer.accept(new PackObject(value));
        }
    }

    public void each(String keyPath, IOConsumer2<String, PackObject> consumer) throws IOException {
        var object = by(keyPath);
        if (object.isNull()) {
            return;
        }
        for (var pair : object.entrySet()) {
            consumer.accept(pair.getKey(), new PackObject(pair.getValue()));
        }
    }

    @Override
    public PackObject at(int index) {
        return new PackObject(IODataObject.super.at(index));
    }

    @Override
    public PackObject get(String key) {
        return new PackObject(IODataObject.super.get(key));
    }

    public PackObject by(String keyPath) {
        // when this is a full key, ignore.
        if (has(keyPath)) {
            return get(keyPath);
        }
        var keys = keyPath.split("\\.");
        PackObject object = this;
        for (String key : keys) {
            object = object.get(key);
        }
        return object;
    }

    @Override
    public JsonElement jsonValue() {
        return element;
    }
}
