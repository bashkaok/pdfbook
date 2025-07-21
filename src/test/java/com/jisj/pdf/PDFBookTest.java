package com.jisj.pdf;

import com.adobe.internal.xmp.XMPMeta;
import com.jisj.pdf.xmp.BookXMPSchema;
import com.jisj.pdf.xmp.WorkStruct;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

import static com.jisj.pdf.PDFFactory.readPDF;
import static org.junit.jupiter.api.Assertions.*;

class PDFBookTest {
    static Path sourcePdf = Path.of("src/test/resources/pdf-test.pdf");
    static Path targetPdf = Path.of("src/test/resources/pdf-test-target.pdf");
    static Path enryptPdf = Path.of("src/test/resources/BWV998.PDF");
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
    void readTargetPdf() throws PDFException, IOException {
        PDFBook tPdf = new PDFBook(PDFFactory.read(targetPdf));
        assertEquals(5, tPdf.getBookXMPSchema().getWorks().size());
        tPdf.getBookXMPSchema().getWorks().forEach(System.out::println);
    }

    @Test
    void read_set_metadata() throws PDFException, IOException {
        XMPMeta meta = book.getMetadata();
        book.setMetadata(meta);
        assertEquals(meta.dumpObject(), book.getMetadata().dumpObject());
        setAndAssert(meta);
        BookXMPSchema bs = new BookXMPSchema(meta);
        setMainRecord(bs);
        setAndAssert(meta);
        book.saveAs(targetPdf);
        assertModified(meta);

        setWork1(bs);
        setAndAssert(meta);

        setWork2(bs);
        setAndAssert(meta);

        setWork3(bs);
        setAndAssert(meta);

        book.saveAs(targetPdf);
        assertModified(meta);
    }

    private void setAndAssert(XMPMeta meta) throws PDFException {
        book.setMetadata(meta);
        assertEquals(meta.dumpObject(), book.getMetadata().dumpObject());
    }

    private void assertModified(XMPMeta source) throws PDFException, IOException {
        PDFBook mBook = new PDFBook(PDFFactory.read(targetPdf));
        XMPMeta as = mBook.getMetadata();
        assertEquals(source.dumpObject(), as.dumpObject());

    }

    @Test
    void dateConvert() {
        LocalDate ld = LocalDate.of(2025, 7, 1);
        assertEquals(ld, LocalDate.parse(ld.toString()));
    }

    @Test
    void readEncrypted_PDF() {
        try (PDFBook mBook = readPDF(enryptPdf)) {

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
            System.out.println(mBook.getDocument().getDocumentCatalog().getCOSObject());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addXMP_to_PDFTest() throws PDFException, IOException {
        XMPMeta meta = book.getMetadata();
        XMPMeta bs = newTestBookData(meta);
        book.setMetadata(bs);
        book.saveAs(targetPdf);
        assertModified(meta);


        System.out.println(meta.dumpObject());

    }

    private XMPMeta newTestBookData(XMPMeta metadata) {
        BookXMPSchema bs = new BookXMPSchema(metadata);
        bs.setTitle(TITLE, "ru");
        bs.setGUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef2619600c"));
        bs.addGenre("music");
        bs.addGenre("music_sheets");
        LocalDate date_created = LocalDate.of(2025, 11, 1);
        bs.setDateCreated(date_created);
        bs.addAuthor("А.А. Составитель", "ru", UUID.fromString("b47665da-6c75-4632-952d-a2ef26190000"));
        bs.setSheets("Any", "piano", "", "");

        //Work1
        WorkStruct w1 = bs.addWork();
        w1.setTitle("Work #1", "en");
        w1.setGUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"));
        w1.addGenre("music_sheets");
        w1.setDateCreated(date_created);
        w1.addAuthor("J.S. Bach", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"));
        w1.setSheets("F-dur", "piano", "BWV 785", "author");

        //Work2
        WorkStruct w2 = bs.addWork();
        w2.setTitle("Work #2", "en");
        w2.setGUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196002"));
        w2.addGenre("music_sheets");
        w2.setDateCreated(date_created);
        w2.addAuthor("L.V. Beethoven", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"));
        w2.setSheets("C-sharp minor", "piano", "BWV 785", "author");

        return bs.getMetadata();
    }

    private void setMainRecord(BookXMPSchema bs) {
        bs.setTitle(TITLE, "ru");
        bs.setGUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef2619600c"));
        bs.addGenre("music");
        bs.addGenre("music_sheets");
        LocalDate date_created = LocalDate.of(2025, 11, 1);
        bs.setDateCreated(date_created);
        bs.addAuthor("А.А. Составитель", "ru", UUID.fromString("b47665da-6c75-4632-952d-a2ef26190000"));
        bs.setSheets("Any", "piano", "", "");
    }

    private void setWork1(BookXMPSchema bs) {
        WorkStruct w1 = bs.addWork();
        w1.setTitle("Work #1", "en");
        w1.setGUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"));
        w1.addGenre("music_sheets");
        LocalDate date_created = LocalDate.of(2025, 11, 1);
        w1.setDateCreated(date_created);
        w1.addAuthor("J.S. Bach", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196000"));
        w1.setSheets("F-dur", "piano", "BWV 785", "author");
    }

    private void setWork2(BookXMPSchema bs) {
        WorkStruct w2 = bs.addWork();
        w2.setTitle("Work #2", "ru");
        w2.setGUID(UUID.fromString("b47665da-6c75-4632-952d-a2ef26196002"));
        w2.addGenre("music_sheets");
        w2.setDateCreated(LocalDate.of(2025, 11, 1));
        w2.addAuthor("L.V. Beethoven", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196001"));
        w2.setSheets("C-sharp minor", "piano", "BWV 785", "author");

    }

    private void setWork3(BookXMPSchema bs) {
        WorkStruct w = bs.addWork();
        w.setTitle("Work #3", "de");
        w.setGUID(UUID.fromString("b47665da-6c75-4632-952d-b2ef26196002"));
        w.addGenre("music_sheets");
        w.setDateCreated(LocalDate.of(2025, 11, 1));
        w.addAuthor("W.A. Mozart", UUID.fromString("b47665da-6c75-4632-952d-a2ef26196003"));
        w.setSheets("C-sharp minor", "piano", "-", "author");

    }


}