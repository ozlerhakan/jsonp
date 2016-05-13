/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.json.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonPointer;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by Hakan Özler on 5/13/2016.
 */
@RunWith(Parameterized.class)
public class JsonPointerContainsValueTest {

    private static JsonObject rfc6901Example;
    @Parameterized.Parameters(name = "{index}: ({0})={1}")
    public static Iterable<Object[]> data() throws Exception {
        rfc6901Example = org.glassfish.json.tests.JsonPointerTest.readRfc6901Example();
        return Arrays.asList(new Object[][]{
                {new JsonPointer(""), true, null},
                {new JsonPointer("/foo"), true, null},
                {new JsonPointer("/foo/0"), true, null},
                {new JsonPointer("/foo/1"), true, null},
                {new JsonPointer("/foo/5"), false, null},
                {new JsonPointer("/p/1"), false, null},
                {new JsonPointer("/p/q"), true, null},
                {new JsonPointer("/"), true, null},
                {new JsonPointer("/a~1b"), true, null},
                {new JsonPointer("/m~0n"), true, null},
                {new JsonPointer("/c%d"), true, null},
                {new JsonPointer("/e^f"), true, null},
                {new JsonPointer("/g|h"), true, null},
                {new JsonPointer("/i\\j"), true, null},
                {new JsonPointer("/k\"l"), true, null},
                {new JsonPointer("/ "), true, null},
                {new JsonPointer("/notexists"), false, null},
                {new JsonPointer("/s/t"), false, JsonException.class},
                {new JsonPointer("/s/0/t"), true, null},
                {new JsonPointer("/o"), true, null}
        });
    }

    private JsonPointer pointer;
    private boolean expectedValue;
    private Class<? extends Exception> expectedException;

    public JsonPointerContainsValueTest(JsonPointer pointer, boolean expectedValue, Class<? extends Exception> expectedException) {
        super();
        this.pointer = pointer;
        this.expectedValue = expectedValue;
        this.expectedException = expectedException;
    }

    @Test
    public void shouldEvaluateJsonPointerExpressions() {
        try {
            boolean result = pointer.containsValue(rfc6901Example);
            assertThat(result, is(expectedValue));
            assertThat(expectedException, nullValue());
        } catch(Exception e) {
            if(expectedException == null) {
                fail(e.getMessage());
            } else {
                assertThat(e, instanceOf(expectedException));
            }
        }
    }

}

