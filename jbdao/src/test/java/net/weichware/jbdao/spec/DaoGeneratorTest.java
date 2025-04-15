package net.weichware.jbdao.spec;

import net.weichware.jbdao.Customer;
import net.weichware.jbdao.DaoGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DaoGeneratorTest {
    private static final Path TEST_PATH = Paths.get("./src/test/java/net/weichware/jbdao/spec/");
    private static final Path OUTOUT_PATH = Paths.get("./target/test/DaoWriterTest");
    private static final Path OUTOUT_PACKAGE_PATH = Paths.get("./target/test/DaoWriterTest/net/weichware/jbdao");
    private static final Path EXPECTED_PATH = Paths.get("./src/test/java/net/weichware/jbdao/");
    private static final String CUSTOMER = "Customer.java";
    private static String CUSTOMER_JSON;

    @BeforeAll
    static void beforeAll() throws IOException {
        CUSTOMER_JSON = new String(Files.readAllBytes(TEST_PATH.resolve("Customer.json")));
        Files.createDirectories(OUTOUT_PATH);
    }

    @Test
    void generateCustomer() throws IOException {
        new DaoGenerator(Specification.readSpec(CUSTOMER_JSON), OUTOUT_PATH).generate();
        String actual = new String(Files.readAllBytes(OUTOUT_PACKAGE_PATH.resolve(CUSTOMER)));
        String expected = new String(Files.readAllBytes(EXPECTED_PATH.resolve(CUSTOMER)));

        assertEquals(expected, actual);
    }

    @Test
    void csvReadTest() {
        //Customer.streamCsv(Paths.get("../customers-2000000.csv")).forEach(c->System.out.println(c.getId() + ": " + c.getFirstName() + " " + c.getLastName() ));
        long start = System.currentTimeMillis();
        List<Customer> collect = Customer.streamCsv(Paths.get("../customers-2000000.csv")).collect(Collectors.toList());
        long end = System.currentTimeMillis();
        System.out.println("records: " + collect.size());
        System.out.println("time needed: " + ((end - start) / 1000.0));

    }
}