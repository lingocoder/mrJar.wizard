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
package com.alexkudlick.authentication.client;

import com.alexkudlick.authentication.models.AuthenticationToken;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class AuthenticationClientApplication {

    public static final String URL_ENV_VAR_NAME = "AUTHENTICATION_URL";

    public static void main(String[] args) throws IOException {
        AuthenticationClient client = createClient();
        try {
            String command = args[0];
            if ("--create".equals(command)) {
                createUser(client, args[1], args[2]);
            } else if ("--login".equals(command)) {
                login(client, args[1], args[2]);
            } else if ("--check".equals(command)) {
                check(client, args[1]);
            } else {
                printHelp();
                System.exit(1);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            printHelp();
            System.exit(1);
        } catch (DuplicationUserNameException e) {
            System.out.println("Duplicate user name " + args[1]);
            System.exit(1);
        }
    }

    private static void check(AuthenticationClient client, String token) throws IOException {
        if (client.isValid(new AuthenticationToken(token))) {
            System.out.println("ok");
        } else {
            System.out.println("invalid");
            System.exit(1);
        }
    }

    private static void login(AuthenticationClient client, String userName, String password) throws IOException {
        Optional<AuthenticationToken> authenticationToken = client.tryLogin(userName, password);
        if (authenticationToken.isPresent()) {
            System.out.println(authenticationToken.get().getToken());
        } else {
            System.out.println("Invalid login");
            System.exit(1);
        }
    }

    private static void createUser(AuthenticationClient client, String userName, String password)
        throws IOException, DuplicationUserNameException
    {
        client.createUser(userName, password);
        System.out.println("ok");
    }

    private static AuthenticationClient createClient() {
        String serviceURL = System.getenv(URL_ENV_VAR_NAME);
        if (serviceURL == null) {
            System.out.println(URL_ENV_VAR_NAME + " is a required environment variable");
            System.exit(1);
        }
        try {
            return AuthenticationClient.buildDefaultClient(new URL(serviceURL));
        } catch (MalformedURLException e) {
            System.out.println("Invalid service url: " + serviceURL);
            System.exit(1);
            return null;
        }
    }

    private static void printHelp() {
        System.out.println("usage: ");
        System.out.println("       --create userName password");
        System.out.println("       --login userName password");
        System.out.println("       --check token");
    }
}
