package com.jisj.pdf;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.XMPMetaFactory;
import com.adobe.internal.xmp.options.SerializeOptions;
import com.jisj.pdf.xmp.BookXMPSchema;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PDF file wrapper
 */
public class PDFBook implements Closeable {
    private final PDDocument pdfDocument;
    private Path bookPath;

    public PDFBook(PDDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    public PDDocument getDocument() {
        return pdfDocument;
    }

    /**
     * Gives PDF document metadata
     *
     * @return metadata object | {@code null} when the PDF is encrypted
     * @throws PDFException when document reading error, document metadata is encrypted or when metadata parsing error
     */
    public XMPMeta getMetadata() throws PDFException {
        if (isMetaDataEncrypted())
            throw new PDFEncryptedMetadata();
        PDMetadata metadata = pdfDocument.getDocumentCatalog().getMetadata();
        if (metadata == null) return null;
        try {
            return XMPMetaFactory.parseFromBuffer(metadata.toByteArray());
        } catch (IOException e) {
            if (e.getMessage().equalsIgnoreCase("Create InputStream called without data being written before to stream."))
                throw new PDFException("PDF file is closed");
            throw new RuntimeException(e);
        } catch (XMPException e) {
            throw new PDFException("Metadata parsing error", e);
        }
    }

    /**
     * Gives the book file path
     *
     * @return file path
     */
    public Path getBookPath() {
        return bookPath;
    }

    /**
     * Sets the book file path
     *
     * @param bookPath book file path
     */
    public void setBookPath(Path bookPath) {
        this.bookPath = bookPath;
    }

    /**
     * Replace the metadata in the document
     *
     * @param metadata metadata object
     * @throws PDFException         metadata serialization error, metadata read/write error
     * @throws PDFEncryptedMetadata when the metadata are encrypted
     */
    public void setMetadata(XMPMeta metadata) throws PDFException {
        if (isMetaDataEncrypted())
            throw new PDFEncryptedMetadata();
        try {
            pdfDocument.getDocumentCatalog()
                    .getMetadata()
                    .importXMPMetadata(XMPMetaFactory
                            .serializeToBuffer(metadata, new SerializeOptions(SerializeOptions.ENCODE_UTF8)));
        } catch (IOException e) {
            throw new PDFException("Metadata saving error", e);
        } catch (XMPException e) {
            throw new PDFException("Metadata serialization error", e);
        }
    }

    /**
     * Checks the metadata encryption
     *
     * @return {@code true} if metadata are encrypted
     */
    public boolean isMetaDataEncrypted() {
        return getDocument().isEncrypted() && getDocument().getEncryption().isEncryptMetaData();
    }


    /**
     * Gives BookScheme information
     *
     * @return {@code BookXPMSchema}} object | null when the metadata are encrypted
     * @throws PDFException during metadata reading and parsing
     */
    public BookXMPSchema getBookXMPSchema() throws PDFException {
        if (isMetaDataEncrypted()) return null;
        return new BookXMPSchema(getMetadata());
    }

    /**
     * Gives main information fields from PDF {@code PDDocumentInformation}
     * @return record with fields
     */
    public PDFInfo getDocumentInfo() {
        return new PDFInfo(
                pdfDocument.getDocumentInformation().getTitle(),
                pdfDocument.getDocumentInformation().getAuthor(),
                pdfDocument.getDocumentInformation().getSubject(),
                pdfDocument.getDocumentInformation().getKeywords(),
                pdfDocument.getDocumentInformation().getCreator(),
                pdfDocument.getDocumentInformation().getCreationDate(),
                pdfDocument.getDocumentInformation().getModificationDate()

        );
    }

    /**
     * Sets the document info fields in PDF
     *
     * @param info   PDFInfo record. All fields with {@code null} value will be omitted
     * @param fields field names need to set. Empty - all fields
     */
    public void setDocumentInfo(PDFInfo info, String... fields) {
        PDDocumentInformation di = pdfDocument.getDocumentInformation();
        Set<String> names = Arrays.stream(fields).map(String::toLowerCase).collect(Collectors.toSet());
        if (fields.length == 0 || names.contains("title")) di.setTitle(info.title());
        if (fields.length == 0 || names.contains("author")) di.setAuthor(info.author());
        if (fields.length == 0 || names.contains("subject")) di.setSubject(info.subject());
        if (fields.length == 0 || names.contains("keywords")) di.setKeywords(info.keywords());
        if (fields.length == 0 || names.contains("creator")) di.setCreator(info.creator());
        if (fields.length == 0 || names.contains("creationdate")) di.setCreationDate(info.creationDate);
        if (fields.length == 0 || names.contains("modificationdate")) di.setModificationDate(info.modificationDate());
    }

    public void saveAs(Path fileName) throws PDFException {
        try {
            getDocument().save(fileName.toFile());
        } catch (IOException e) {
            throw new PDFException("File save error: " + fileName, e);
        }
    }

    /**
     * Gives the document language
     *
     * @return language name | {@code null}
     */
    public String getLanguage() {
        return getDocument().getDocumentCatalog().getLanguage();
    }

    /**
     * Gives the file size in bytes
     *
     * @return file size | {@code 0L} if the file doesn't exist or {@link #getBookPath()} is null
     */
    public long getFileSize() {
        if (getBookPath() != null)
            return getBookPath().toFile().length();
        return 0L;
    }


    @Override
    public void close() throws IOException {
        getDocument().close();
    }

    /**
     * PDF document information
     *
     * @param title
     * @param author
     * @param subject
     * @param keywords
     * @param creator
     * @param creationDate
     * @param modificationDate
     */
    public record PDFInfo(String title,
                          String author,
                          String subject,
                          String keywords,
                          String creator,
                          Calendar creationDate,
                          Calendar modificationDate) {
    }
}
