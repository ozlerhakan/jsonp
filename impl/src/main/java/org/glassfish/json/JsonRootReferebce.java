package org.glassfish.json;

import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonException;

class JsonRootReference implements JsonReference {

    JsonStructure root;

    public JsonRootReference(JsonStructure root) {
        this.root = root;
    }

    @Override
    public JsonValue get() {
        return root;
    }

    @Override
    public JsonValue add(JsonValue value) {
        switch (value.getValueType() ) {
            case OBJECT:
            case ARRAY:
                this.root = (JsonStructure) value;
            default:
                throw new JsonException("The root value can only be added or replaced by a JSON object or array");
        }
    }

    @Override
    public JsonValue remove() {
        throw new JsonException("The JSON value at the root cannot be removed;");
    }

    @Override
    public JsonValue replace(JsonValue value) {
        return add(value);
    }
}
