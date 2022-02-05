package net.stenschmidt.desksearch.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.stenschmidt.desksearch.reader.TextReader;

public class TextReaderTest {
	@Test
	public void test_getText_TextFile_txt() throws Exception {
		String actual = new TextReader().getText("src/test/java/net/stenschmidt/desksearch/reader/TextFile.txt");
		assertTrue(actual.length() > 0);
		assertEquals("Simple text file", actual);
	}

}
