package net.weichware.myapp;

import net.weichware.jbdao.AbstractCsvReader;
import net.weichware.jbdao.AbstractResultSetSpliterator;
import net.weichware.jbdao.CsvReaderException;
import net.weichware.jbdao.GsonUtil;
import net.weichware.jbdao.ValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractUser<T> {
    private final long id;
    private final String name;
    private final LocalDateTime lastActiveTime;

    protected AbstractUser() {
        id = 0;
        name = null;
        lastActiveTime = null;
    }

    protected AbstractUser(long id, String name) {
        this.id = id;
        this.name = name;
        lastActiveTime = null;
        validate();
    }

    protected AbstractUser(long id, String name, LocalDateTime lastActiveTime) {
        this.id = id;
        this.name = name;
        this.lastActiveTime = lastActiveTime;
        validate();
    }

    protected AbstractUser(ResultSet resultSet) throws SQLException {
        id = resultSet.getObject("ID", Long.class);
        name = resultSet.getObject("NAME", String.class);
        lastActiveTime = resultSet.getObject("LAST_ACTIVE_TIME", LocalDateTime.class);
        validate();
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

    public User withId(long id) {
        return new User(id, name, lastActiveTime);
    }

    public User withName(String name) {
        return new User(id, name, lastActiveTime);
    }

    public User withLastActiveTime(LocalDateTime lastActiveTime) {
        return new User(id, name, lastActiveTime);
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

    public static User fromJson(String json) {
        return GsonUtil.gson.fromJson(json, User.class);
    }

    public static User fromJson(Reader jsonReader) {
        return GsonUtil.gson.fromJson(jsonReader, User.class);
    }

    public static User fromJson(InputStream jsonStream) throws IOException {
        try (Reader jsonReader = new InputStreamReader(jsonStream)) {
            return GsonUtil.gson.fromJson(jsonReader, User.class);
        }
    }

    public static User fromJson(Path jsonFile) throws IOException {
        try (Reader jsonReader = new InputStreamReader(Files.newInputStream(jsonFile))) {
            return GsonUtil.gson.fromJson(jsonReader, User.class);
        }
    }

    public String toJson() {
        return GsonUtil.gson.toJson(this);
    }

    public void toJson(Writer writer) throws IOException {
        writer.write(toJson());
    }

    public void toJson(OutputStream outputStream) throws IOException {
        outputStream.write(toJson().getBytes(StandardCharsets.UTF_8));
    }

    public void toJson(Path jsonFile) throws IOException {
        Files.write(jsonFile, toJson().getBytes(StandardCharsets.UTF_8));
    }

    public static Stream<User> streamCsv(Path file) {
        try {
            return StreamSupport.stream(new CsvReader(file, true), false);
        } catch (IOException e) {
            throw new CsvReaderException("Could not read file '" + file + "'", e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(long id, String name) {
        return new Builder(id, name);
    }

    public Builder builderFrom() {
        return new Builder(id, name, lastActiveTime);
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

    private static class CsvReader extends AbstractCsvReader<User> {
        private int id;
        private int name;

        public CsvReader(Path file, boolean hasHeader) throws IOException {
            super(Files.newBufferedReader(file), hasHeader);
        }

        @Override
        protected void validateHeader(Map<String, Integer> header) {
            id = header.get("ID");
            name = header.get("NAME");
        }

        @Override
        protected User create(List<String> fields) {
            return new User(Long.parseLong(fields.get(id)), fields.get(name));
        }
    }

    public static class Builder {
        private long id;
        private String name;
        private LocalDateTime lastActiveTime;

        public Builder() {
        }

        public Builder(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder(long id, String name, LocalDateTime lastActiveTime) {
            this.id = id;
            this.name = name;
            this.lastActiveTime = lastActiveTime;
        }

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLastActiveTime(LocalDateTime lastActiveTime) {
            this.lastActiveTime = lastActiveTime;
            return this;
        }

        public User build() {
            return new User(id, name, lastActiveTime);
        }
    }
}
