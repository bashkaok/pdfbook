package com.jisj.pdf;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.jisj.pdf.xmp.BookXMPSchema;
import com.jisj.pdf.xmp.WorkStruct;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
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
        book = new PDFBook(PDFFactory.read(sourcePdf));
        assertNotNull(book);
    }

    @Test
    void readPdf() throws PDFException {
        assertEquals(TITLE, book.getDocumentInfo().title());
        assertEquals(AUTHOR, book.getDocumentInfo().author());
        assertEquals(SUBJECT, book.getDocumentInfo().subject());
        System.out.println(book.getDocumentInfo());
        assertNotNull(book.getMetadata());
        BookXMPSchema bookSchema = book.getBookXMPSchema();
        assertNotNull(bookSchema);
        assertFalse(bookSchema.getMetadata().dumpObject().isEmpty());
        System.out.println(book.getDocument().getDocumentCatalog().getLanguage());
    }

    @Test
    void read_set_metadata() throws PDFException, IOException, XMPException {
        XMPMeta meta = book.getMetadata();
        book.setMetadata(meta);
        assertEquals(meta.dumpObject(), book.getMetadata().dumpObject());
        setAndAssert(meta);
        BookXMPSchema bs = new BookXMPSchema(meta);
        setMainRecord(bs);
        setAndAssert(meta);
        book.saveAs(modifiedPdf);
        assertModified(meta);

        setWork1(bs);
        setAndAssert(meta);

        setWork2(bs);
        setAndAssert(meta);

        setWork3(bs);
        setAndAssert(meta);

        book.saveAs(modifiedPdf);
        assertModified(meta);
    }

    private void setAndAssert(XMPMeta meta) throws IOException, XMPException, PDFException {
        book.setMetadata(meta);
        assertEquals(meta.dumpObject(), book.getMetadata().dumpObject());
    }

    private void assertModified(XMPMeta source) throws IOException, PDFException {
        PDFBook mBook = new PDFBook(PDFFactory.read(modifiedPdf));
        XMPMeta as = mBook.getMetadata();
        assertEquals(source.dumpObject(), as.dumpObject());

    }


    @Test
    void addXMP_to_protected_PDFTest() throws IOException {
        try (PDFBook mBook = new PDFBook(PDFFactory.read(Path.of("src/test/resources/BWV998.PDF")))) {

//        PDFBook mBook = new PDFBook(PDFReader.read(protectedPdf));
            System.out.println("isEncrypted=" + mBook.getDocument().isEncrypted());
            System.out.println("isEncryptMetaData=" + mBook.getDocument().getEncryption().isEncryptMetaData());
            System.out.println("?=" + Integer.toBinaryString(mBook.getDocument().getEncryption().getPermissions()));

            AccessPermission ap = mBook.getDocument().getCurrentAccessPermission();
            System.out.println("Current PDF Permissions:");
            System.out.println("Can Assemble Document: " + ap.canAssembleDocument());
            System.out.println("Can Copy Content: " + ap.canExtractContent());
            System.out.println("Can Extract Content for Accessibility: " + ap.canExtractForAccessibility());
            System.out.println("Can Fill Form Fields: " + ap.canFillInForm());
            System.out.println("Can Modify Annotations: " + ap.canModifyAnnotations());
            System.out.println("Can Modify Content: " + ap.canModify());
            System.out.println("Can Print: " + ap.canPrint());
//        System.out.println("Can Print Degraded: " + ap.canPrintDegraded());
            System.out.println("Is Owner Permission: " + ap.isOwnerPermission());

            assertThrowsExactly(PDFEncryptedMetadata.class, mBook::getMetadata);
            assertNotNull(mBook.getDocumentInfo());
            System.out.println(mBook.getDocumentInfo());
        }
    }

    @Test
    void addXMP_to_PDFTest() throws IOException, XMPException, PDFException {
        XMPMeta meta = book.getMetadata();
        XMPMeta bs = newTestBookData(meta);
        book.setMetadata(bs);
        book.saveAs(modifiedPdf);
        assertModified(meta);


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

    private void setMainRecord(BookXMPSchema bs) {
        bs.setTitle(TITLE);
        bs.setUUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef2619600c"));
        bs.addGenre("music");
        bs.addGenre("music_sheets");
        Calendar date_created = new Calendar.Builder().setDate(2025, 11, 1).build();
        bs.setDateCreated(date_created);
        bs.addAuthor("А.А. Составитель", UUID.fromString("b47665da-6c75-4632-952d-a2ef26190000"));
        bs.setSheets("Any", "piano", "", "");
    }

    private void setWork1(BookXMPSchema bs) {
        WorkStruct w1 = bs.addWork();
        w1.setTitle("Work #1");
        w1.setUUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"));
        w1.addGenre("music_sheets");
        Calendar date_created = new Calendar.Builder().setDate(2025, 11, 1).build();
        w1.setDateCreated(date_created);
        w1.addAuthor("J.S. Bach", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"));
        w1.setSheets("F-dur", "piano", "BWV 785", "author");
    }

    private void setWork2(BookXMPSchema bs) {
        WorkStruct w2 = bs.addWork();
        w2.setTitle("Work #2");
        w2.setUUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196002"));
        w2.addGenre("music_sheets");
        Calendar date_created = new Calendar.Builder().setDate(2025, 11, 1).build();
        w2.addAuthor("L.V. Beethoven", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"));
        w2.setSheets("C-sharp minor", "piano", "BWV 785", "author");

    }

    private void setWork3(BookXMPSchema bs) {
        WorkStruct w = bs.addWork();
        w.setTitle("Work #3");
        w.setUUID(UUID.fromString("b47665da-6c75-4632-952d-b2ef26196002"));
        w.addGenre("music_sheets");
        Calendar date_created = new Calendar.Builder().setDate(2025, 11, 1).build();
        w.addAuthor("W.A. Mozart", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196003"));
        w.setSheets("C-sharp minor", "piano", "-", "author");

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