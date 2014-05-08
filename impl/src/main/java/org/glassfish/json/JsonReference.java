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

import javax.json.JsonValue;

/**
 * <p>This class encapsulates a reference to the root of a JSON value tree,
 * a member of a JSON object, or a member of a JSON array.</p>
 * <p>For a root of a JSON value tree, the reference is the JSON value itself.
 * For a JSON object, the reference is the member identified by a key.
 * For a JSON array, the reference is the array element indentified by an index.</p>
 * <p>The value of a reference can be retrieved or replaced.
 * With a reference to a JSON object member or JSON array member, its value can be
 * removed, or if the referenced value does not exist, a new value can be added to the
 * JSON object or JSON array.</p>
 *
 * @author Kin-man Chung
 */
public interface JsonReference {

    /**
     * Get the value at the referenced location
     */
    public JsonValue get();

    /**
     * Add or replace a value at the referenced location.
     * If the reference specifies an index to a JSON array, the value is inserted
     * into the array at the specified index.  If the index is -1, the value is
     * appended to the array.
     * If the reference is a key to a JSON object, the (key, value) pair is added
     * to the object, replacing any member with the same key.
     *
     * @throws JsonException if the index to the array is not -1 or is out of range
     */
    public JsonValue add(JsonValue value);

    /**
     * Remove the member specified by the reference
     *
     * throws JsonException if the referenced member does not exist
     */
    public JsonValue remove();

    /**
     * Replace the referenced member with the specified value.
     *
     * @throws JsonException if the referenced member does not exist
     */
    public JsonValue replace(JsonValue value);

}

