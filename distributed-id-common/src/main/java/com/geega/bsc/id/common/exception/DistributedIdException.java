package com.geega.bsc.id.common.exception;

/**
 * The base class of all other Kafka exceptions
 */
public class DistributedIdException extends RuntimeException {

    private final static long serialVersionUID = 1L;

    public DistributedIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public DistributedIdException(String message) {
        super(message);
    }

    public DistributedIdException(Throwable cause) {
        super(cause);
    }

    public DistributedIdException() {
        super();
    }

}
