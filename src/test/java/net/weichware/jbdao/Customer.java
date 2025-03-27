package net.weichware.jbdao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

public class Customer {
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;

    public Customer(String firstName, String lastName, LocalDate birthDate) {
        Objects.requireNonNull(firstName, "firstName my not be null");
        Objects.requireNonNull(lastName, "lastName my not be null");

        if (firstName.isEmpty()) throw new IllegalArgumentException("firstName may not be empty");
        if (lastName.isEmpty()) throw new IllegalArgumentException("lastName may not be empty");

        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    private Customer(ResultSet resultSet) throws SQLException {
        firstName = resultSet.getObject("FIRST_NAME", String.class);
        lastName = resultSet.getObject("LAST_NAME", String.class);
        birthDate = resultSet.getObject("BIRTH_DATE", LocalDate.class);
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

    public Customer withFirstName(String firstName) {
        return new Customer(firstName, lastName, birthDate);
    }

    public Customer withLastName(String lastName) {
        return new Customer(firstName, lastName, birthDate);
    }

    public Customer withBirthDate(LocalDate birthDate) {
        return new Customer(firstName, lastName, birthDate);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(firstName, customer.firstName) && Objects.equals(lastName, customer.lastName) && Objects.equals(birthDate, customer.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, birthDate);
    }
}
