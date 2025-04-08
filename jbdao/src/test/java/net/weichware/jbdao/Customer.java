package net.weichware.jbdao;

import net.weichware.jbdao.GsonUtil;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Customer {
    private final long id;
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
    private final String address;
    private final String country;
    private final Integer postalCode;

    public Customer() {
        id = 0;
        firstName = null;
        lastName = null;
        birthDate = null;
        address = "Unknown";
        country = null;
        postalCode = 9999;
    }

    public Customer(long id, String firstName, String lastName) {
        Objects.requireNonNull(firstName, "firstName may not be null");
        Objects.requireNonNull(lastName, "lastName may not be null");

        if (firstName.isEmpty()) throw new IllegalArgumentException("firstName may not be empty");
        if (lastName.isEmpty()) throw new IllegalArgumentException("lastName may not be empty");

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        birthDate = null;
        address = "Unknown";
        country = null;
        postalCode = 9999;
    }

    public Customer(long id, String firstName, String lastName, LocalDate birthDate, String address, String country, Integer postalCode) {
        Objects.requireNonNull(firstName, "firstName may not be null");
        Objects.requireNonNull(lastName, "lastName may not be null");

        if (firstName.isEmpty()) throw new IllegalArgumentException("firstName may not be empty");
        if (lastName.isEmpty()) throw new IllegalArgumentException("lastName may not be empty");

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.country = country;
        this.postalCode = postalCode;
    }

    private Customer(ResultSet resultSet) throws SQLException {
        id = resultSet.getObject("ID", Long.class);
        firstName = resultSet.getObject("FIRST_NAME", String.class);
        lastName = resultSet.getObject("LAST_NAME", String.class);
        birthDate = resultSet.getObject("BIRTH_DATE", LocalDate.class);
        address = resultSet.getObject("ADDRESS", String.class);
        country = resultSet.getObject("COUNTRY", String.class);
        postalCode = resultSet.getObject("POSTAL_CODE", Integer.class);
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public Customer withId(long id) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode);
    }

    public Customer withFirstName(String firstName) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode);
    }

    public Customer withLastName(String lastName) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode);
    }

    public Customer withBirthDate(LocalDate birthDate) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode);
    }

    public Customer withAddress(String address) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode);
    }

    public Customer withCountry(String country) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode);
    }

    public Customer withPostalCode(Integer postalCode) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode);
    }

    public Customer insert(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into CUSTOMER (ID, FIRST_NAME, LAST_NAME, BIRTH_DATE, ADDRESS, COUNTRY, POSTAL_CODE) values(?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, firstName);
            preparedStatement.setObject(3, lastName);
            preparedStatement.setObject(4, birthDate);
            preparedStatement.setObject(5, address);
            preparedStatement.setObject(6, country);
            preparedStatement.setObject(7, postalCode);
            preparedStatement.execute();
        }
        return this;
    }

    public Customer update(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("update CUSTOMER set FIRST_NAME = ?, LAST_NAME = ?, BIRTH_DATE = ?, ADDRESS = ?, COUNTRY = ?, POSTAL_CODE = ? where ID = ?")) {
            preparedStatement.setObject(1, firstName);
            preparedStatement.setObject(2, lastName);
            preparedStatement.setObject(3, birthDate);
            preparedStatement.setObject(4, address);
            preparedStatement.setObject(5, country);
            preparedStatement.setObject(6, postalCode);
            preparedStatement.setObject(7, id);
            if (preparedStatement.executeUpdate() != 1) {
                throw new SQLException("CUSTOMER table not updated for primary key ID = '" + id + "'");
            }
        }
        return this;
    }

    public void delete(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from CUSTOMER where ID = ?")) {
            preparedStatement.setObject(1, id);
            preparedStatement.execute();
        }
    }

    public boolean isInDatabase(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID from CUSTOMER where ID = ?")) {
            preparedStatement.setObject(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Customer persist(Connection connection) throws SQLException {
        if (isInDatabase(connection)) {
            return update(connection);
        }
        return insert(connection);
    }

    public static Optional<Customer> get(Connection connection, long id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID, FIRST_NAME, LAST_NAME, BIRTH_DATE, ADDRESS, COUNTRY, POSTAL_CODE from CUSTOMER where ID = ?")) {
            preparedStatement.setObject(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Customer(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public static boolean exists(Connection connection, long id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID from CUSTOMER where ID = ?")) {
            preparedStatement.setObject(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Customer> getList(Connection connection) throws SQLException {
        return getList(connection, "select ID, FIRST_NAME, LAST_NAME, BIRTH_DATE, ADDRESS, COUNTRY, POSTAL_CODE from CUSTOMER");
    }

    public static List<Customer> getList(Connection connection, String sql, Object... args) throws SQLException {
        final List<Customer> list = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object arg : args) {
                preparedStatement.setObject(i++, arg);
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.setFetchSize(1000);
                while (resultSet.next()) {
                    list.add(new Customer(resultSet));
                }
            }
        }
        return list;
    }

    public static Stream<Customer> stream(Connection connection) throws SQLException {
        return stream(connection, "select ID, FIRST_NAME, LAST_NAME, BIRTH_DATE, ADDRESS, COUNTRY, POSTAL_CODE from CUSTOMER ");
    }

    public static Stream<Customer> stream(Connection connection, String sql, Object... args) throws SQLException {
        return StreamSupport.stream(new ResultSetSpliterator(connection, sql, args), false);
    }

    public static Customer fromJson(String json) {
        return GsonUtil.gson.fromJson(json, Customer.class);
    }

    public static Customer fromJson(Reader jsonReader) {
        return GsonUtil.gson.fromJson(jsonReader, Customer.class);
    }

    public static Customer fromJson(InputStream jsonStream) throws IOException {
        try (Reader jsonReader = new InputStreamReader(jsonStream)) {
            return GsonUtil.gson.fromJson(jsonReader, Customer.class);
        }
    }

    public static Customer fromJson(Path jsonFile) throws IOException {
        try (Reader jsonReader = new InputStreamReader(Files.newInputStream(jsonFile))) {
            return GsonUtil.gson.fromJson(jsonReader, Customer.class);
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

    public static Builder builder(long id, String firstName, String lastName) {
        return new Builder(id, firstName, lastName);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", postalCode=" + postalCode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(firstName, customer.firstName) && Objects.equals(lastName, customer.lastName) && Objects.equals(birthDate, customer.birthDate) && Objects.equals(address, customer.address) && Objects.equals(country, customer.country) && Objects.equals(postalCode, customer.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, birthDate, address, country, postalCode);
    }

    private static class ResultSetSpliterator extends AbstractResultSetSpliterator<Customer> implements AutoCloseable {

        public ResultSetSpliterator(Connection connection, String sql, Object... args) {
            super(connection, sql, args);
        }

        @Override
        protected Customer create(ResultSet resultSet) throws SQLException {
            return new Customer(resultSet);
        }
    }

    public static class Builder {
        private final long id;
        private final String firstName;
        private final String lastName;
        private LocalDate birthDate;
        private String address = "Unknown";
        private String country;
        private Integer postalCode = 9999;

        public Builder(long id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Builder setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setPostalCode(Integer postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Customer build() {
            return new Customer(id, firstName, lastName, birthDate, address, country, postalCode);
        }
    }
}
