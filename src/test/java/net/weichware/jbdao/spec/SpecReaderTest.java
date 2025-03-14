package net.weichware.jbdao.spec;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class SpecReaderTest {
    private static final Path TEST_PATH =  Paths.get("./src/test/java/net/weichware/jbdao/spec/");
    private static String CUSTOMER_JSON;

    @BeforeAll
    static void beforeAll() throws IOException {
        CUSTOMER_JSON = new String(Files.readAllBytes(TEST_PATH.resolve("Customer.json")));
    }

    @Test
    void readSpecRetunsCorrectRepresentation() {
        assertEquals("lala", Specification.readSpec(CUSTOMER_JSON));
    }
}