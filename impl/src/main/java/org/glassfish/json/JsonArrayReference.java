package org.glassfish.json;

import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.json.JsonException;

class JsonArrayReference implements JsonReference {
        
    private JsonArray array;
    private int index;

    public JsonArrayReference(JsonArray array, int index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public JsonValue get() {
        return array.get(index);
    }

    @Override
    public JsonValue add(JsonValue value) {
        if (index == -1) {
            array.add(value);
        } else {
            array.add(index, value);
        }
        return value;
    }

    @Override
    public JsonValue remove() {
        if (index < 0 || index >= array.size()) {
            throw new JsonException("Cannot remove an array item with an out of range index");
        }
        return array.remove(index);
    }

    @Override
    public JsonValue replace(JsonValue value) {
        if (index < 0 || index >= array.size()) {
            throw new JsonException("Cannot replace an array item with an out of range index");
        }
        return array.set(index, value);
    }
}

