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
package com.alexkudlick.authentication.application.config;

import com.alexkudlick.authentication.application.tokens.AuthenticationTokenManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilder;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

public class AuthenticationConfiguration extends Configuration {

    @JsonProperty("cacheSpec")
    private String cacheSpec;

    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    public ServiceRegistry createServiceRegistry(SessionFactory sessionFactory) {
        return new ServiceRegistry(
            CacheBuilder.from(cacheSpec).build(),
            createPasswordEncoder(),
            sessionFactory
        );
    }

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    private PasswordEncoder createPasswordEncoder() {
        return new SCryptPasswordEncoder();
    }
}
