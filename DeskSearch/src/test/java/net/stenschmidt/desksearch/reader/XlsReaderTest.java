package net.stenschmidt.desksearch.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.stenschmidt.desksearch.reader.DocxReader;

public class XlsReaderTest {

    @Test
    public void test_getText_ExcelFile_xlsx() throws Exception {
        String actual = new XlsxReader().getText("src/test/java/net/stenschmidt/desksearch/reader/ExcelFile.xlsx");
        assertTrue(actual.length() > 0);
        assertEquals("Tabelle1\n"
                + "ExcelFile", actual);
    }

}
