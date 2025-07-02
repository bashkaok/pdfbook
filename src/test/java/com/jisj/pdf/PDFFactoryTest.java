package com.jisj.pdf;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PDFFactoryTest {
    static Path sourcePdf = Path.of("src/test/resources/pdf-test.pdf");

    @Test
    void readPdf() throws Exception {
        assertNotNull(PDFFactory.read(sourcePdf));
    }
}
