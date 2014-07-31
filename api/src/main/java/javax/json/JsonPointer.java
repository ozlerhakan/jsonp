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

/**
 * <p>This interface represents a JSON Pointer as specified in
 * <a href="http://tools.ietf.org/html/rfc6901">RFC 6901</a>.
 * A {@code JsonPointer}, when applied to a target
 * {@code JsonStructure}, defines either a reference to the whole
 * target {@code JsonStructure} (when the Pointer string is empty),
 * or a reference to a value in the JSON object or array 
 * (when the Pointer string is a sequence of '/' prefixed tokens).</p>
 * <p> The method {@link JsonPointer#getValue getValue()} returns the referenced value.
 * The methods {@link JsonPointer#add add()}, {@link JsonPointer#replace replace()},
 * and {@link JsonPointer#remove remove()} executes the operations specified in 
 * <a href="http://tools.ietf.org/html/rfc6902">RFC 6902</a>. </p>
 * 
 * <p>An instance of JsonPointer can be created with the factory method 
 * {@link Json#createPointer}.</p>
 *
 * @author Kin-man Chung
 * @since 1.1
 */

public interface JsonPointer {

    /**
     * Return the value at the referenced location
     * in the specified {@code JsonStructure}
     *
     * @param target the {@code JsonStructure} referenced by this {@code JsonPointer}
     *
     * @return the {@code JsonValue} referenced by this {@code JsonPointer}
     */
    public JsonValue getValue(JsonStructure target);

    /**
     * Add or replace a value at the referenced location
     * in the specified {@code JsonStructure}.
     * <ol>
     * <li>If the reference is the target {@code JsonStructure},
     * the value, which must be the same type as the target,
     * is returned.</li>
     * <li>If the reference is an index to an array, the value is inserted
     * into the array at the index.  If the index is specified with a "-",
     * or if the index is equal to the size of the array,
     * the value is appended to the array.</li>
     * <li>If the reference is a name of a {@code JsonObject}, and the
     * referenced value exists, the value is replaced by the specified value.
     * If it does not exists, a new name/value pair is added to the object.
     * </li>
     * </ol>
     *
     * @param target the target {@code JsonStructure}
     * @param value the value to be added
     * @return the resultant JsonStructure
     * @throws IndexOutOfBoundsException if the index to the array is out of range
     */
    public JsonStructure add(JsonStructure target, JsonValue value);

    /**
     * Replace the value at the referenced location 
     * in the specified {@code JsonStructure} with the specified value.
     *
     * @param target the target {@code JsonStructure}
     * @param value the value to be stored at the referenced location
     * @return the resultant JsonStructure
     * @throws JsonException if the value does not exists,
     *    or if the reference is the target.
     */
    public JsonStructure replace(JsonStructure target, JsonValue value);

    /**
     * Remove the value at the reference location 
     * in the specified {@code JsonStructure}
     *
     * @param target the target {@code JsonStructure}
     * @return the resultant JsonStructure
     * @throws JsonException if the referenced value does not exists,
     *    or if the reference is the target.
     */
    public JsonStructure remove(JsonStructure target);

    /**
     * Add or replace a value at the referenced location
     * in the specified {@code JsonObject}.
     * If the reference is the target {@code JsonStructure},
     * the value, which must be a {@code JsonObject}, is returned.
     * If the referenced value exists in the target object, it is replaced
     * by the specified value. If it does not exists, a new name/value pair
     * is added to the object.
     *
     * @param target the target {@code JsonObject}
     * @param value the value to be added
     * @return the resultant {@code JsonObject}
     */
    public JsonObject add(JsonObject target, JsonValue value);

    /**
     * Insert a value at the referenced location
     * in the specified {@code JsonArray}.
     * If the reference is the target {@code JsonStructure},
     * the value, which must be a {@code JsonArray}, is returned.
     * If the reference is an index of an array, the value is inserted
     * into the array at the index.  If the index is specified with a "-",
     * or if the index is equal to the size of the array,
     * the value is appended to the array.
     *
     * @param target the target {@code JsonArray}
     * @param value the value to be added
     * @return the resultant {@code JsonArray}
     * @throws IndexOutOfBoundsException if the index to the array is out of range
     */
    public JsonArray add(JsonArray target, JsonValue value);

    /**
     * Replace the value at the referenced location
     * in the specified {@code JsonObject} with the specified value.
     *
     * @param target the target {@code JsonObject}
     * @param value the value be stored at the referenced location
     * @return the resultant JsonObject
     * @throws JsonException if the value does not exists,
     *    or if the reference is the target.
     */
    public JsonObject replace(JsonObject target, JsonValue value);

    /**
     * Replace the value at the referenced location
     * in the specified {@code JsonArray} with the specified value.
     *
     * @param target the target {@code JsonArray}
     * @param value the value to be stored at the referenced location
     * @return the resultant JsonArray
     * @throws JsonException if the value does not exists,
     *    or if the reference is the target.
     */
    public JsonArray replace(JsonArray target, JsonValue value);

    /**
     * Remove the value at the referenced location
     * in the specified {@code JsonObject}
     *
     * @param target the target {@code JsonObject}
     * @return the resultant JsonObject
     * @throws JsonException if the referenced value does not exists,
     *    or if the reference is the target.
     */
    public JsonObject remove(JsonObject target);

    /**
     * Remove the value at the referenced location
     * in the specified {@code JsonArray}
     *
     * @param target the target {@code JsonJsonArray}
     * @return the resultant JsonArray
     * @throws JsonException if the referenced value does not exists,
     *    or if the reference is the target.
     */
    public JsonArray remove(JsonArray target);
}
