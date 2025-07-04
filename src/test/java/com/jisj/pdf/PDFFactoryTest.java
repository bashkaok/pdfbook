package com.jisj.pdf;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.jisj.pdf.PDFFactory.*;
import static org.junit.jupiter.api.Assertions.*;

public class PDFFactoryTest {
    static Path sourcePdf = Path.of("src/test/resources/BWV998.pdf");


    @Test
    void readPdf() throws Exception {
        assertNotNull(read(sourcePdf));
    }
}
