package dev.palestiner.jbplugins.exception;

public class MarketplaceException extends RuntimeException {

    public MarketplaceException(String message) {
        super(message);
    }

    public MarketplaceException(String message, Throwable cause) {
        super(message, cause);
    }

}
