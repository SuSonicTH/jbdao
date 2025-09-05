package net.weichware.jbdao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.*;
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
    private static final String CUSTOMER_JSON = "{\"id\":1,\"firstName\":\"Michael\",\"lastName\":\"Wolf\",\"birthDate\":\"1980-03-20\",\"kids\":0}";
    private static final String CSV_FILE_CONTENTS = "Index,Customer Id,First Name,Last Name,Company,City,Country,Phone 1,Phone 2,Email,Subscription Date,Website\n" +
            "1,4962fdbE6Bfee6D,Pam,Sparks,Patel-Deleon,Blakemouth,British Indian Ocean Territory (Chagos Archipelago),267-243-9490x035,480-078-0535x889,nicolas00@faulkner-kramer.com,2020-11-29,https://nelson.com/\n" +
            "2,9b12Ae76fdBc9bE,Gina,Rocha,\"Acosta, Paul and Barber\",East Lynnchester,Costa Rica,027.142.0940,+1-752-593-4777x07171,yfarley@morgan.com,2021-01-03,https://pineda-rogers.biz/\n" +
            "3,39edFd2F60C85BC,Kristie,Greer,Ochoa PLC,West Pamela,Ecuador,+1-049-168-7497x5053,+1-311-216-7855,jennyhayden@petty.org,2021-06-20,https://mckinney.com/\n";
    private static final Path CSV_FILE_PATH = TEST_PATH.resolve("testFile.csv");
    private final Customer customer = new Customer(1, "Michael", "Wolf", LocalDate.of(1980, 3, 20), null, null, null, null, 0);
    private final Customer customerWithDefaults = customer.withAddress("Unknown").withPostalCode(9999);
    private final Customer customer2 = new Customer(2, "Michaela", "Gruber", LocalDate.of(1985, 5, 23), "Somestreet 20", "Austria", 1010, "+43123456", 2);

    @BeforeAll
    static void beforeAll() throws IOException {
        if (!Files.exists(TEST_PATH)) {
            Files.createDirectories(TEST_PATH);
        }
    }

    @Test
    void NonNullArgsConstructorTest() {
        Customer customer = new Customer(1, "Michael", "Wolf");
        assertEquals(1, customer.getId());
        assertEquals("Michael", customer.getFirstName());
        assertEquals("Wolf", customer.getLastName());
        assertNull(customer.getBirthDate());
        assertEquals("Unknown", customer.getAddress());
        assertNull(customer.getCountry());
        assertEquals(9999, customer.getPostalCode());
        assertNull(customer.getPhoneNumber());
        assertEquals(0, customer.getKids());
    }

    @Test
    void noArgsConstructorTest() {
        Customer customer = new Customer();
        assertEquals(0, customer.getId());
        assertNull(customer.getFirstName());
        assertNull(customer.getLastName());
        assertNull(customer.getBirthDate());
        assertEquals("Unknown", customer.getAddress());
        assertNull(customer.getCountry());
        assertEquals(9999, customer.getPostalCode());
        assertNull(customer.getPhoneNumber());
        assertEquals(0, customer.getKids());
    }

    @Test
    void nonMatchingPatternThrows() {
        assertEquals("phoneNumber does not match pattern '\\+[1-9][0-9]+'",
                assertThrows(ValidationException.class, () -> customer.withPhoneNumber("0043123456")).getMessage()
        );
        assertEquals("phoneNumber does not match pattern '\\+[1-9][0-9]+'",
                assertThrows(ValidationException.class, () -> customer.withPhoneNumber("+043123456")).getMessage()
        );
        assertEquals("phoneNumber does not match pattern '\\+[1-9][0-9]+'",
                assertThrows(ValidationException.class, () -> customer.withPhoneNumber("+ABC")).getMessage()
        );
        assertEquals("phoneNumber does not match pattern '\\+[1-9][0-9]+'",
                assertThrows(ValidationException.class, () -> customer.withPhoneNumber("+1")).getMessage()
        );
        assertDoesNotThrow(() -> customer.withPhoneNumber("+4312345"));
    }

    @Test
    void firstNameNullThrows() {
        assertEquals("firstName may not be null",
                assertThrows(ValidationException.class, () -> new Customer(1, null, "Wolf", LocalDate.of(1980, 3, 20), null, null, null, null, 0)).getMessage()
        );
        assertEquals("firstName may not be null",
                assertThrows(ValidationException.class, () -> new Customer(1, null, "Wolf")).getMessage()
        );
    }

    @Test
    void emptyNameThrows() {
        assertEquals("firstName may not be empty",
                assertThrows(ValidationException.class, () -> new Customer(1, "", "Wolf", LocalDate.of(1980, 3, 20), null, null, null, null, 0)).getMessage()
        );
        assertEquals("firstName may not be empty",
                assertThrows(ValidationException.class, () -> new Customer(1, "", "Wolf")).getMessage()
        );
    }

    @Test
    void lastNameNullThrows() {
        assertEquals("lastName may not be null",
                assertThrows(ValidationException.class, () -> new Customer(1, "Michael", null, LocalDate.of(1980, 3, 20), null, null, null, null, 0)).getMessage()
        );
        assertEquals("lastName may not be null",
                assertThrows(ValidationException.class, () -> new Customer(1, "Michael", null)).getMessage()
        );
    }

    @Test
    void lastNameEmptyThrows() {
        assertEquals("lastName may not be empty",
                assertThrows(ValidationException.class, () -> new Customer(1, "Michael", "", LocalDate.of(1980, 3, 20), null, null, null, null, 0)).getMessage()
        );
        assertEquals("lastName may not be empty",
                assertThrows(ValidationException.class, () -> new Customer(1, "Michael", "")).getMessage()
        );
    }

    @Test
    void validateThrowsForNonValidObject() {
        Customer emptyCustomer = new Customer();
        assertEquals("firstName may not be null",
                assertThrows(ValidationException.class, emptyCustomer::validate).getMessage()
        );
    }

    @Test
    void validateReturnsSameObject() {
        assertSame(customer, customer.validate());
    }

    @Test
    void minValidationThrowsForOutOfRange() {
        assertEquals("kids is lower then min 0",
                assertThrows(ValidationException.class, () -> customer.withKids(-1)).getMessage()
        );
    }

    @Test
    void maxValidationThrowsForOutOfRange() {
        assertEquals("kids is higher then max 10",
                assertThrows(ValidationException.class, () -> customer.withKids(11)).getMessage()
        );
    }

    @Test
    void minValidationThrowsForStringLengthOutOfRange() {
        assertEquals("address is shorter than min 3",
                assertThrows(ValidationException.class, () -> customer.withAddress("AS")).getMessage()
        );
    }

    @Test
    void maxValidationThrowsForStringLengthOutOfRange() {
        assertEquals("address is longer than max 50",
                assertThrows(ValidationException.class, () -> customer.withAddress("Some extra long street name that should throw because its to long 1")).getMessage()
        );
    }

    @Test
    void toStringTest() {
        assertEquals("Customer{id=1, firstName='Michael', lastName='Wolf', birthDate=1980-03-xx, address='null', country='null', postalCode=null, phoneNumber='null'}", customer.toString());
        assertEquals("Customer{id=2, firstName='Michaela', lastName='Gruber', birthDate=1985-05-xx, address='Somestreet 20', country='Austria', postalCode=1010, phoneNumber='+4312xxxx'}", customer2.toString());
    }

    @Test
    void getterTest() {
        assertEquals(1, customer.getId());
        assertEquals("Michael", customer.getFirstName());
        assertEquals("Wolf", customer.getLastName());
        assertEquals(LocalDate.of(1980, 3, 20), customer.getBirthDate());
        assertNull(customer.getAddress());
        assertNull(customer.getCountry());
        assertNull(customer.getPostalCode());
        assertEquals("Somestreet 20", customer2.getAddress());
        assertEquals("Austria", customer2.getCountry());
        assertEquals(1010, customer2.getPostalCode());
    }

    @Test
    void setterSetsValue() {
        Customer customer3 = new Customer(3, "Hans", "Schmidt");
        customer3.setKids(3);
        assertEquals(3, customer3.getKids());
    }

    @Test
    void setterReturnsSameObject() {
        Customer customer3 = new Customer(3, "Hans", "Schmidt");
        assertSame(customer3, customer3.setKids(3));
    }

    @Test
    void setterThrowsValidationErrorIfValueTooHigh() {
        Customer customer3 = new Customer(3, "Hans", "Schmidt");
        assertEquals("kids is higher then max 10",
                assertThrows(ValidationException.class, () -> customer3.setKids(20)).getMessage()
        );
    }

    @Test
    void setterThrowsValidationErrorIfValueTooLow() {
        Customer customer3 = new Customer(3, "Hans", "Schmidt");
        assertEquals("kids is lower then min 0",
                assertThrows(ValidationException.class, () -> customer3.setKids(-2)).getMessage()
        );
    }

    @Test
    void getPhoneNumberMaskedTest() {
        assertEquals("+4312xxxx", customer2.getPhoneNumberMasked(false));
        assertEquals("+4312xxxx", customer2.getPhoneNumberMasked(true));
        assertEquals("", customer.getPhoneNumberMasked(false));
        assertNull(customer.getPhoneNumberMasked(true));
    }

    @Test
    void getBirthDateMaskedTest() {
        assertEquals("1985-05-xx", customer2.getBirthDateMasked(false));
        assertEquals("1985-05-xx", customer2.getBirthDateMasked(true));
        assertEquals("", new Customer().getBirthDateMasked(false));
        assertNull(new Customer().getBirthDateMasked(true));
    }

    @Test
    void withTest() {
        Customer withCustomer = customer
                .withId(2)
                .withFirstName("Michaela")
                .withLastName("Gruber")
                .withBirthDate(LocalDate.of(1985, 5, 23))
                .withAddress("A-street 10")
                .withCountry("Germany")
                .withPostalCode(12345)
                .withPhoneNumber("+43123456")
                .withKids(2);


        assertEquals(2, withCustomer.getId());
        assertEquals("Michaela", withCustomer.getFirstName());
        assertEquals("Gruber", withCustomer.getLastName());
        assertEquals(LocalDate.of(1985, 5, 23), withCustomer.getBirthDate());
        assertEquals("A-street 10", withCustomer.getAddress());
        assertEquals("Germany", withCustomer.getCountry());
        assertEquals(12345, withCustomer.getPostalCode());
        assertEquals("+43123456", withCustomer.getPhoneNumber());
        assertEquals(2, withCustomer.getKids());
    }

    @Test
    void hashCodeTest() {
        assertEquals(customer.hashCode(), customer.hashCode());
        assertEquals(customer.hashCode(), new Customer(1, "Michael", "Wolf", LocalDate.of(1980, 3, 20), null, null, null, null, 0).hashCode());
        assertNotEquals(customer, customer2);

        assertEquals(customer2.hashCode(), new Customer(2, "Michaela", "Gruber", LocalDate.of(1985, 5, 23), "Somestreet 20", "Austria", 1010, "+43123456", 2).hashCode());
    }

    @Test
    void equalsTest() {
        assertEquals(customer, customer);

        Customer equalCustomer = new Customer(1, "Michael", "Wolf", LocalDate.of(1980, 3, 20), null, null, null, null, 0);
        assertEquals(customer, equalCustomer);

        assertNotEquals(customer, customer2);

        assertNotEquals(customer, new Object());

        assertNotEquals(customer, customer.withId(2));
        assertNotEquals(customer, customer.withFirstName("Test"));
        assertNotEquals(customer, customer.withLastName("Test"));
        assertNotEquals(customer, customer.withBirthDate(LocalDate.of(1981, 4, 21)));
    }

    @Test
    void databaseGetExisting(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            testDatabase.execute("Insert into CUSTOMER (ID,FIRST_NAME,LAST_NAME, BIRTH_DATE, KIDS) values (1, 'Michael', 'Wolf', date '1980-03-20', 0)");
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
    void updateThrowsForNonExistingPrimaryKey(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            assertEquals("CUSTOMER table not updated for primary key ID = '1'",
                    assertThrows(SQLException.class, () -> customer.update(testDatabase.getConnection())).getMessage()
            );
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
    void existsReturnsCorrectSate(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            assertFalse(Customer.exists(testDatabase.getConnection(), 1));

            customer.insert(testDatabase.getConnection());

            assertTrue(Customer.exists(testDatabase.getConnection(), 1));
        }
    }

    @Test
    void isInDatabaseReturnsCorrectSate(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            assertFalse(customer.isInDatabase(testDatabase.getConnection()));

            customer.insert(testDatabase.getConnection());

            assertTrue(customer.isInDatabase(testDatabase.getConnection()));
        }
    }

    @Test
    void persistInsertsOrUpdates(TestInfo testInfo) throws Exception {
        try (TestDatabase testDatabase = setupTestDatabase(testInfo)) {
            customer.persist(testDatabase.getConnection());
            assertTrue(customer.isInDatabase(testDatabase.getConnection()));

            Customer changedCustomer = customer.withCountry("Test").persist(testDatabase.getConnection());

            Optional<Customer> changedCustomerFromDb = Customer.get(testDatabase.getConnection(), 1);
            assertEquals(changedCustomer, changedCustomerFromDb.get());
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
        assertEquals(customerWithDefaults, Customer.fromJson(CUSTOMER_JSON));
    }

    @Test
    void fromJsonReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CUSTOMER_JSON.getBytes());
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream))) {
            assertEquals(customerWithDefaults, Customer.fromJson(bufferedReader));
        }
    }

    @Test
    void fromJsonInputStream() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(CUSTOMER_JSON.getBytes());
        assertEquals(customerWithDefaults, Customer.fromJson(byteArrayInputStream));
    }

    @Test
    void fromJsonFile() throws IOException {
        Path file = TEST_PATH.resolve("customer.json");
        Files.write(file, CUSTOMER_JSON.getBytes(StandardCharsets.UTF_8));

        assertEquals(customerWithDefaults, Customer.fromJson(file));
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

    @Test
    void builderTestOnlyNotNullable() {
        Customer build = Customer.builder(1, "Michael", "Wolf").build();
        assertEquals(new Customer(1, "Michael", "Wolf", null, "Unknown", null, 9999, null, 0), build);
    }

    @Test
    void builderTestSettersWithArguments() {
        Customer build = Customer.builder(1, "Michael", "Wolf")
                .setBirthDate(LocalDate.of(1980, 3, 20))
                .build();
        assertEquals(customerWithDefaults, build);

        Customer build2 = Customer.builder(2, "Michaela", "Gruber")
                .setBirthDate(LocalDate.of(1985, 5, 23))
                .setAddress("Somestreet 20")
                .setCountry("Austria")
                .setPostalCode(1010)
                .setPhoneNumber("+43123456")
                .setKids(2)
                .build();

        assertEquals(customer2, build2);
    }

    @Test
    void BuilderFormGeneratesEqualObject() {
        assertEquals(customer, customer.builderFrom().build());
    }

    @Test
    void builderTestSetters() {
        Customer build = Customer.builder()
                .setId(1)
                .setFirstName("Michael")
                .setLastName("Wolf")
                .setBirthDate(LocalDate.of(1980, 3, 20))
                .build();
        assertEquals(customerWithDefaults, build);

        Customer build2 = Customer.builder()
                .setId(2)
                .setFirstName("Michaela")
                .setLastName("Gruber")
                .setBirthDate(LocalDate.of(1985, 5, 23))
                .setAddress("Somestreet 20")
                .setCountry("Austria")
                .setPostalCode(1010)
                .setPhoneNumber("+43123456")
                .setKids(2)
                .build();

        assertEquals(customer2, build2);
    }

    @Test
    void builderThrowsOnBuildWithEmptyStringForNonEmptyMember() {
        Customer.Builder builder = Customer.builder(0, "Mike", "");
        assertEquals("lastName may not be empty",
                assertThrows(ValidationException.class, builder::build).getMessage()
        );
    }

    @Test
    void csvFileReadingTest() throws IOException {
        Files.write(CSV_FILE_PATH, CSV_FILE_CONTENTS.getBytes());
        List<Customer> list = Customer.streamCsv(CSV_FILE_PATH).collect(Collectors.toList());
        assertEquals(3, list.size());
        assertEquals(new Customer(1, "Pam", "Sparks"), list.get(0));
        assertEquals(new Customer(2, "Gina", "Rocha"), list.get(1));
        assertEquals(new Customer(3, "Kristie", "Greer"), list.get(2));
    }

    @Test
    void streamCsvThrowsOnIOError() {
        Path notExistingFile = TEST_PATH.resolve("NotExisting.csv");
        assertEquals("Could not read file '" + notExistingFile + "'",
                assertThrows(CsvReaderException.class, () -> Customer.streamCsv(notExistingFile)).getMessage().replace('\\', '/')
        );
    }

    private TestDatabase setupTestDatabase(TestInfo testInfo) throws SQLException {
        TestDatabase testDatabase = new TestDatabase(testInfo);
        testDatabase.execute("create table CUSTOMER (" +
                "id number not null," +
                "FIRST_NAME varchar2 not null," +
                "LAST_NAME varchar2 not null," +
                "BIRTH_DATE date," +
                "ADDRESS varchar2," +
                "COUNTRY varchar2," +
                "POSTAL_CODE varchar2," +
                "PHONE_NUMBER varchar2," +
                "KIDS number" +
                ")");
        return testDatabase;
    }
}
