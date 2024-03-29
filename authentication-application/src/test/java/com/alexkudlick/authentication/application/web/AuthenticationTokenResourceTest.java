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
package com.alexkudlick.authentication.application.web;


import com.alexkudlick.authentication.application.tokens.AuthenticationTokenManager;
import com.alexkudlick.authentication.models.AuthenticationRequest;
import com.alexkudlick.authentication.models.AuthenticationToken;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AuthenticationTokenResourceTest {

    private AuthenticationTokenManager manager;
    private AuthenticationTokenResource resource;

    @Before
    public void setUp() {
        manager = mock(AuthenticationTokenManager.class);
        resource = new AuthenticationTokenResource(manager);
    }

    @Test
    public void testCheckTokenValidityReturnsOkIfTokenIsValid() {
        when(manager.isValid(anyString())).thenReturn(true);

        Response response = resource.checkTokenValidity("21l2kjh253424");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        verify(manager).isValid("21l2kjh253424");
        verifyNoMoreInteractions(manager);
    }

    @Test
    public void testCheckTokenValidityReturnsNotFoundIfTokenIsNotValid() {
        when(manager.isValid(anyString())).thenReturn(false);

        Response response = resource.checkTokenValidity("lky4hq32kk");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        verify(manager).isValid("lky4hq32kk");
        verifyNoMoreInteractions(manager);
    }

    @Test
    public void testCreateTokenWithValidLogin() {
        AuthenticationRequest request = new AuthenticationRequest("asdf", "hijk");
        AuthenticationToken token = new AuthenticationToken("q2tkjh2kj3rh");

        when(manager.login(anyString(), anyString())).thenReturn(Optional.of(token));

        Response response = resource.createToken(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(token, response.getEntity());

        verify(manager).login(request.getUserName(), request.getPassword());
        verifyNoMoreInteractions(manager);
    }

    @Test
    public void testCreateTokenWithInvalidLogin() {
        AuthenticationRequest request = new AuthenticationRequest("asdf", "hijk");

        when(manager.login(anyString(), anyString())).thenReturn(Optional.empty());

        Response response = resource.createToken(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        verify(manager).login(request.getUserName(), request.getPassword());
        verifyNoMoreInteractions(manager);
    }
}