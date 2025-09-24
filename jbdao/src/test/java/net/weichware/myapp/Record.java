package net.weichware.myapp;

import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Record {
    public static final Gson GSON = GsonUtil.GSON_BUILDER
            .registerTypeAdapter(Product.class, new Product.GsonAdapter().nullSafe())
            .create();
    private final int id;
    private final Product product;

    public Record() {
        id = 0;
        product = null;
    }

    public Record(int id, Product product) {
        this.id = id;
        this.product = product;
        validate();
    }

    protected Record(ResultSet resultSet) throws SQLException {
        id = resultSet.getObject("ID", Integer.class);
        product = Product.fromDatabase(resultSet.getString("PRODUCT"));
        validate();
    }

    public Record validate() {
        if (product == null) throw new ValidationException("product may not be null");
        return this;
    }

    public int id() {
        return id;
    }

    public Product product() {
        return product;
    }

    public Record insert(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into RECORD (ID, PRODUCT) values(?, ?)")) {
            preparedStatement.setObject(1, id);
            preparedStatement.setString(2, product.toDatabase());
            preparedStatement.execute();
        }
        return this;
    }

    public Record update(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("update RECORD set PRODUCT = ? where ID = ?")) {
            preparedStatement.setString(1, product.toDatabase());
            preparedStatement.setObject(2, id);
            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("RECORD table not updated for primary key ID = '" + id + "'");
            }
        }
        return this;
    }

    public void delete(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from RECORD where ID = ?")) {
            preparedStatement.setObject(1, id);
            preparedStatement.execute();
        }
    }

    public boolean isInDatabase(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID from RECORD where ID = ?")) {
            preparedStatement.setObject(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Record persist(Connection connection) throws SQLException {
        if (isInDatabase(connection)) {
            return update(connection);
        }
        return insert(connection);
    }

    public static Optional<Record> get(Connection connection, long id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID, PRODUCT from RECORD where ID = ?")) {
            preparedStatement.setObject(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Record(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public static boolean exists(Connection connection, long id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID from RECORD where ID = ?")) {
            preparedStatement.setObject(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Record> getList(Connection connection) throws SQLException {
        return getList(connection, "select ID, PRODUCT from RECORD");
    }

    public static List<Record> getList(Connection connection, String sql, Object... args) throws SQLException {
        final List<Record> list = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object arg : args) {
                preparedStatement.setObject(i++, arg);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.setFetchSize(1000);
                while (resultSet.next()) {
                    list.add(new Record(resultSet));
                }
            }
        }
        return list;
    }

    public static Stream<Record> stream(Connection connection) throws SQLException {
        return stream(connection, "select ID, PRODUCT from RECORD ");
    }

    public static Stream<Record> stream(Connection connection, String sql, Object... args) throws SQLException {
        return StreamSupport.stream(new ResultSetSpliterator(connection, sql, args), false);
    }

    public static Record fromJson(String json) {
        return GSON.fromJson(json, Record.class);
    }

    public static Record fromJson(Reader jsonReader) {
        return GSON.fromJson(jsonReader, Record.class);
    }

    public static Record fromJson(InputStream jsonStream) throws IOException {
        try (Reader jsonReader = new InputStreamReader(jsonStream)) {
            return GSON.fromJson(jsonReader, Record.class);
        }
    }

    public static Record fromJson(Path jsonFile) throws IOException {
        try (Reader jsonReader = new InputStreamReader(Files.newInputStream(jsonFile))) {
            return GSON.fromJson(jsonReader, Record.class);
        }
    }

    public String toJson() {
        return GSON.toJson(this);
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

    public static Stream<Record> streamCsv(Path file) {
        try {
            return StreamSupport.stream(new CsvReader(file, true), false);
        } catch (IOException e) {
            throw new CsvReaderException("Could not read file '" + file + "'", e);
        }
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", product=" + product +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record)) return false;
        Record record = (Record) o;
        return Objects.equals(id, record.id) && Objects.equals(product, record.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product);
    }

    private static class ResultSetSpliterator extends AbstractResultSetSpliterator<Record> {

        public ResultSetSpliterator(Connection connection, String sql, Object... args) {
            super(connection, sql, args);
        }

        @Override
        protected Record create(ResultSet resultSet) throws SQLException {
            return new Record(resultSet);
        }
    }

    private static class CsvReader extends AbstractCsvReader<Record> {
        private int id;
        private int product;

        public CsvReader(Path file, boolean hasHeader) throws IOException {
            super(Files.newBufferedReader(file), hasHeader);
        }

        @Override
        protected void validateHeader(Map<String, Integer> header) {
            id = header.get("ID");
            product = header.get("PRODUCT");
        }

        @Override
        protected Record create(List<String> fields) {
            return new Record(Integer.parseInt(fields.get(id)), Product.fromCsv(fields.get(product)));
        }
    }
}
