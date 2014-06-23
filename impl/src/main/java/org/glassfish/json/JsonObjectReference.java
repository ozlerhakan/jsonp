package org.glassfish.json;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonException;

class JsonObjectReference implements JsonReference {

    private JsonObject object;
    private String key;

    public JsonObjectReference(JsonObject object, String key) {
        this.object = object;
        this.key = key;
    }

    @Override
    public JsonValue get() {
        return object.get(key);
    }

    @Override
    public void add(JsonValue value) {
        object.put(key, value);
    }

    @Override
    public JsonValue remove() {
        if (! object.containsKey(key)) {
            throw new JsonException("Cannot remove a non-existing object entry");
        }
        return object.remove(key);
    }

    @Override
    public JsonValue replace(JsonValue value) {
        if (! object.containsKey(key)) {
            throw new JsonException("Cannot replace a non-existing object entry");
        }
        return object.put(key, value);
    }
}
