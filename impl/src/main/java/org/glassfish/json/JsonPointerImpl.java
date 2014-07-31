/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

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

    public JsonPointerImpl(String jsonPointer) {
        parse(jsonPointer);
    }

    @Override
    public JsonValue getValue(JsonStructure target) {
        JsonValueReference[] pathRef = getReferences(target);
        return pathRef[0].get();
    }

    @Override
    public JsonStructure add(JsonStructure target, JsonValue value) {
        JsonValueReference[] pathRef = getReferences(target);
        JsonStructure result = pathRef[0].add(value);
        for (int i = 1; i < pathRef.length; i++) {
            result = pathRef[i].replace(result);
        }
        return result;
    }

    @Override
    public JsonStructure replace(JsonStructure target, JsonValue value) {
        JsonValueReference[] pathRef = getReferences(target);
        JsonStructure result = pathRef[0].replace(value);
        for (int i = 1; i < pathRef.length; i++) {
            result = pathRef[i].replace(result);
        }
        return result;
    }

    @Override
    public JsonStructure remove(JsonStructure target) {
        JsonValueReference[] pathRef = getReferences(target);
        JsonStructure result = pathRef[0].remove();
        for (int i = 1; i < pathRef.length; i++) {
            result = pathRef[i].replace(result);
        }
        return result;
    }

    @Override
    public JsonObject add(JsonObject target, JsonValue value) {
        return (JsonObject) add((JsonStructure) target, value);
    }

    @Override
    public JsonArray add(JsonArray target, JsonValue value) {
        return (JsonArray) add((JsonStructure) target, value);
    }

    @Override
    public JsonObject replace(JsonObject target, JsonValue value) {
        return (JsonObject) replace((JsonStructure) target, value);
    }

    @Override
    public JsonArray replace(JsonArray target, JsonValue value) {
        return (JsonArray) replace((JsonArray) target, value);
    }

    @Oerride
    public JsonObject remove(JsonObject target) {
        return (JsonObject) remove((JsonObject) target);
    }

    @Override
    public JsonArray remove(JsonArray target) {
        return (JsonArray) remove((JsonArray) target);
    }

    /**
     * Compute the {@code JsonValueReference}s for each node on the path of
     * the JSON Pointer, in reverse order, starting from the leaf node
     */
    private JsonValueReference[] getReferences(JsonStructure target) {

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
                    JsonObject object = (JsonObject) value;
                    references[s-i-1] = JsonValueReference.of(object, tokens[i]);
                    if (i < s-1) {
                        // The last object mapping in the path needs not exist
                        value = object.get(tokens[i]);
                        if (value == null) {
                            // Except for the last name, the mapping must exist
                            throw new JsonException("The JSON object " + object + " contains no mapping "
                                 + " for the name " + tokens[i]);
                        }
                    }
                    break;
                case ARRAY:
                    int index = getIndex(tokens[i]);
                    JsonArray array = (JsonArray) value;
                    references[s-i-1] = JsonValueReference.of(array, index);
                    if (i < s-1 && index != -1) {
                        // The last array index in the path can have index value of -1
                        // ("-" in the JSON pointer)
                        value = array.get(index);
                    }
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
