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
import com.google.common.base.Preconditions;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Objects;

public class UserDAO extends AbstractDAO<UserEntity> {

    private final PasswordEncoder passwordEncoder;

    public UserDAO(SessionFactory sessionFactory, PasswordEncoder passwordEncoder) {
        super(sessionFactory);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    public void createUser(String userName, String password) {
        Preconditions.checkArgument(userName != null);
        Preconditions.checkArgument(password != null);
        UserEntity.withUnsavedInstance(
            userName,
            passwordEncoder.encode(password),
            this::persist
        );
    }

    public boolean isValidLogin(String userName, String password) {
        CriteriaBuilder criteriaBuilder = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        try {
            UserEntity entity = currentSession()
                .createQuery(query.where(criteriaBuilder.equal(root.get("userName"), userName)))
                .getSingleResult();
            return passwordEncoder.matches(password, entity.getEncryptedPassword());
        } catch (NoResultException e) {
            return false;
        }
    }
}
