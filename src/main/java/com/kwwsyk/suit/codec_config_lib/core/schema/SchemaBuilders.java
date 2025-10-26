package com.kwwsyk.suit.codec_config_lib.core.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Lifecycle;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility methods that convert JSON snapshots produced by codecs into {@link OptionSchema}
 * instances. The builder records every node in a lookup table to support quick path based
 * navigation.
 */
public final class SchemaBuilders {

    private SchemaBuilders() {
    }

    /**
     * Builds a schema tree from a JSON element.
     *
     * @param jsonElement source element.
     * @return schema node representing the provided JSON.
     */
    public static Node buildTree(JsonElement jsonElement, Map<ValuePath, Node> lookup) {
        Objects.requireNonNull(jsonElement, "jsonElement");
        Objects.requireNonNull(lookup, "lookup");
        ValuePath rootPath = ValuePath.root();
        return visit(rootPath, jsonElement, lookup);
    }

    private static Node visit(ValuePath path, JsonElement element, Map<ValuePath, Node> lookup) {
        if (element == null || element instanceof JsonNull) {
            Node node = new LeafNode(path, NodeKind.NULL, JsonNull.INSTANCE, Lifecycle.experimental());
            lookup.put(path, node);
            return node;
        }
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            NodeKind kind = determinePrimitiveKind(primitive);
            Node node = new LeafNode(path, kind, primitive, Lifecycle.stable());
            lookup.put(path, node);
            return node;
        }
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            List<Node> children = new LinkedList<>();
            for (int i = 0; i < array.size(); i++) {
                JsonElement child = array.get(i);
                ValuePath childPath = path.child(i);
                children.add(visit(childPath, child, lookup));
            }
            Node node = new ListNode(path, array, Lifecycle.stable(), children);
            lookup.put(path, node);
            return node;
        }
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            Map<String, Node> children = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                ValuePath childPath = path.child(entry.getKey());
                Node child = visit(childPath, entry.getValue(), lookup);
                children.put(entry.getKey(), child);
            }
            Node node = new GroupNode(path, object, Lifecycle.stable(), children);
            lookup.put(path, node);
            return node;
        }
        throw new IllegalStateException("Unsupported JSON element: " + element);
    }

    private static NodeKind determinePrimitiveKind(JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return NodeKind.BOOL;
        }
        if (primitive.isNumber()) {
            return NodeKind.NUMBER;
        }
        return NodeKind.STRING;
    }
}
