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


import com.alexkudlick.authentication.models.AuthenticationRequest;
import com.alexkudlick.authentication.application.tokens.AuthenticationTokenManager;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

@Path("/api/tokens/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationTokenResource {

    private final AuthenticationTokenManager manager;

    public AuthenticationTokenResource(AuthenticationTokenManager manager) {
        this.manager = Objects.requireNonNull(manager);
    }

    @POST
    @UnitOfWork(readOnly = true)
    public Response createToken(@Valid @NotNull AuthenticationRequest request) {
        return manager.login(request.getUserName(), request.getPassword())
            .map(token -> Response.ok().entity(token).build())
            .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST).build());
    }

    @GET
    @Path("{token}/")
    public Response checkTokenValidity(@NotEmpty @PathParam("token") String token) {
        if (manager.isValid(token)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
