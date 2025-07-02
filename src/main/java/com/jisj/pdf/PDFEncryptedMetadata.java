package com.jisj.pdf;

/**
 * PDF exception class throws on encrypted metadata
 */
public class PDFEncryptedMetadata extends PDFException {
    public PDFEncryptedMetadata() {
        super("Metadata are encrypted");
    }
}
