package net.weichware.jbdao;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}