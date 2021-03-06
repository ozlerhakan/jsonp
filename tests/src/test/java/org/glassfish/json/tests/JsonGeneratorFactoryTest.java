/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
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

import junit.framework.TestCase;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests JsonGeneratorFactory
 *
 * @author Jitendra Kotamraju
 */
public class JsonGeneratorFactoryTest extends TestCase {

    public JsonGeneratorFactoryTest(String testName) {
        super(testName);
    }

    public void testGeneratorFactory() {
        JsonGeneratorFactory generatorFactory = Json.createGeneratorFactory(null);

        JsonGenerator generator1 = generatorFactory.createGenerator(new StringWriter());
        generator1.writeStartArray().writeEnd();
        generator1.close();

        JsonGenerator generator2 = generatorFactory.createGenerator(new StringWriter());
        generator2.writeStartArray().writeEnd();
        generator2.close();
    }

    public void testGeneratorFactoryWithConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonGeneratorFactory generatorFactory = Json.createGeneratorFactory(config);
        Map<String, ?> config1 = generatorFactory.getConfigInUse();
        if (config1.size() != 1) {
            throw new JsonException("Expecting no of properties=1, got="+config1.size());
        }
        assertTrue(config1.containsKey(JsonGenerator.PRETTY_PRINTING));

        JsonGenerator generator1 = generatorFactory.createGenerator(new StringWriter());
        generator1.writeStartArray().writeEnd();
        generator1.close();

        JsonGenerator generator2 = generatorFactory.createGenerator(new StringWriter());
        generator2.writeStartArray().writeEnd();
        generator2.close();
    }

}
