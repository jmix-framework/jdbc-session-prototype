package org.example.jdbcsession.security;

import io.jmix.securitydata.user.AbstractDatabaseUserRepository;
import org.example.jdbcsession.entity.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component("testproj_UserRepository")
public class DatabaseUserRepository extends AbstractDatabaseUserRepository<User> {

    @Override
    protected Class<User> getUserClass() {
        return User.class;
    }
}