package net.weichware.jbdao;

import net.weichware.jbdao.Customer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CustomerTest {
    private final Customer customer = new Customer(1, "Michael", "Wolf", LocalDate.of(1980, 3, 20));
    private final Customer customer2 = new Customer(2, "Michaela", "Gruber", LocalDate.of(1985, 5, 23));

    @Test
    void toStringTest() {
        assertEquals("Customer{id=1, firstName='Michael', lastName='Wolf', birthDate=1980-03-20}", customer.toString());
    }

    @Test
    void getterTest() {
        assertEquals(1, customer.getId());
        assertEquals("Michael", customer.getFirstName());
        assertEquals("Wolf", customer.getLastName());
        assertEquals(LocalDate.of(1980, 3, 20), customer.getBirthDate());
    }

    @Test
    void withTest() {
        Customer withCustomer = customer
                .withId(2)
                .withFirstName("Michaela")
                .withLastName("Gruber")
                .withBirthDate(LocalDate.of(1985, 5, 23));

        assertEquals(2, withCustomer.getId());
        assertEquals("Michaela", withCustomer.getFirstName());
        assertEquals("Gruber", withCustomer.getLastName());
        assertEquals(LocalDate.of(1985, 5, 23), withCustomer.getBirthDate());
    }

    @Test
    void hashCodeTest() {
        assertEquals(customer.hashCode(), customer.hashCode());
        assertEquals(customer.hashCode(), new Customer(1, "Michael", "Wolf", LocalDate.of(1980, 3, 20)).hashCode());
        assertNotEquals(customer, customer2);

        assertEquals(customer2.hashCode(), new Customer(2, "Michaela", "Gruber", LocalDate.of(1985, 5, 23)).hashCode());
    }
}
