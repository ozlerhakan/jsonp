package org.glassfish.json;

import java.util.List;
import javax.json.JsonPointer;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonStructure;
import javax.json.JsonException;

public class JsonPointerImpl implements JsonPointer {
    private String[] tokens;
    private JsonStructure target;

    public JsonPointerImpl(JsonStructure target, String jsonPointer) {
        this.target = target;
        parse(jsonPointer);
    }

    @Override
    public JsonValue getValue() {
        return getReference().get();
    }

    JsonReference getReference() {

        JsonValue value = target;

        // First check if this is a reference to a JSON value tree
        if (tokens.length == 1) {
            return new JsonRootReference(target);
        }

        // Skip the root
        for (int i = 1; i < tokens.length; i++) {
            switch (value.getValueType()) {
                case OBJECT:
                    if (i == tokens.length - 1) {
                        return new JsonObjectReference((JsonObject)value, tokens[i]);
                    }
                    value = ((JsonObject)value).get(tokens[i]);
                    break;
                case ARRAY:
                    if (i == tokens.length - 1) {
                        return new JsonArrayReference((JsonArray)value, getIndex(tokens[i]));
                    }
                    value = ((JsonArray)value).get(getIndex(tokens[i]));
                    break;
                default:
                    throw new JsonException("The reference value in a Json pointer must be a Json object or a Json array");
             }
        }
        return null;
    }

    static int getIndex(String token) {
        if (token == null || token.length() == 0) {
            throw new JsonException("Array index format error");
        }
        if (token.equals("-")) {
            return -1;
        }
        if (token.equals("0")) {
            return 0;
        }
        if (token.charAt(0) == '+' || token.charAt(0) == '-') {
            throw new JsonException("Array index format error");
        }
        return Integer.parseInt(token);
    }

    private void parse(String jsonPointer) {
        tokens = jsonPointer.split("/", -1);  // keep the trailing blanks
        if (! "".equals(tokens[0])) {
            throw new JsonException("A JSON pointer must begin with a reference to the root of a JSON value object");
        }
        for (int i = 1; i < tokens.length; i++) {
            tokens[i] = tokens[i].replaceAll("~1", "/").replaceAll("~0","~");
        }
    }

}
