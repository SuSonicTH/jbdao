package net.weichware.myapp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class User extends AbstractUser<User> {

    public User() {
        super();
    }

    public User(long id, String name) {
        super(id, name);
    }

    public User(long id, String name, LocalDateTime lastActiveTime) {
        super(id, name, lastActiveTime);
    }

    protected User(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }
}
