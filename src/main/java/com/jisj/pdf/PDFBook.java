package com.jisj.pdf;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.XMPMetaFactory;
import com.adobe.internal.xmp.options.SerializeOptions;
import com.jisj.pdf.xmp.BookXMPSchema;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;

/**
 * PDF file wrapper
 */
public class PDFBook implements Closeable {
    private final PDDocument pdfDocument;

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
        } catch (XMPException | IOException e) {
            if (e.getMessage().equalsIgnoreCase("Create InputStream called without data being written before to stream."))
                throw new PDFException("PDF file is closed");
            throw new RuntimeException(e);
        }
    }

    /**
     * Replace the metadata in the document
     *
     * @param metadata metadata object
     * @throws IOException  when the metadata are encrypted
     * @throws XMPException metadata serialization error
     */
    public void setMetadata(XMPMeta metadata) throws IOException, XMPException, PDFEncryptedMetadata {
        if (isMetaDataEncrypted())
            throw new PDFEncryptedMetadata();
        pdfDocument.getDocumentCatalog()
                .getMetadata()
                .importXMPMetadata(XMPMetaFactory
                        .serializeToBuffer(metadata, new SerializeOptions(SerializeOptions.ENCODE_UTF8)));
    }

    public boolean isMetaDataEncrypted() {
        return getDocument().isEncrypted() && getDocument().getEncryption().isEncryptMetaData();
    }


    /**
     * Gives BookScheme information
     *
     * @return {@code BookXPMSchema}} object | null when the metadata are encrypted
     */
    public BookXMPSchema getBookXMPSchema() throws PDFException {
        if (isMetaDataEncrypted()) return null;
        return new BookXMPSchema(getMetadata());
    }

    public PDFInfo getDocumentInfo() {
        return new PDFInfo(pdfDocument.getDocumentInformation().getTitle(),
                pdfDocument.getDocumentInformation().getAuthor(),
                pdfDocument.getDocumentInformation().getSubject(),
                pdfDocument.getDocumentInformation().getKeywords(),
                pdfDocument.getDocumentInformation().getCreator(),
                pdfDocument.getDocumentInformation().getCreationDate(),
                pdfDocument.getDocumentInformation().getModificationDate()

        );
    }

    public void saveAs(Path fileName) throws IOException {
        getDocument().save(fileName.toFile());
    }

    /**
     * Gives the document language
     *
     * @return language name | {@code null}
     */
    public String getLanguage() {
        return getDocument().getDocumentCatalog().getLanguage();
    }


    @Override
    public void close() throws IOException {
        getDocument().close();
    }

    public record PDFInfo(String title,
                          String author,
                          String subject,
                          String keywords,
                          String creator,
                          Calendar creationDate,
                          Calendar modificationDate) {
    }
}
