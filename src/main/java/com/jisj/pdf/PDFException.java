package com.jisj.pdf;

/**
 * PDF exception
 */
public class PDFException extends Exception {
    public PDFException(String message) {
        super(message);
    }

    public PDFException(String message, Throwable cause) {
        super(message, cause);
    }
}
