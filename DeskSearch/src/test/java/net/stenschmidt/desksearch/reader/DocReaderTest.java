package net.stenschmidt.desksearch.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.stenschmidt.desksearch.reader.DocReader;

public class DocReaderTest {
	@Test
	public void test_getText_Word97File_doc() throws Exception {
		String actual = new DocReader().getText("src/test/java/net/stenschmidt/desksearch/reader/Word97File.doc");
		assertTrue(actual.length() > 0);
		assertEquals("Word97 File 123", actual);
	}

	@Test
	public void test_getText_Word97File_dot() throws Exception {
		String actual = new DocReader().getText("src/test/java/net/stenschmidt/desksearch/reader/Word97File.dot");
		assertTrue(actual.length() > 0);
		assertEquals("Word97 File 123\r\n" + "Word97Dot Vorlage", actual);
	}
}
