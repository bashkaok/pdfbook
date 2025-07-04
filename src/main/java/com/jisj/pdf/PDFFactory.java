package com.jisj.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Static methods for PDF classes creation
 */
public class PDFFactory {
    private PDFFactory() {
    }

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
    public static PDFBook readPDF(Path pdfFile) throws IOException {
        PDFBook book;
        book = new PDFBook(read(pdfFile));
        book.setBookPath(pdfFile);
        return book;
    }

    /**
     * Creates new PDF print object
     *
     * @param document PDF document
     * @return new object
     */
    public static PDFPrint newPDFPrint(PDDocument document) {
        return new PDFPrint(document);
    }
}
