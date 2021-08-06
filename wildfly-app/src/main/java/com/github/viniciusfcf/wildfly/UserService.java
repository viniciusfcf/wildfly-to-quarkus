package com.github.viniciusfcf.wildfly;

import java.util.HashMap;
import java.util.Map;

//@Stateless
public class UserService {

    private static final Map<String, User> USER_DB = new HashMap<String, User>() {{
        put("customer", new User("customer", "customerpw", Roles.CUSTOMER));
        put("admin", new User("admin", "adminpw", Roles.ADMIN));
    }};

    public User authenticate(final String username, final String password) throws Exception {
        User user = USER_DB.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        throw new Exception("Failed logging in org.jboss.user with name '" + username + "': unknown username or wrong password");
    }
}
