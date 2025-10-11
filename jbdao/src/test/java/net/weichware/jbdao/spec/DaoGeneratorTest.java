package net.weichware.jbdao.spec;

import net.weichware.jbdao.ConcreteClassGenerator;
import net.weichware.jbdao.DaoGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DaoGeneratorTest {
    private static final Path TEST_PATH = Paths.get("./src/test/java/net/weichware/jbdao/spec/");
    private static final Path OUTOUT_PATH = Paths.get("./target/test/DaoWriterTest");
    private static final Path OUTOUT_PACKAGE_PATH = Paths.get("./target/test/DaoWriterTest/net/weichware/myapp");
    private static final Path EXPECTED_PATH = Paths.get("./src/test/java/net/weichware/myapp/");
    private static final String CUSTOMER = "Customer.java";
    private static final String CUSTOMER_SQL = "Customer.sql";
    private static final String USER = "User.java";
    private static final String ABSTRACT_USER = "AbstractUser.java";
    private static final String PRODUCT = "Product.java";
    private static final String RECORD = "Record.java";
    private static String CUSTOMER_JSON;
    private static String USER_JSON;
    private static String PRODUCT_JSON;
    private static String RECORD_JSON;

    @BeforeAll
    static void beforeAll() throws IOException {
        CUSTOMER_JSON = new String(Files.readAllBytes(TEST_PATH.resolve("Customer.json")));
        USER_JSON = new String(Files.readAllBytes(TEST_PATH.resolve("User.json")));
        PRODUCT_JSON = new String(Files.readAllBytes(TEST_PATH.resolve("Product.json")));
        RECORD_JSON = new String(Files.readAllBytes(TEST_PATH.resolve("RECORD.json")));
        Files.createDirectories(OUTOUT_PATH);
    }

    @Test
    void generateCustomer() throws IOException {
        new DaoGenerator(Specification.readSpec(CUSTOMER_JSON), OUTOUT_PATH).generate();
        String actual = new String(Files.readAllBytes(OUTOUT_PACKAGE_PATH.resolve(CUSTOMER)));
        String expected = new String(Files.readAllBytes(EXPECTED_PATH.resolve(CUSTOMER)));

        assertEquals(expected, actual);

        actual = new String(Files.readAllBytes(OUTOUT_PATH.resolve(CUSTOMER_SQL)));
        expected = new String(Files.readAllBytes(EXPECTED_PATH.resolve(CUSTOMER_SQL)));

        assertEquals(expected, actual);
    }

    @Test
    void generateAbstractUser() throws IOException {
        new DaoGenerator(Specification.readSpec(USER_JSON), OUTOUT_PATH).generate();
        String actual = new String(Files.readAllBytes(OUTOUT_PACKAGE_PATH.resolve(ABSTRACT_USER)));
        String expected = new String(Files.readAllBytes(EXPECTED_PATH.resolve(ABSTRACT_USER)));

        assertEquals(expected, actual);
    }

    @Test
    void generateConcreteUser() throws IOException {
        new ConcreteClassGenerator(Specification.readSpec(USER_JSON), OUTOUT_PATH).generate();
        String actual = new String(Files.readAllBytes(OUTOUT_PACKAGE_PATH.resolve(USER)));
        String expected = new String(Files.readAllBytes(EXPECTED_PATH.resolve(USER)));

        assertEquals(expected, actual);
    }

    @Test
    void generateProduct() throws IOException {
        new DaoGenerator(Specification.readSpec(PRODUCT_JSON), OUTOUT_PATH).generate();
        String actual = new String(Files.readAllBytes(OUTOUT_PACKAGE_PATH.resolve(PRODUCT)));
        String expected = new String(Files.readAllBytes(EXPECTED_PATH.resolve(PRODUCT)));

        assertEquals(expected, actual);
    }

    @Test
    void generateRecord() throws IOException {
        new DaoGenerator(Specification.readSpec(RECORD_JSON), OUTOUT_PATH).generate();
        String actual = new String(Files.readAllBytes(OUTOUT_PACKAGE_PATH.resolve(RECORD)));
        String expected = new String(Files.readAllBytes(EXPECTED_PATH.resolve(RECORD)));

        assertEquals(expected, actual);
    }
}