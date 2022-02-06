package net.stenschmidt.desksearch.reader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxReader {

    public String getText(String filePath) throws Exception {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
                XSSFWorkbook doc = new XSSFWorkbook(bufferedInputStream);
                XSSFExcelExtractor wordExtractor = new XSSFExcelExtractor(doc);) {
            return wordExtractor.getText().trim();
        }
    }

}
