package net.stenschmidt.desksearch.reader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class XlsReader {

    public String getText(String filePath) throws Exception {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
                HSSFWorkbook doc = new HSSFWorkbook(bufferedInputStream);
                ExcelExtractor wordExtractor = new ExcelExtractor(doc);) {
            return wordExtractor.getText().trim();
        }
    }

}
