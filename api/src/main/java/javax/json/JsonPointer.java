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
 * This interface represents a JSON Pointer as specified in
 * <a href="http://tools.ietf.org/html/rfc6901">RFC 6901</a>.
 * <p>An instance of JsonPointer can be created with the factory method 
 * {@link Json#createPointer}.</p>
 *
 * @author Kin-man Chung
 * @since 1.1
 */

public interface JsonPointer {

    /**
     * Evaluate the JSON Pointer with a reference to the specified JSON document
     * and return the JSON value identified by the JSON Pointer.
     *
     * @param target the JSON structure referenced by this JSON Pointer
     *
     * @return the JSON value referenced by the JSON Pointer
     */
    public JsonValue getValue(JsonStructure target);

    /**
     * <p>Evaluate the JSON Pointer with a reference to the specified JSON document
     * and return the references to the JSON values on the JSON Pointer path.
     * The first reference (index 0) is the last JSON value in a JSON object or array.
     * specified by the JSON path, and the last reference (index pathLength-1) is the
     * first JSON value in the target
     * JSON object or array.</p>
     * For instance, if the target is the following JSON:
     * <pre><code>
     *    [
     *       {"name": "Duke",
     *        "phones": {"home": "123-234-3456",
     *                   "cell": "666-777-8888"}},
     *       {"name": "Jane",
     *        "phones": {"cell": "987-654-3210"}}
     *    ]
     * </code></pre>
     * and the JSON Pointer is "/0/phones/cell", then references returned are:
     * <pre><code>
     * 0: "cell": "666-777-8888"
     * 1: "phones": {"home": "123-234-3456",
     *               "cell": "666-777-8888"}
     * 2: {"name": "Duke",
     *     "phones": {"home": "123-234-3456",
     *                "cell": "666-777-8888"}}
     * </code></pre>
     *
     * @param target the JSON structure refernced by this JSON Pointer
     * @return an array of {@code JsonValueReference} representing the references to
     *    the values on the JSON Pointer path
     * @throws JsonException if the evaluation of the JSON Pointer with the target
     *    cannot continue due to a non-existing mapping (except for the last mapping
     *    which can be non-existing)
     * @throws IndexOutOfBoundsException if the evaluation of the JSON Pointer with the
     *    target cannot continue due to out of bounds index of an array ( except for
     *    the last index, which can be a "-"
     */
    public JsonValueReference[] getReferences(JsonStructure target);
}
