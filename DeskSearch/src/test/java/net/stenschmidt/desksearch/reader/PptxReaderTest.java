package net.stenschmidt.desksearch.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PptxReaderTest {
    @Test
    public void test_getText_PowerPointFile_pptx() throws Exception {
        String actual = new PptxReader().getText("src/test/java/net/stenschmidt/desksearch/reader/PowerPointFile.pptx");
        assertTrue(actual.length() > 0);
        assertEquals("PowerPointFile", actual);
    }

}
