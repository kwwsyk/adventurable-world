package com.kwwsyk.suit.codec_config_lib.core.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents a navigable path into a JSON tree. Each segment is either an object key or a
 * positional index inside a JSON array. The class offers factory helpers to build child paths
 * incrementally while preserving immutability, which makes it safe to reuse across schema
 * construction, UI binding and patch application.
 */
public final class ValuePath implements Iterable<ValuePath.Segment> {

    private static final ValuePath ROOT = new ValuePath(List.of());

    private final List<Segment> segments;

    private ValuePath(List<Segment> segments) {
        this.segments = segments;
    }

    /**
     * Creates a path that points to the root element.
     *
     * @return an empty path with no segments.
     */
    public static ValuePath root() {
        return ROOT;
    }

    /**
     * Creates a new path by appending an object key to this path.
     *
     * @param key object member key.
     * @return immutable child path instance.
     */
    public ValuePath child(String key) {
        Objects.requireNonNull(key, "key");
        List<Segment> copy = new ArrayList<>(segments.size() + 1);
        copy.addAll(segments);
        copy.add(Segment.ofKey(key));
        return new ValuePath(Collections.unmodifiableList(copy));
    }

    /**
     * Creates a new path by appending an array index to this path.
     *
     * @param index array position.
     * @return immutable child path instance.
     */
    public ValuePath child(int index) {
        List<Segment> copy = new ArrayList<>(segments.size() + 1);
        copy.addAll(segments);
        copy.add(Segment.ofIndex(index));
        return new ValuePath(Collections.unmodifiableList(copy));
    }

    /**
     * Returns true when the current path represents the root element.
     *
     * @return whether the path is empty.
     */
    public boolean isRoot() {
        return segments.isEmpty();
    }

    /**
     * Provides read-only access to the individual path segments.
     *
     * @return immutable view of the path segments.
     */
    public List<Segment> segments() {
        return segments;
    }

    @Override
    public Iterator<Segment> iterator() {
        return segments.iterator();
    }

    @Override
    public String toString() {
        if (segments.isEmpty()) {
            return "/";
        }
        StringJoiner joiner = new StringJoiner("/", "/", "");
        for (Segment segment : segments) {
            joiner.add(segment.toString());
        }
        return joiner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ValuePath valuePath)) {
            return false;
        }
        return segments.equals(valuePath.segments);
    }

    @Override
    public int hashCode() {
        return segments.hashCode();
    }

    /**
     * Represents a single segment in a {@link ValuePath}. Segments can be object keys or array indices.
     */
    public static final class Segment {

        private final String key;
        private final Integer index;

        private Segment(String key, Integer index) {
            this.key = key;
            this.index = index;
        }

        /**
         * Builds a key segment.
         *
         * @param key member name.
         * @return key segment instance.
         */
        public static Segment ofKey(String key) {
            Objects.requireNonNull(key, "key");
            return new Segment(key, null);
        }

        /**
         * Builds an index segment.
         *
         * @param index element position.
         * @return index segment instance.
         */
        public static Segment ofIndex(int index) {
            return new Segment(null, index);
        }

        /**
         * @return true when this segment targets an object member.
         */
        public boolean isKey() {
            return key != null;
        }

        /**
         * @return true when this segment targets an array position.
         */
        public boolean isIndex() {
            return index != null;
        }

        /**
         * @return segment key if present, otherwise {@code null}.
         */
        public String key() {
            return key;
        }

        /**
         * @return segment index if present, otherwise {@code null}.
         */
        public Integer index() {
            return index;
        }

        @Override
        public String toString() {
            if (isKey()) {
                return key;
            }
            return index == null ? "?" : index.toString();
        }
    }
}
