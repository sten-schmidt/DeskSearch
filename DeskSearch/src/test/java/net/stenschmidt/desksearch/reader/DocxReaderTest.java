package net.stenschmidt.desksearch.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.stenschmidt.desksearch.reader.DocxReader;

public class DocxReaderTest {
	@Test
	public void test_getText_WordFile_docm() throws Exception {
		String actual = new DocxReader().getText("src/test/java/net/stenschmidt/desksearch/reader/WordFile.docm");
		assertTrue(actual.length() > 0);
		assertEquals("Word File 123 WordDocm", actual);
	}

	@Test
	public void test_getText_WordFile_docx() throws Exception {
		String actual = new DocxReader().getText("src/test/java/net/stenschmidt/desksearch/reader/WordFile.docx");
		assertTrue(actual.length() > 0);
		assertEquals("Word File 123 docx", actual);
	}
	
	@Test
	public void test_getText_WordFile_dotm() throws Exception {
		String actual = new DocxReader().getText("src/test/java/net/stenschmidt/desksearch/reader/WordFile.dotm");
		assertTrue(actual.length() > 0);
		assertEquals("Word File 123\n"
				+ "WordDotmVorlage", actual);
	}
	
	@Test
	public void test_getText_WordFile_dotx() throws Exception {
		String actual = new DocxReader().getText("src/test/java/net/stenschmidt/desksearch/reader/WordFile.dotx");
		assertTrue(actual.length() > 0);
		assertEquals("Word File 123\n"
				+ "WordDotxVorlage", actual);
	}
}
