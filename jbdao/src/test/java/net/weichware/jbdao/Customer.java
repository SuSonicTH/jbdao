package net.weichware.jbdao;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final String phoneNumber;
    private int kids;

    public Customer() {
        id = 0;
        firstName = null;
        lastName = null;
        birthDate = null;
        address = "Unknown";
        country = null;
        postalCode = 9999;
        phoneNumber = null;
        kids = 0;
    }

    public Customer(long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        birthDate = null;
        address = "Unknown";
        country = null;
        postalCode = 9999;
        phoneNumber = null;
        kids = 0;
        validate();
    }

    public Customer(long id, String firstName, String lastName, LocalDate birthDate, String address, String country, Integer postalCode, String phoneNumber, int kids) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.country = country;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.kids = kids;
        validate();
    }

    private Customer(ResultSet resultSet) throws SQLException {
        id = resultSet.getObject("ID", Long.class);
        firstName = resultSet.getObject("FIRST_NAME", String.class);
        lastName = resultSet.getObject("LAST_NAME", String.class);
        birthDate = resultSet.getObject("BIRTH_DATE", LocalDate.class);
        address = resultSet.getObject("ADDRESS", String.class);
        country = resultSet.getObject("COUNTRY", String.class);
        postalCode = resultSet.getObject("POSTAL_CODE", Integer.class);
        phoneNumber = resultSet.getObject("PHONE_NUMBER", String.class);
        kids = resultSet.getObject("KIDS", Integer.class);
        validate();
    }

    public Customer validate() {
        if (firstName == null) throw new ValidationException("firstName may not be null");
        if (firstName.isEmpty()) throw new ValidationException("firstName may not be empty");
        if (lastName == null) throw new ValidationException("lastName may not be null");
        if (lastName.isEmpty()) throw new ValidationException("lastName may not be empty");
        if (address != null && address.length() < 3) throw new ValidationException("address is shorter than min 3");
        if (address != null && address.length() > 50) throw new ValidationException("address is longer than max 50");
        if (phoneNumber != null && !phoneNumber.matches("\\+[1-9][0-9]+")) throw new ValidationException("phoneNumber does not match pattern '\\+[1-9][0-9]+'");
        if (kids < 0) throw new ValidationException("kids is lower then min 0");
        if (kids > 10) throw new ValidationException("kids is higher then max 10");
        return this;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getKids() {
        return kids;
    }

    public Customer setKids(int kids) {
        if (kids < 0) throw new ValidationException("kids is lower then min 0");
        if (kids > 10) throw new ValidationException("kids is higher then max 10");
        this.kids = kids;
        return this;
    }

    public Customer withId(long id) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
    }

    public Customer withFirstName(String firstName) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
    }

    public Customer withLastName(String lastName) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
    }

    public Customer withBirthDate(LocalDate birthDate) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
    }

    public Customer withAddress(String address) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
    }

    public Customer withCountry(String country) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
    }

    public Customer withPostalCode(Integer postalCode) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
    }

    public Customer withPhoneNumber(String phoneNumber) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
    }

    public Customer withKids(int kids) {
        return new Customer(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
    }

    public Customer insert(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into CUSTOMER (ID, FIRST_NAME, LAST_NAME, BIRTH_DATE, ADDRESS, COUNTRY, POSTAL_CODE, PHONE_NUMBER, KIDS) values(?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, firstName);
            preparedStatement.setObject(3, lastName);
            preparedStatement.setObject(4, birthDate);
            preparedStatement.setObject(5, address);
            preparedStatement.setObject(6, country);
            preparedStatement.setObject(7, postalCode);
            preparedStatement.setObject(8, phoneNumber);
            preparedStatement.setObject(9, kids);
            preparedStatement.execute();
        }
        return this;
    }

    public Customer update(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("update CUSTOMER set FIRST_NAME = ?, LAST_NAME = ?, BIRTH_DATE = ?, ADDRESS = ?, COUNTRY = ?, POSTAL_CODE = ?, PHONE_NUMBER = ?, KIDS = ? where ID = ?")) {
            preparedStatement.setObject(1, firstName);
            preparedStatement.setObject(2, lastName);
            preparedStatement.setObject(3, birthDate);
            preparedStatement.setObject(4, address);
            preparedStatement.setObject(5, country);
            preparedStatement.setObject(6, postalCode);
            preparedStatement.setObject(7, phoneNumber);
            preparedStatement.setObject(8, kids);
            preparedStatement.setObject(9, id);
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
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID, FIRST_NAME, LAST_NAME, BIRTH_DATE, ADDRESS, COUNTRY, POSTAL_CODE, PHONE_NUMBER, KIDS from CUSTOMER where ID = ?")) {
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
        return getList(connection, "select ID, FIRST_NAME, LAST_NAME, BIRTH_DATE, ADDRESS, COUNTRY, POSTAL_CODE, PHONE_NUMBER, KIDS from CUSTOMER");
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
        return stream(connection, "select ID, FIRST_NAME, LAST_NAME, BIRTH_DATE, ADDRESS, COUNTRY, POSTAL_CODE, PHONE_NUMBER, KIDS from CUSTOMER ");
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

    public static Stream<Customer> streamCsv(Path file) {
        try {
            return StreamSupport.stream(new CsvReader(file, true), false);
        } catch (IOException e) {
            throw new CsvReaderException("Could not read file '" + file + "'", e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(long id, String firstName, String lastName) {
        return new Builder(id, firstName, lastName);
    }

    public Builder builderFrom() {
        return new Builder(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + getBirthDateMasked(true) +
                ", address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", postalCode=" + postalCode +
                ", phoneNumber='" + getPhoneNumberMasked(true) + '\'' +
                '}';
    }

    public String getBirthDateMasked(boolean nullable) {
        if (birthDate == null) {
            return nullable ? null : "";
        }
        return (birthDate + "").replaceAll("^(.+)..", "$1xx");
    }

    public String getPhoneNumberMasked(boolean nullable) {
        if (phoneNumber == null) {
            return nullable ? null : "";
        }
        return phoneNumber.replaceAll("^(.+)....", "$1xxxx");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(firstName, customer.firstName) && Objects.equals(lastName, customer.lastName) && Objects.equals(birthDate, customer.birthDate) && Objects.equals(address, customer.address) && Objects.equals(country, customer.country) && Objects.equals(postalCode, customer.postalCode) && Objects.equals(phoneNumber, customer.phoneNumber) && Objects.equals(kids, customer.kids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
    }

    private static class ResultSetSpliterator extends AbstractResultSetSpliterator<Customer> {

        public ResultSetSpliterator(Connection connection, String sql, Object... args) {
            super(connection, sql, args);
        }

        @Override
        protected Customer create(ResultSet resultSet) throws SQLException {
            return new Customer(resultSet);
        }
    }

    private static class CsvReader extends AbstractCsvReader<Customer> {
        private int id;
        private int firstName;
        private int lastName;

        public CsvReader(Path file, boolean hasHeader) throws IOException {
            super(Files.newBufferedReader(file), hasHeader);
        }

        @Override
        protected void validateHeader(Map<String, Integer> header) {
            id = header.get("Index");
            firstName = header.get("First Name");
            lastName = header.get("Last Name");
        }

        @Override
        protected Customer create(List<String> fields) {
            return new Customer(Long.parseLong(fields.get(id)), fields.get(firstName), fields.get(lastName));
        }
    }

    public static class Builder {
        private long id;
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private String address = "Unknown";
        private String country;
        private Integer postalCode = 9999;
        private String phoneNumber;
        private int kids = 0;

        public Builder() {
        }

        public Builder(long id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Builder(long id, String firstName, String lastName, LocalDate birthDate, String address, String country, Integer postalCode, String phoneNumber, int kids) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthDate = birthDate;
            this.address = address;
            this.country = country;
            this.postalCode = postalCode;
            this.phoneNumber = phoneNumber;
            this.kids = kids;
        }

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
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

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setKids(int kids) {
            this.kids = kids;
            return this;
        }

        public Customer build() {
            return new Customer(id, firstName, lastName, birthDate, address, country, postalCode, phoneNumber, kids);
        }
    }
}
