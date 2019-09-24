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
package com.alexkudlick.authentication.application;

import com.alexkudlick.authentication.application.config.AuthenticationConfiguration;
import com.alexkudlick.authentication.application.config.ServiceRegistry;
import com.alexkudlick.authentication.application.entities.UserEntity;
import com.alexkudlick.authentication.application.web.AuthenticationTokenResource;
import com.alexkudlick.authentication.application.web.ConstraintViolationExceptionMapper;
import com.alexkudlick.authentication.application.web.UserResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;


public class AuthenticationApplication extends Application<AuthenticationConfiguration> {

    private static final HibernateBundle<AuthenticationConfiguration> HIBERNATE_BUNDLE = new HibernateBundle<>(
        UserEntity.class
    ) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(AuthenticationConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    private static final MigrationsBundle<AuthenticationConfiguration> MIGRATIONS_BUNDLE = new MigrationsBundle<>() {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(AuthenticationConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }

        @Override
        public String getMigrationsFileName() {
            return "migrations.yml";
        }
    };

    public static void main(String[] args) throws Exception {
        new AuthenticationApplication().run(args);
    }

    @Override
    public void run(AuthenticationConfiguration configuration, Environment environment) throws Exception {
        ServiceRegistry serviceRegistry = configuration.createServiceRegistry(HIBERNATE_BUNDLE.getSessionFactory());
        environment.jersey().register(
            new AuthenticationTokenResource(serviceRegistry.getTokenManager())
        );
        environment.jersey().register(new UserResource(serviceRegistry.getUserDAO()));
        environment.jersey().register(new ConstraintViolationExceptionMapper());
        environment.jersey().register(new JsonProcessingExceptionMapper(true));
    }

    @Override
    public void initialize(Bootstrap<AuthenticationConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
            new SubstitutingSourceProvider(
                new ResourceConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor()
            )
        );
        bootstrap.addBundle(HIBERNATE_BUNDLE);
        bootstrap.addBundle(MIGRATIONS_BUNDLE);
    }
}
