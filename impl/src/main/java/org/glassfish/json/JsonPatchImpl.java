package org.glassfish.json;

import java.util.Map;
import java.util.Arrays;

import javax.json.JsonPatch;
import javax.json.JsonArray;
import javax.json.JsonStructure;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValueReference;
import javax.json.JsonException;

public class JsonPatchImpl implements JsonPatch {

    private JsonArray patch;

    public JsonPatchImpl(JsonArray patch) {
        this.patch = patch;
    }

    @Override
    public JsonStructure apply(JsonStructure target) {

        JsonStructure result = target;

        for (JsonValue operation: patch) {
            if (operation.getValueType() != JsonValue.ValueType.OBJECT) {
                throw new JsonException("A JSON patch must be an array of JSON objects.");
            }
            result = apply(result, (JsonObject) operation);
        }
        return result;
    }

    private JsonStructure apply(JsonStructure source, JsonObject operation) {

        JsonStructure result = source;
        JsonValueReference[] pathRef = getReferences(source, operation, "path");
            
        switch (operation.getString("op")) {
            case "add":
                result = pathRef[0].add(getValue(operation));
                break;
            case "replace":
                result = pathRef[0].replace(getValue(operation));
                break;
            case "remove":
                result = pathRef[0].remove();
                break;
            case "copy":
                JsonValueReference[] fromRefs = getReferences(source, operation, "from");
                checkOverlap(operation);
                result = pathRef[0].add(fromRefs[0].get());
                break;
            case "move":
                fromRefs = getReferences(source, operation, "from");
                checkOverlap(operation);
                JsonValue value = fromRefs[0].get();
                result = fromRefs[0].remove();
                for (int i = 1; i < fromRefs.length; i++) {
                    result = fromRefs[i].replace(result);
                }
                pathRef = getReferences(result, operation, "path");
                result = pathRef[0].add(value);
                break;
            case "test":
                if (! getValue(operation).equals(pathRef[0].get())) {
                    throw new JsonException("The JSON patch operation 'test' failed.");
                }
                return source;
            default:
                throw new JsonException("Illegal value for the op member of the JSON patch operation: " + operation.getString("op"));
        }
        for (int i = 1; i < pathRef.length; i++) {
            result = pathRef[i].replace(result);
        }
        return result;
   }

    private JsonValueReference[] getReferences(JsonStructure source, JsonObject operation,
            String member) {
        String pointerString = operation.getString(member);
        if (pointerString == null) {
            missingMember(operation.getString("op"), member);
        }
        JsonPointerImpl pointer = new JsonPointerImpl(pointerString);
        return pointer.getReferences(source);
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

    private void checkOverlap(JsonObject operation) {
         if (operation.getString("path").startsWith(operation.getString("from"))) {
             throw new JsonException("The 'from' path of the patch operation " 
                         + operation.getString("op") + " contains the 'path' path");
         }
    }

}

