package com.geega.bsc.id.common.exception;

public class InvalidReceiveException extends DistributedIdException {

    public InvalidReceiveException(String message) {
        super(message);
    }

    public InvalidReceiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
