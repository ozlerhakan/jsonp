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

import java.util.Map;
import java.util.Arrays;

import javax.json.JsonPatch;
import javax.json.JsonPointer;
import javax.json.JsonArray;
import javax.json.JsonStructure;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonString;
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

    @Override
    public JsonObject apply(JsonObject target) {
        return (JsonObject) apply((JsonStructure)target);
    }

    @Override
    public JsonArray apply(JsonArray target) {
        return (JsonArray) apply((JsonStructure)target);
    }

    private JsonStructure apply(JsonStructure target, JsonObject operation) {

        JsonPointer pointer = getPointer(operation, "path");
        JsonPointer from;
        switch (operation.getString("op")) {
            case "add":
                return pointer.add(target, getValue(operation));
            case "replace":
                return pointer.replace(target, getValue(operation));
            case "remove":
                return pointer.remove(target);
            case "copy":
                checkOverlap(operation);
                from = getPointer(operation, "from");
                return pointer.add(target, from.getValue(target));
            case "move":
                checkOverlap(operation);
                from = getPointer(operation, "from");
                return pointer.add(from.remove(target), from.getValue(target));
            case "test":
                if (! getValue(operation).equals(pointer.getValue(target))) {
                    throw new JsonException("The JSON patch operation 'test' failed.");
                }
                return target;
            default:
                throw new JsonException("Illegal value for the op member of the JSON patch operation: " + operation.getString("op"));
        }
   }

    private JsonPointer getPointer(JsonObject operation, String member) {
        JsonString pointerString = operation.getJsonString(member);
        if (pointerString == null) {
            missingMember(operation.getString("op"), member);
        }
        return new JsonPointerImpl(pointerString.getString());
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

