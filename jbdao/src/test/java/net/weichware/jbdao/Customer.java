package net.weichware.jbdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Customer {
    private final long id;
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;

    public Customer(long id, String firstName, String lastName, LocalDate birthDate) {
        Objects.requireNonNull(firstName, "firstName my not be null");
        Objects.requireNonNull(lastName, "lastName my not be null");

        if (firstName.isEmpty()) throw new IllegalArgumentException("firstName may not be empty");
        if (lastName.isEmpty()) throw new IllegalArgumentException("lastName may not be empty");

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    private Customer(ResultSet resultSet) throws SQLException {
        id = resultSet.getObject("ID", long.class);
        firstName = resultSet.getObject("FIRST_NAME", String.class);
        lastName = resultSet.getObject("LAST_NAME", String.class);
        birthDate = resultSet.getObject("BIRTH_DATE", LocalDate.class);
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

    public Customer withId(long id) {
        return new Customer(id, firstName, lastName, birthDate);
    }

    public Customer withFirstName(String firstName) {
        return new Customer(id, firstName, lastName, birthDate);
    }

    public Customer withLastName(String lastName) {
        return new Customer(id, firstName, lastName, birthDate);
    }

    public Customer withBirthDate(LocalDate birthDate) {
        return new Customer(id, firstName, lastName, birthDate);
    }

    public Customer insert(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into CUSTOMER (ID, FIRST_NAME, LAST_NAME, BIRTH_DATE) values(?, ?, ?, ?)")) {
            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, firstName);
            preparedStatement.setObject(3, lastName);
            preparedStatement.setObject(4, birthDate);
            preparedStatement.execute();
        }
        return this;
    }

    public Customer update(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("update CUSTOMER set FIRST_NAME = ?, LAST_NAME = ?, BIRTH_DATE = ? where ID = ?")) {
            preparedStatement.setObject(1, firstName);
            preparedStatement.setObject(2, lastName);
            preparedStatement.setObject(3, birthDate);
            preparedStatement.setObject(4, id);
            preparedStatement.execute();
        }
        return this;
    }

    public void delete(Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from CUSTOMER where ID = ?")) {
            preparedStatement.setObject(1, id);
            preparedStatement.execute();
        }
    }

    public static Optional<Customer> get(Connection connection, long id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("select ID, FIRST_NAME, LAST_NAME, BIRTH_DATE from CUSTOMER where ID = ?")) {
            preparedStatement.setObject(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Customer(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public static List<Customer> getList(Connection connection) throws SQLException {
        return getList(connection, "select ID, FIRST_NAME, LAST_NAME, BIRTH_DATE from CUSTOMER");
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
        return stream(connection, "select ID, FIRST_NAME, LAST_NAME, BIRTH_DATE from CUSTOMER ");
    }

    public static Stream<Customer> stream(Connection connection, String sql, Object... args) throws SQLException {
        return StreamSupport.stream(new ResultSetSpliterator(connection, sql, args), false);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(firstName, customer.firstName) && Objects.equals(lastName, customer.lastName) && Objects.equals(birthDate, customer.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, birthDate);
    }

    private static class ResultSetSpliterator extends Spliterators.AbstractSpliterator<Customer> implements AutoCloseable {
        private final PreparedStatement preparedStatement;
        private final ResultSet resultSet;

        ResultSetSpliterator(Connection connection, String sql, Object... args) throws SQLException {
            super(Long.MAX_VALUE, Spliterator.ORDERED);
            try {
                preparedStatement = connection.prepareStatement(sql);
                int i = 1;
                for (Object arg : args) {
                    preparedStatement.setObject(i++, arg);
                }

                resultSet = preparedStatement.executeQuery();
                resultSet.setFetchSize(1000);
            } catch (SQLException sqlException) {
                try {
                    close();
                } catch (SQLException ex) {
                    //intentionally left blank
                }
                throw new ResultSetSpliteratorException("Could not create ResultSetSpliterator", sqlException);
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super Customer> action) {
            try {
                if (resultSet.next()) {
                    action.accept(new Customer(resultSet));
                    return true;
                } else {
                    close();
                    return false;
                }
            } catch (SQLException e) {
                throw new ResultSetSpliteratorException("Could not advance to next record", e);
            }
        }

        public void close() throws SQLException {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }

        public static class ResultSetSpliteratorException extends RuntimeException {
            public ResultSetSpliteratorException(String message, Throwable cause) {
                super(message, cause);
            }
        }
    }
}
