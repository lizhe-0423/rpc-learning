package com.lizhe.bhrpccommon.exception;

/**
 * SerializerException
 * {@code @description} SerializerException
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午4:25
 * @version 1.0
 */
public class SerializerException extends RuntimeException{
    private static final long serialVersionUID = -6783134254669118520L;

    /**
     * Instantiates a new Serializer exception.
     *
     * @param e the e
     */
    public SerializerException(final Throwable e) {
        super(e);
    }

    /**
     * Instantiates a new Serializer exception.
     *
     * @param message the message
     */
    public SerializerException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new Serializer exception.
     *
     * @param message   the message
     * @param throwable the throwable
     */
    public SerializerException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
