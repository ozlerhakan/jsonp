package org.glassfish.json;

import java.util.Map;

import javax.json.JsonPatch;
import javax.json.JsonArray;
import javax.json.JsonStructure;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonException;

public class JsonPatchImpl implements JsonPatch {

    private JsonArray patch;
    private JsonStructure target;

    public JsonPatchImpl(JsonStructure target, JsonArray patch) {
        this.target = target;
        this.patch = patch;
    }

    @Override
    public JsonStructure apply() {

        JsonStructure result = clone(target, true);

        for (JsonValue operation: patch) {
            if (operation.getValueType() != JsonValue.ValueType.OBJECT) {
                throw new JsonException("A JSON patch must be an array of JSON objects.");
            }
            apply(result, (JsonObject) operation);
        }
        return clone(result, false);
    }

    private JsonStructure clone(JsonStructure source, boolean mutable) {
        if (source.getValueType() == JsonValue.ValueType.OBJECT) {
            JsonObject object = (JsonObject) source;
            JsonObjectBuilderImpl objectBuilder = new JsonObjectBuilderImpl(new BufferPoolImpl());
            for (Map.Entry<String, JsonValue> entry: object.entrySet()) {
                String key = entry.getKey();
                JsonValue value = entry.getValue();
                switch (value.getValueType()) {
                    case OBJECT:
                    case ARRAY:
                        objectBuilder.add(key, clone((JsonStructure)value, mutable));
                        break;
                    default:
                        objectBuilder.add(key, value);
                }
            }
            return mutable? objectBuilder.mutableBuild(): objectBuilder.build();
        }
        JsonArray array = (JsonArray) source;
        JsonArrayBuilderImpl arrayBuilder = new JsonArrayBuilderImpl(new BufferPoolImpl());
        for (JsonValue value: array) {
            switch (value.getValueType()) {
                case OBJECT:
                case ARRAY:
                    arrayBuilder.add(clone((JsonStructure)value, mutable));
                    break;
                default:
                    arrayBuilder.add(value);
            }
        }
        return mutable? arrayBuilder.mutableBuild(): arrayBuilder.build();
    }     
            
    private void apply(JsonStructure source, JsonObject operation) {

        JsonReference pathRef = getReference(source, operation, "path");

        JsonReference fromRef;
        switch (operation.getString("op")) {
            case "add":
                pathRef.add(getValue(operation));
                break;
            case "remove":
                pathRef.remove();
                break;
            case "replace":
                pathRef.replace(getValue(operation));
                break;
            case "move":
                // TODO: check if "from" is a proper prefix of "path"
                fromRef = getReference(source, operation, "from");
                pathRef.add(fromRef.remove());
                break;
            case "copy":
                fromRef = getReference(target, operation, "from");
                pathRef.add(fromRef.get());
                break;
            case "test":
                if (! getValue(operation).equals(pathRef.get())) {
                    throw new JsonException("The JSON patch operation 'test' failed.");
                }
                break;
            default:
                throw new JsonException("Illegal value for the op member of the JSON patch operation: " + operation.getString("op"));
        }
    }

    private JsonReference getReference(JsonStructure source, JsonObject operation,
            String member) {
        String pointerString = operation.getString(member);
        if (pointerString == null) {
            missingMember(operation.getString("op"), member);
        }
        JsonPointerImpl pointer = new JsonPointerImpl(source, pointerString);
        return pointer.getReference();
    }

    private JsonValue getValue(JsonObject operation) {
        JsonValue value = operation.get("value");
        if (value == null) {
            missingMember(operation.getString("op"), "value");
        }
        return value;
    }

    private void missingMember(String op, String  member) {
        throw new JsonException(String.format("The JSON Patch operation %s must contain a %s member", op, member));
    }

}

