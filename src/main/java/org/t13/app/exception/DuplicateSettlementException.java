package org.t13.app.api.exception;

public class DuplicateSettlementException extends RuntimeException {

    public DuplicateSettlementException(String message) {
        super(message);
    }
}
