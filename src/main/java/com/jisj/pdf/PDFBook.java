package com.jisj.pdf;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.XMPMetaFactory;
import com.adobe.internal.xmp.options.SerializeOptions;
import com.jisj.pdf.xmp.BookXMPSchema;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;

/**
 * PDF file wrapper
 */
public class PDFBook {
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
     * @return metadata object
     * @throws IOException  when document reading error
     * @throws XMPException when document parsing error
     */
    public XMPMeta getMetadata() throws IOException, XMPException {
        return XMPMetaFactory.parse(pdfDocument.getDocumentCatalog().getMetadata().exportXMPMetadata());
    }

    public void setMetadata(XMPMeta metadata) throws IOException, XMPException {
        pdfDocument.getDocumentCatalog()
                .getMetadata()
                .importXMPMetadata(XMPMetaFactory
                        .serializeToBuffer(metadata, new SerializeOptions(SerializeOptions.ENCODE_UTF8)));
    }

    /**
     * Gives BookScheme information
     *
     * @return {@code BookSchema} object | null
     */
    public BookXMPSchema getBookXMPSchema() throws IOException, XMPException {
        return new BookXMPSchema(getMetadata());
    }

    public String getTitle() {
        return pdfDocument.getDocumentInformation().getTitle();
    }

    public String getAuthor() {
        return pdfDocument.getDocumentInformation().getAuthor();
    }

    public String getSubject() {
        return pdfDocument.getDocumentInformation().getSubject();
    }

    public String getKeywords() {
        return pdfDocument.getDocumentInformation().getKeywords();
    }

    public String getCreator() {
        return pdfDocument.getDocumentInformation().getCreator();
    }

    public Calendar getCreationDate() {
        return pdfDocument.getDocumentInformation().getCreationDate();
    }

    public Calendar getModificationDate() {
        return pdfDocument.getDocumentInformation().getModificationDate();
    }

    public void save() {
    }

    public void saveAs(Path fileName) throws IOException {
        getDocument().save(fileName.toFile());
    }


}
