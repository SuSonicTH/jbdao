package net.weichware.myapp;

import net.weichware.jbdao.AbstractResultSetSpliterator;
import net.weichware.jbdao.ValidationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractUser<T> {
    private final long id;
    private final String name;
    private final LocalDateTime lastActiveTime;

    protected AbstractUser(long id, String name) {
        this.id = id;
        this.name = name;
        lastActiveTime = null;
        validate();
    }

    protected AbstractUser(ResultSet resultSet) throws SQLException {
        id = resultSet.getObject("ID", Long.class);
        name = resultSet.getObject("NAME", String.class);
        lastActiveTime = resultSet.getObject("LAST_ACTIVE_TIME", LocalDateTime.class);
        validate();
    }

    public static Optional<User> get(Connection connection, long id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID, NAME, LAST_ACTIVE_TIME from USER where ID = ?")) {
            preparedStatement.setObject(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new User(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public static boolean exists(Connection connection, long id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID from USER where ID = ?")) {
            preparedStatement.setObject(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<User> getList(Connection connection) throws SQLException {
        return getList(connection, "select ID, NAME, LAST_ACTIVE_TIME from USER");
    }

    public static List<User> getList(Connection connection, String sql, Object... args) throws SQLException {
        final List<User> list = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object arg : args) {
                preparedStatement.setObject(i++, arg);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.setFetchSize(1000);
                while (resultSet.next()) {
                    list.add(new User(resultSet));
                }
            }
        }
        return list;
    }

    public static Stream<User> stream(Connection connection) throws SQLException {
        return stream(connection, "select ID, NAME, LAST_ACTIVE_TIME from USER ");
    }

    public static Stream<User> stream(Connection connection, String sql, Object... args) throws SQLException {
        return StreamSupport.stream(new ResultSetSpliterator(connection, sql, args), false);
    }

    public T validate() {
        if (name == null) throw new ValidationException("name may not be null");
        if (name.isEmpty()) throw new ValidationException("name may not be empty");
        return (T) this;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getLastActiveTime() {
        return lastActiveTime;
    }

    public T insert(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into USER (ID, NAME, LAST_ACTIVE_TIME) values(?, ?, ?)")) {
            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, name);
            preparedStatement.setObject(3, lastActiveTime);
            preparedStatement.execute();
        }
        return (T) this;
    }

    public T update(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("update USER set NAME = ?, LAST_ACTIVE_TIME = ? where ID = ?")) {
            preparedStatement.setObject(1, name);
            preparedStatement.setObject(2, lastActiveTime);
            preparedStatement.setObject(3, id);
            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("USER table not updated for primary key ID = '" + id + "'");
            }
        }
        return (T) this;
    }

    public void delete(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from USER where ID = ?")) {
            preparedStatement.setObject(1, id);
            preparedStatement.execute();
        }
    }

    public boolean isInDatabase(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID from USER where ID = ?")) {
            preparedStatement.setObject(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public T persist(Connection connection) throws SQLException {
        if (isInDatabase(connection)) {
            return update(connection);
        }
        return insert(connection);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastActiveTime=" + lastActiveTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.getId()) && Objects.equals(name, user.getName()) && Objects.equals(lastActiveTime, user.getLastActiveTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastActiveTime);
    }

    private static class ResultSetSpliterator extends AbstractResultSetSpliterator<User> {

        public ResultSetSpliterator(Connection connection, String sql, Object... args) {
            super(connection, sql, args);
        }

        @Override
        protected User create(ResultSet resultSet) throws SQLException {
            return new User(resultSet);
        }
    }
}
