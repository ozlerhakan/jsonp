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
package javax.json;

class JsonValueReferences {

    static class RootReference implements JsonValueReference {

        private JsonStructure root;

        RootReference(JsonStructure root) {
            this.root = root;
        }

        @Override
        public JsonValue get() {
            return root;
        }

        @Override
        public JsonStructure add(JsonValue value) {
            switch (value.getValueType() ) {
                case OBJECT:
                case ARRAY:
                    this.root = (JsonStructure) value;
                    break;
                default:
                    throw new JsonException("The root value only allows adding a JSON object or array");
            }
            return root;
        }

        @Override
        public JsonStructure remove() {
            throw new JsonException("The JSON value at the root cannot be removed;");
        }

        @Override
        public JsonStructure replace(JsonValue value) {
            return add(value);
        }
    }

    static class ObjectReference implements JsonValueReference {

        private JsonObject object;
        private String key;
        
        ObjectReference(JsonObject object, String key) {
            this.object = object;
            this.key = key;
        }
        
        @Override
        public JsonValue get() {
            return object.get(key);
        }       
                
        @Override
        public JsonObject add(JsonValue value) {
            return Json.createObjectBuilder(object)
                    .add(key, value)
                    .build();
        }
    
        @Override 
        public JsonObject remove() {
            if (! object.containsKey(key)) {
                throw new JsonException("Cannot remove a non-existing name/value pair in the object");
            }
            return Json.createObjectBuilder(object)
                    .remove(key)
                    .build();
        }   
    
        @Override
        public JsonObject replace(JsonValue value) {
            if (! object.containsKey(key)) {
                throw new JsonException("Cannot replace a non-existing name/value pair in the object");
            }
            return add(value);
        }
    }

    static class ArrayReference implements JsonValueReference {
     
        private JsonArray array;
        private int index;
    
        ArrayReference(JsonArray array, int index) {
            this.array = array;
            this.index = index;
        }
    
        @Override
        public JsonValue get() {
            return array.get(index);
        }
    
        @Override 
        public JsonStructure add(JsonValue value) {
            JsonArrayBuilder builder = Json.createArrayBuilder(this.array);
            if (index == -1) {
                builder.add(value);
            } else {
                builder.add(index, value);
            }
            return builder.build();
        }       
        
        @Override
        public JsonStructure remove() {
            if (index < 0 || index >= array.size()) {
                throw new JsonException("Cannot remove an array item with an out of range index");
            }
            JsonArrayBuilder builder = Json.createArrayBuilder(this.array);
            return builder.remove(index).build();
        }

        @Override
        public JsonStructure replace(JsonValue value) {
            if (index < 0 || index >= array.size()) {
                throw new JsonException("Cannot replace an array item with an out of range index");
            }
            JsonArrayBuilder builder = Json.createArrayBuilder(this.array);
            return builder.set(index, value).build();
        }
    }

}

