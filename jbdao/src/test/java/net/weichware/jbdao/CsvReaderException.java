package net.weichware.jbdao;

import java.io.IOException;

public class CsvReaderException extends RuntimeException {
    public CsvReaderException(String message) {
        super(message);
    }

    public CsvReaderException(String message, Exception exception) {
        super(message, exception);
    }
}
