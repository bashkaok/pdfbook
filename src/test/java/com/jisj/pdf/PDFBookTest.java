package com.jisj.pdf;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.XMPMetaFactory;
import com.jisj.pdf.xmp.BookXMPSchema;
import com.jisj.pdf.xmp.WorkStruct;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PDFBookTest {
    static Path sourcePdf = Path.of("src/test/resources/pdf-test.pdf");
    static Path modifiedPdf = Path.of("src/test/resources/pdf-test-modified.pdf");
    static PDFBook book;

    //book test data
    static final String TITLE = "An American In Paris";
    static final String AUTHOR = "George Gershwin";
    static final String SUBJECT = "Описание";

    @BeforeAll
    static void setUp() throws IOException {
        book = new PDFBook(PDFReader.read(sourcePdf));
        assertNotNull(book);
    }

    @Test
    void readPdf() throws IOException, XMPException {
        assertEquals(TITLE, book.getTitle());
        assertEquals(AUTHOR, book.getAuthor());
        assertEquals(SUBJECT, book.getSubject());
        System.out.println("Keywords=" + book.getKeywords());
        System.out.println("Creator=" + book.getCreator());
        System.out.println("Creation Date=" + formatDate(book.getCreationDate()));
        System.out.println("Modification Date=" + formatDate(book.getModificationDate()));
        System.out.println("Trapped=" + book.getDocument().getDocumentInformation().getTrapped());
        assertNotNull(book.getMetadata());
        BookXMPSchema bookSchema = book.getBookXMPSchema();
        assertNotNull(bookSchema);
        assertFalse(bookSchema.getMetadata().dumpObject().isEmpty());
    }

    @Test
    void adobeParserTest() throws IOException, XMPException {
        XMPMeta meta = book.getMetadata();
        XMPMeta bs = newTestBookData(meta);
        book.setMetadata(bs);
        book.saveAs(modifiedPdf);

        PDFBook mBook = new PDFBook(PDFReader.read(modifiedPdf));
        XMPMeta as = XMPMetaFactory.parse(mBook.getDocument().getDocumentCatalog().getMetadata().exportXMPMetadata());
        assertEquals(bs.dumpObject(), as.dumpObject());

        System.out.println(meta.dumpObject());

    }

    private XMPMeta newTestBookData(XMPMeta metadata) {
        BookXMPSchema bs = new BookXMPSchema(metadata);
        bs.setTitle(TITLE);
        bs.setUUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef2619600c"));
        bs.addGenre("music");
        bs.addGenre("music_sheets");
        Calendar date_created = new Calendar.Builder().setDate(2025, 11, 1).build();
        bs.setDateCreated(date_created);
        bs.addAuthor("А.А. Составитель", UUID.fromString("b47665da-6c75-4632-952d-a2ef26190000"));
        bs.setSheets("Any", "piano", "", "");

        //Work1
        WorkStruct w1 = bs.addWork();
        w1.setTitle("Work #1");
        w1.setUUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"));
        w1.addGenre("music_sheets");
        w1.setDateCreated(date_created);
        w1.addAuthor("J.S. Bach", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"));
        w1.setSheets("F-dur", "piano", "BWV 785", "author");

        //Work2
        WorkStruct w2 = bs.addWork();
        w2.setTitle("Work #2");
        w2.setUUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196002"));
        w2.addGenre("music_sheets");
        w2.setDateCreated(date_created);
        w2.addAuthor("L.V. Beethoven", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"));
        w2.setSheets("C-sharp minor", "piano", "BWV 785", "author");

        return bs.getMetadata();
    }

    private String formatDate(Calendar date) {
        String retval = null;
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat();
            retval = formatter.format(date.getTime());
        }

        return retval;
    }
}