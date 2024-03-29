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
package com.alexkudlick.authentication.application.dao;

import com.alexkudlick.authentication.application.entities.UserEntity;
import io.dropwizard.testing.junit.DAOTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UserDAOTest {

    @Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
        .addEntityClass(UserEntity.class)
        .build();

    private PasswordEncoder passwordEncoder;
    private UserDAO dao;

    @Before
    public void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        dao = new UserDAO(database.getSessionFactory(), passwordEncoder);
    }

    @Test
    public void testCreateUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("ENCRYPTED");

        database.inTransaction(() -> dao.createUser("user123", "password123"));

        UserEntity entity = database.getSessionFactory().getCurrentSession().get(UserEntity.class, 1L);

        assertEquals("user123", entity.getUserName());
        assertEquals("ENCRYPTED", entity.getEncryptedPassword());

        verify(passwordEncoder).encode("password123");
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    public void testCheckLoginWithValidUserNameAndPassword() {
        saveUser("userFoo", "encryptedPassword1");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertTrue(dao.isValidLogin("userFoo", "rawPassword"));

        verify(passwordEncoder).matches("rawPassword", "encryptedPassword1");
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    public void testCheckLoginWithIncorrectPassword() {
        saveUser("userASDF", "encryptedPassword2");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertFalse(dao.isValidLogin("userASDF", "rawPassword"));

        verify(passwordEncoder).matches("rawPassword", "encryptedPassword2");
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    public void testCheckLoginWithNonExistentUserName() {
        assertFalse(dao.isValidLogin("userFoo", "rawPassword"));
        verifyNoMoreInteractions(passwordEncoder);
    }

    private void saveUser(String userName, String encryptedPassword) {
        database.inTransaction(() -> UserEntity.withUnsavedInstance(
            userName,
            encryptedPassword,
            database.getSessionFactory().getCurrentSession()::persist
        ));
    }
}