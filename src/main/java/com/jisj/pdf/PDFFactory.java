package com.jisj.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Static methods for PDF classes creation
 */
public class PDFFactory {
    private PDFFactory(){}
    /**
     * Reads the PDF document
     *
     * @param pdfFile path to file
     * @return PDF document object
     * @throws IOException file reading error
     */
    public static PDDocument read(Path pdfFile) throws IOException {
        return Loader.loadPDF(pdfFile.toFile());
    }

    /**
     * Creates the PDF book wrapper
     *
     * @param pdfFile path to file
     * @return PDF book object
     * @throws IOException file reading error
     */
    public static PDFBook readBook(Path pdfFile) throws IOException {
        return new PDFBook(read(pdfFile));
    }


}
