package com.jisj.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Static methods for PDF read/write
 */
public class PDFReader {
    public static PDDocument read(Path pdfFile) throws IOException {
        return Loader.loadPDF(pdfFile.toFile());
    }


}
