package com.jisj.pdf;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.jisj.pdf.PDFFactory.newPDFPrint;
import static com.jisj.pdf.PDFFactory.read;
import static org.junit.jupiter.api.Assertions.*;

class PDFPrintTest {
    static Path sourcePdf = Path.of("src/test/resources/BWV998.pdf");
    static Path targetPdf = Path.of("target/test-data").resolve(sourcePdf.getFileName());

    @Test
    void print() throws IOException, PDFException {
        Files.deleteIfExists(targetPdf);

        PDFPrint prn = newPDFPrint(read(sourcePdf));
        assertNotNull(prn.getPrintService("Microsoft Print To PDF"));
        prn.setPrintService(prn.getPrintService("Microsoft Print To PDF"));
        assertEquals("Microsoft Print To PDF", prn.getPrintService().getName());
        prn.setFileName(targetPdf);
        prn.print();
        assertTrue(Files.exists(targetPdf));
    }
}