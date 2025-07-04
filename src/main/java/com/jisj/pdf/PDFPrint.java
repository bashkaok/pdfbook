package com.jisj.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.MediaSizeName;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.nio.file.Path;

/**
 * PDF print job class
 */
public class PDFPrint {
    private final PrinterJob job;
    private final PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();

    /**
     * Creates new printer job for document
     *
     * @param document PDF document
     */
    public PDFPrint(PDDocument document) {
        job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        job.setJobName("MyPrinting");
        attr.add(MediaSizeName.ISO_A4);
    }

    /**
     * Sets the file name when to file printing
     *
     * @param fileName absolute file path
     */
    public void setFileName(Path fileName) {
        job.setJobName(fileName.getFileName().toString());
        attr.add(new Destination(fileName.toUri()));
    }

    /**
     * Gives the printer job for document
     *
     * @return printer job object
     */
    public PrinterJob getJob() {
        return job;
    }

    /**
     * Gives the current print service object
     *
     * @return current print service
     */
    public PrintService getPrintService() {
        return job.getPrintService();
    }

    /**
     * Gives the print service object by specified name
     *
     * @param serviceName string printer name
     * @return print service | {@code null}
     */
    public PrintService getPrintService(String serviceName) {
        for (PrintService ps : PrinterJob.lookupPrintServices()) {
            if (ps.getName().equalsIgnoreCase(serviceName)) {
                return ps;
            }
        }
        return null;
    }

    /**
     * Sets the printer
     *
     * @param printService print service object
     * @throws IllegalArgumentException when specified print service is null
     */
    public void setPrintService(PrintService printService) {
        if (printService == null)
            throw new IllegalArgumentException("Unexpected printer value = null");
        try {
            job.setPrintService(printService);
        } catch (PrinterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the printer by printer name
     *
     * @param printerName printer string name
     * @throws PDFException when the printer not found
     */
    public void setPrinter(String printerName) throws PDFException {
        PrintService printer = getPrintService(printerName);
        if (printer == null)
            throw new PDFException("Printer not found: " + printerName);
        setPrintService(printer);
    }


    /**
     * Prints the PDF with current attributes
     *
     * @throws PDFException an error in the print system caused the job to be aborted
     */
    public void print() throws PDFException {
        try {
            job.print(attr);
        } catch (PrinterException e) {
            throw new PDFException("Printer job error", e);
        }

    }

    /**
     * Prints the PDF with print dialog
     *
     * @throws PDFException an error in the print system caused the job to be aborted
     */
    public void printDialog() throws PDFException {
        if (job.printDialog(attr)) {
            try {
                job.print(attr);
            } catch (PrinterException e) {
                throw new PDFException("Printer job error", e);
            }
        }
    }
}
