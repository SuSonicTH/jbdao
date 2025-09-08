package net.weichware.myapp;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User extends AbstractUser<User> {

    public User(long id, String name) {
        super(id, name);
    }

    protected User(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }
}
