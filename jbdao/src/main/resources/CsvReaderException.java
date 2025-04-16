package net.weichware.jbdao;

public class CsvReaderException extends RuntimeException {
    public CsvReaderException(String message) {
        super(message);
    }

    public CsvReaderException(String message, Exception exception) {
        super(message, exception);
    }
}
