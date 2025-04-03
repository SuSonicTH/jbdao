package net.weichware.jbdao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {
    private static final Path TEST_PATH = Paths.get("./target/test");
    private static final String CUSTOMER_JSON = "{\"id\":1,\"firstName\":\"Michael\",\"lastName\":\"Wolf\",\"birthDate\":\"1980-03-20\"}";
    private final Customer customer = new Customer(1, "Michael", "Wolf", LocalDate.of(1980, 3, 20));
    private final Customer customer2 = new Customer(2, "Michaela", "Gruber", LocalDate.of(1985, 5, 23));

    @BeforeAll
    static void beforeAll() throws IOException {
        if (!Files.exists(TEST_PATH)) {
            Files.createDirectories(TEST_PATH);
        }
    }

    @Test
    void firstNameNullThrows() {
        assertEquals("firstName may not be null",
                assertThrows(NullPointerException.class, () -> new Customer(1, null, "Wolf", LocalDate.of(1980, 3, 20))).getMessage()
        );
    }

    @Test
    void emptyNameThrows() {
        assertEquals("firstName may not be empty",
                assertThrows(IllegalArgumentException.class, () -> new Customer(1, "", "Wolf", LocalDate.of(1980, 3, 20))).getMessage()
        );
    }

    @Test
    void lastNameNullThrows() {
        assertEquals("lastName may not be null",
                assertThrows(NullPointerException.class, () -> new Customer(1, "Michael", null, LocalDate.of(1980, 3, 20))).getMessage()
        );
    }

    @Test
    void lastNameEmptyThrows() {
        assertEquals("lastName may not be empty",
                assertThrows(IllegalArgumentException.class, () -> new Customer(1, "Michael", "", LocalDate.of(1980, 3, 20))).getMessage()
        );
    }

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

    @Test
    void equalsTest() {
        assertTrue(customer.equals(customer));

        Customer equalCustomer = new Customer(1, "Michael", "Wolf", LocalDate.of(1980, 3, 20));
        assertTrue(customer.equals(equalCustomer));

        assertFalse(customer.equals(customer2));

        assertFalse(customer.equals(new Object()));

        assertFalse(customer.equals(customer.withId(2)));
        assertFalse(customer.equals(customer.withFirstName("Test")));
        assertFalse(customer.equals(customer.withLastName("Test")));
        assertFalse(customer.equals(customer.withBirthDate(LocalDate.of(1981, 4, 21))));
    }

    @Test
    void databaseGetExisting(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            testDatabase.execute("Insert into CUSTOMER (ID,FIRST_NAME,LAST_NAME, BIRTH_DATE) values (1, 'Michael', 'Wolf', date '1980-03-20')");
            Optional<Customer> optionalCustomer = Customer.get(testDatabase.getConnection(), 1);
            assertTrue(optionalCustomer.isPresent());
            assertEquals(customer, optionalCustomer.get());
        }
    }

    @Test
    void databaseGetNonExisting(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            Optional<Customer> optionalCustomer = Customer.get(testDatabase.getConnection(), 1);
            assertFalse(optionalCustomer.isPresent());
        }
    }

    @Test
    void databaseGetAfterInsertReturnsEqualObject(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            customer.insert(testDatabase.getConnection());

            Optional<Customer> optionalCustomer = Customer.get(testDatabase.getConnection(), 1);
            assertTrue(optionalCustomer.isPresent());
            assertEquals(customer, optionalCustomer.get());
        }
    }

    @Test
    void insertReturnsSameObject(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            assertSame(customer, customer.insert(testDatabase.getConnection()));
        }
    }

    @Test
    void databaseGetAfterUpdateReturnsEqualObject(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            customer.insert(testDatabase.getConnection());

            Customer tester = customer.withLastName("Tester");
            tester.update(testDatabase.getConnection());

            Optional<Customer> optionalCustomer = Customer.get(testDatabase.getConnection(), 1);
            assertTrue(optionalCustomer.isPresent());
            assertEquals(tester, optionalCustomer.get());
        }
    }

    @Test
    void updateReturnsSameObject(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            customer.insert(testDatabase.getConnection());
            Customer tester = customer.withLastName("Tester");

            assertSame(tester, tester.update(testDatabase.getConnection()));
        }
    }

    @Test
    void deleteRemovesRowFromDatabase(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            customer.insert(testDatabase.getConnection());
            assertTrue(Customer.get(testDatabase.getConnection(), customer.getId()).isPresent());

            customer.delete(testDatabase.getConnection());
            assertFalse(Customer.get(testDatabase.getConnection(), customer.getId()).isPresent());
        }
    }

    @Test
    void getListReturnsAllRowsFromDatabase(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            customer.insert(testDatabase.getConnection());
            customer2.insert(testDatabase.getConnection());

            List<Customer> actual = Customer.getList(testDatabase.getConnection());

            List<Customer> expected = new ArrayList<>();
            expected.add(customer);
            expected.add(customer2);

            assertEquals(expected, actual);
        }
    }

    @Test
    void getListWithArgumentsReturnsCorrectItems(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            customer.insert(testDatabase.getConnection());
            customer2.insert(testDatabase.getConnection());

            List<Customer> actual = Customer.getList(testDatabase.getConnection(), "select * From CUSTOMER where FIRST_NAME = ?", "Michael");

            assertEquals(customer, actual.get(0));
            assertEquals(1, actual.size());
        }
    }

    @Test
    void streamReturnsAllRowsFromDatabase(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            customer.insert(testDatabase.getConnection());
            customer2.insert(testDatabase.getConnection());

            List<Customer> actual = Customer.stream(testDatabase.getConnection()).collect(Collectors.toList());

            List<Customer> expected = new ArrayList<>();
            expected.add(customer);
            expected.add(customer2);

            assertEquals(expected, actual);
        }
    }

    @Test
    void streamWithArgumentsReturnsCorrectItems(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            customer.insert(testDatabase.getConnection());
            customer2.insert(testDatabase.getConnection());

            List<Customer> actual = Customer.stream(testDatabase.getConnection(), "select * From CUSTOMER where FIRST_NAME = ?", "Michael").collect(Collectors.toList());

            assertEquals(customer, actual.get(0));
            assertEquals(1, actual.size());
        }
    }

    @Test
    void fromJson() {
        assertEquals(customer, Customer.fromJson(CUSTOMER_JSON));
    }

    @Test
    void fromJsonReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CUSTOMER_JSON.getBytes());
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream))) {
            assertEquals(customer, Customer.fromJson(bufferedReader));
        }
    }

    @Test
    void fromJsonInputStream() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CUSTOMER_JSON.getBytes());
        assertEquals(customer, Customer.fromJson(byteArrayInputStream));
    }

    @Test
    void fromJsonFile() throws IOException {
        Path file = TEST_PATH.resolve("customer.json");
        Files.write(file, CUSTOMER_JSON.getBytes(StandardCharsets.UTF_8));

        assertEquals(customer, Customer.fromJson(file));
    }

    @Test
    void toJson() {
        assertEquals(CUSTOMER_JSON, customer.toJson());
    }

    @Test
    void toJsonWriter() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream))) {
            customer.toJson(bufferedWriter);
        }
        assertEquals(CUSTOMER_JSON, byteArrayOutputStream.toString());
    }

    @Test
    void toJsonOutputStream() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        customer.toJson(byteArrayOutputStream);
        assertEquals(CUSTOMER_JSON, byteArrayOutputStream.toString());
    }

    @Test
    void toJsonFile() throws IOException {
        Path file = TEST_PATH.resolve("newCustomer.json");
        customer.toJson(file);
        assertEquals(CUSTOMER_JSON, new String(Files.readAllBytes(file)));
    }


    private TestDatabase setupTestDatabase(TestInfo testInfo) throws SQLException {
        TestDatabase testDatabase = new TestDatabase(testInfo);
        testDatabase.execute("create table CUSTOMER (" +
                "id number not null," +
                "FIRST_NAME varchar2 not null," +
                "LAST_NAME varchar2 not null," +
                "BIRTH_DATE date" +
                ")");
        return testDatabase;
    }
}
