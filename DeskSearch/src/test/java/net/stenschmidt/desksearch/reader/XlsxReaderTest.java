package net.stenschmidt.desksearch.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.stenschmidt.desksearch.reader.DocxReader;

public class XlsxReaderTest {

    @Test
    public void test_getText_Excel97File_xls() throws Exception {
        String actual = new XlsReader().getText("src/test/java/net/stenschmidt/desksearch/reader/Excel97File.xls");
        assertTrue(actual.length() > 0);
        assertEquals("Tabelle1\n"
                + "Excel97File", actual);
    }

}
