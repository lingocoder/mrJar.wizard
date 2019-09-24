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
module com.alexkudlick.authentication.application {
    requires com.alexkudlick.authentication.models;

    requires jackson.annotations;
    requires com.google.common;
    requires javax.ws.rs.api;
    requires java.naming;

    requires java.sql;
    requires java.xml.bind;

    requires hibernate.jpa;
    requires dropwizard.hibernate;
    requires hibernate.core;
    requires spring.security.crypto;
    requires dropwizard.servlets;
    requires dropwizard.db;
    requires dropwizard.migrations;
    requires com.fasterxml.jackson.databind;

    requires validation.api;
    requires hibernate.validator;

    requires dropwizard.core;
    requires dropwizard.configuration;

    opens com.alexkudlick.authentication.application.config to com.fasterxml.jackson.databind;
    opens com.alexkudlick.authentication.application.web to jersey.server;
    opens com.alexkudlick.authentication.application.entities to hibernate.core, javassist;

    requires dropwizard.jersey;
}
