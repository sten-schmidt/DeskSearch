package net.stenschmidt.desksearch.reader;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfReader {

    public static String readPDF(String pdfFilePath) throws IOException {
        String result = "";
        try (PDDocument doc = PDDocument.load(new File(pdfFilePath));) {
            result = new PDFTextStripper().getText(doc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return result;
    }

}
