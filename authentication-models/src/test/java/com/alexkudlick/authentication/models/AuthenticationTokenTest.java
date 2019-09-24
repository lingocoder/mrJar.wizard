/**
 * A Gradle plugin that supports compiling, testing, assembling and maintaining Modular Multi-Release JAR Files.
 *
 * Copyright (C) 2019 lingocoder <plugins@lingocoder.com>
 *
 * This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0
 * International (CC BY-ND 4.0) License.
 *
 * This work is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)
 * License for more details.
 *
 * You should have received a copy of the Creative Commons
 * Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0) License
 * along with this program. To view a copy of this license,
 * visit https://creativecommons.org/licenses/by-nd/4.0/.
 */
package com.alexkudlick.authentication.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class AuthenticationTokenTest {


    @Test
    public void testDeserialize() throws IOException {
        String json = "{" +
            "\"token\":\"testToken\"" +
            "}";
        AuthenticationToken deserialized = new ObjectMapper().readValue(json, AuthenticationToken.class);
        assertEquals(
            new AuthenticationToken("testToken"),
            deserialized
        );
    }

    @Test
    public void testSerialize() throws IOException {
        AuthenticationToken token = new AuthenticationToken("testToken");
        String expectedJson = "{" +
            "\"token\":\"testToken\"" +
            "}";
        String serialized = new ObjectMapper().writeValueAsString(token);
        assertEquals(
            expectedJson,
            serialized
        );
    }

}
