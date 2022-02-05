package net.stenschmidt.desksearch.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.stenschmidt.desksearch.reader.DocxReader;

public class PptReaderTest {
    @Test
    public void test_getText_PowerPointFile_ppt() throws Exception {
        String actual = new PptReader()
                .getText("src/test/java/net/stenschmidt/desksearch/reader/PowerPoint97File.ppt");
        assertTrue(actual.length() > 0);
        assertEquals("PowerPoint97File", actual);
    }
}
