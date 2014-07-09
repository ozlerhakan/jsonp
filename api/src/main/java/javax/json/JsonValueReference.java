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
 * This class encapsulates a reference to a JSON value.
 * There are three types of references. 
 * <ol><li>a reference to the root of a JSON tree, i.e. the outermost JSON object or array.</li>
 *     <li>a reference to a name/value (possibly non-existing) pair of a JSON object, identified by a name.</li>
 *     <li>a reference to a member value of a JSON array, identified by an index.</li>
 * </ol>
 * Static factory methods are provided for creating these references.
 *
 * <p>A referenced value can be retrieved or replaced.
 * The value of a JSON object or JSON array can be
 * removed.  A new value can be added to a JSON object or
 * inserted into a JSON array</p>
 *
 * <p>Since a {@code JsonObject} or {@code JsonArray} is immutable, these operations 
 * must not modify the referenced JSON object or array. The methods {@link #add},
 * {@link #replace}, and {@link #remove} returns a new
 * JSON object or array after the execution of the operation.</p>
 *
 * @author Kin-man Chung
 *
 * @since 3.1
 */
public interface JsonValueReference {

    /**
     * Get the value at the referenced location.
     *
     * @return the JSON value referenced
     */
    public JsonValue get();

    /**
     * Add or replace a value at the referenced location.
     * If the reference is the root of a JSON tree, the added value must be
     * a JSON object or array, which becomes the referenced JSON value.
     * If the reference is an index of a JSON array, the value is inserted
     * into the array at the index.  If the index is -1, the value is
     * appended to the array.
     * If the reference is a name of a JSON object, the name/value pair is added
     * to the object, replacing any pair with the same name.
     *
     * @param value the value to be added
     * @return the JsonStructure after the operation
     * @throws JsonException if the index to the array is not -1 or is out of range
     */
    public JsonStructure add(JsonValue value);

    /**
     * Remove the name/value pair from the JSON object, or the value in a JSON array, as specified by the reference
     *
     * @return the JsonStructure after the operation
     * @throws JsonException if the name/value pair of the referenced JSON object
     *    does not exist, or if the index of the referenced JSON array is
     *    out of range, or if the reference is a root reference
     */
    public JsonStructure remove();

    /**
     * Replace the referenced value with the specified value.
     *
     * @param value the JSON value to be stored at the referenced location
     * @return the JsonStructure after the operation
     * @throws JsonException if the name/value pair of the referenced JSON object
     *    does not exist, or if the index of the referenced JSON array is
     *    out of range, or if the reference is a root reference
     */
    public JsonStructure replace(JsonValue value);

    /**
     * Returns a {@code JsonValueReference} for a {@code JsonStructure}.
     *
     * @param structure the {@code JsonStructure} referenced
     * @return the {@code JsonValueReference}
     */
    public static JsonValueReference of(JsonStructure structure) {
        return new JsonValueReferences.RootReference(structure);
    }

    /**
     * Returns a {@code JsonValueReference} for a name/value pair in a
     * JSON object.
     *
     * @param object the referenced JSON object
     * @param name the name of the name/pair
     * @return the {@code JsonValueReference} 
     */
    public static JsonValueReference of(JsonObject object, String name) {
        return new JsonValueReferences.ObjectReference(object, name);
    }

    /**
     * Returns a {@code JsonValueReference} for a member value in a
     * JSON array.
     *
     * @param array the referenced JSON array
     * @param index the index of the member value in the JSON array
     * @return the {@code JsonValueReference} 
     */
    public static JsonValueReference of(JsonArray array, int index) {
        return new JsonValueReferences.ArrayReference(array, index);
    }

}

