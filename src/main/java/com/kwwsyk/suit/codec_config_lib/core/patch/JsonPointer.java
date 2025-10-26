package com.kwwsyk.suit.codec_config_lib.core.patch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.kwwsyk.suit.codec_config_lib.core.schema.ValuePath;

import java.util.Objects;

/**
 * Minimal JSON pointer helper that understands {@link ValuePath}. It supports setting primitive
 * values, replacing sub-trees, and will create missing intermediate containers when required.
 */
public final class JsonPointer {

    private JsonPointer() {
    }

    /**
     * Performs a deep copy of a JSON element.
     *
     * @param element element to clone.
     * @return detached copy suitable for mutation.
     */
    public static JsonElement deepCopy(JsonElement element) {
        return element == null ? JsonNull.INSTANCE : element.deepCopy();
    }

    /**
     * Sets a value at the provided path, creating intermediate nodes if required.
     *
     * @param root  root element to mutate.
     * @param path  target path.
     * @param value replacement value or primitive.
     */
    public static void set(JsonElement root, ValuePath path, Object value) {
        Objects.requireNonNull(root, "root");
        Objects.requireNonNull(path, "path");
        JsonElement payload = toJsonElement(value);
        if (path.isRoot()) {
            throw new IllegalArgumentException("Cannot replace the root element using set(); supply a copy instead.");
        }
        JsonElement cursor = root;
        var segments = path.segments();
        for (int i = 0; i < segments.size() - 1; i++) {
            ValuePath.Segment segment = segments.get(i);
            ValuePath.Segment next = segments.get(i + 1);
            cursor = descend(cursor, segment, next);
        }
        ValuePath.Segment last = segments.getLast();
        apply(cursor, last, payload);
    }

    private static JsonElement descend(JsonElement current, ValuePath.Segment segment, ValuePath.Segment next) {
        if (segment.isKey()) {
            if (!current.isJsonObject()) {
                throw new IllegalStateException("Expected object at segment " + segment);
            }
            JsonObject object = current.getAsJsonObject();
            if (!object.has(segment.key()) || object.get(segment.key()).isJsonNull()) {
                object.add(segment.key(), instantiateIntermediate(next));
            }
            JsonElement value = object.get(segment.key());
            if (!isContainerCompatible(value, next)) {
                object.add(segment.key(), instantiateIntermediate(next));
                value = object.get(segment.key());
            }
            return value;
        }
        if (!current.isJsonArray()) {
            throw new IllegalStateException("Expected array at segment " + segment);
        }
        JsonArray array = current.getAsJsonArray();
        int index = segment.index();
        while (array.size() <= index) {
            array.add(JsonNull.INSTANCE);
        }
        JsonElement value = array.get(index);
        if (value.isJsonNull()) {
            JsonElement intermediate = instantiateIntermediate(next);
            array.set(index, intermediate);
            value = intermediate;
        }
        if (!isContainerCompatible(value, next)) {
            JsonElement intermediate = instantiateIntermediate(next);
            array.set(index, intermediate);
            value = intermediate;
        }
        return value;
    }

    private static boolean isContainerCompatible(JsonElement element, ValuePath.Segment next) {
        if (next == null) {
            return true;
        }
        if (next.isKey()) {
            return element.isJsonObject();
        }
        if (next.isIndex()) {
            return element.isJsonArray();
        }
        return true;
    }

    private static JsonElement instantiateIntermediate(ValuePath.Segment next) {
        if (next == null) {
            return JsonNull.INSTANCE;
        }
        if (next.isKey()) {
            return new JsonObject();
        }
        if (next.isIndex()) {
            return new JsonArray();
        }
        return JsonNull.INSTANCE;
    }

    private static void apply(JsonElement parent, ValuePath.Segment segment, JsonElement payload) {
        if (segment.isKey()) {
            if (!parent.isJsonObject()) {
                throw new IllegalStateException("Expected object parent for key segment " + segment);
            }
            parent.getAsJsonObject().add(segment.key(), payload);
            return;
        }
        if (!parent.isJsonArray()) {
            throw new IllegalStateException("Expected array parent for index segment " + segment);
        }
        JsonArray array = parent.getAsJsonArray();
        int index = segment.index();
        while (array.size() <= index) {
            array.add(JsonNull.INSTANCE);
        }
        array.set(index, payload);
    }

    /**
     * Converts user facing values into JSON elements for patching.
     *
     * @param value arbitrary primitive or {@link JsonElement}.
     * @return normalized JSON element.
     */
    public static JsonElement toJsonElement(Object value) {
        if (value instanceof JsonElement element) {
            return element;
        }
        if (value instanceof String string) {
            return new JsonPrimitive(string);
        }
        if (value instanceof Number number) {
            return new JsonPrimitive(number);
        }
        if (value instanceof Boolean bool) {
            return new JsonPrimitive(bool);
        }
        if (value == null) {
            return JsonNull.INSTANCE;
        }
        throw new IllegalArgumentException("Unsupported patch value type: " + value.getClass());
    }
}
