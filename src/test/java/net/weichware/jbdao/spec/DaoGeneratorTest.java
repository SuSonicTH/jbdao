package net.weichware.jbdao.spec;

import net.weichware.jbdao.DaoGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class DaoGeneratorTest {
    private static final Path TEST_PATH = Paths.get("./src/test/java/net/weichware/jbdao/spec/");
    private static final Path OUTOUT_PATH = Paths.get("./target/test/DaoWriterTest");
    private static String CUSTOMER_JSON;

    @BeforeAll
    static void beforeAll() throws IOException {
        CUSTOMER_JSON = new String(Files.readAllBytes(TEST_PATH.resolve("Customer.json")));
        Files.createDirectories(OUTOUT_PATH);
    }

    @Test
    void generateCustomer() throws IOException {
        new DaoGenerator(Specification.readSpec(CUSTOMER_JSON), OUTOUT_PATH).generate();
    }
}