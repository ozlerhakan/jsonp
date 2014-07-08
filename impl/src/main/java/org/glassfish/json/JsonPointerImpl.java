package org.glassfish.json;

import java.util.List;
import javax.json.JsonPointer;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonStructure;
import javax.json.JsonException;
import javax.json.JsonValueReference;

public class JsonPointerImpl implements JsonPointer {
    private String[] tokens;

    public JsonPointerImpl(String jsonPointer) {
        parse(jsonPointer);
    }

    @Override
    public JsonValue getValue(JsonStructure target) {
        JsonValueReference[] references = getReferences(target);
        return references[references.length - 1].get();
    }

    @Override
    public JsonValueReference[] getReferences(JsonStructure target) {

        JsonValueReference[] references;
        // First check if this is a reference to a JSON value tree
        if (tokens.length == 1) {
            references = new JsonValueReference[1];
            references[0] = JsonValueReference.of(target);
            return references;
        }

        references = new JsonValueReference[tokens.length-1];
        JsonValue value = target;
        int s = tokens.length;
        for (int i = 1; i < s; i++) {
             // Start with index 1, skipping the "" token
            switch (value.getValueType()) {
                case OBJECT:
                    references[s-i-1] = JsonValueReference.of((JsonObject)value, tokens[i]);
                    value = ((JsonObject)value).get(tokens[i]);
                    break;
                case ARRAY:
                    references[s-i-1] = JsonValueReference.of((JsonArray)value, getIndex(tokens[i]));
                    value = ((JsonArray)value).get(getIndex(tokens[i]));
                    break;
                default:
                    throw new JsonException("The reference value in a Json pointer must be a Json object or a Json array");
             }
        }
        return references;
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
