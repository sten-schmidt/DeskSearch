package net.stenschmidt.desksearch.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DocxReaderTest {
	@Test
	public void test_getText_WordFile_docm() throws Exception {
		String actual = new DocxReader().getText("src/test/java/net/stenschmidt/desksearch/parser/WordFile.docm");
		assertTrue(actual.length() > 0);
		assertEquals("Word File 123 WordDocm", actual);
	}

	@Test
	public void test_getText_WordFile_docx() throws Exception {
		String actual = new DocxReader().getText("src/test/java/net/stenschmidt/desksearch/parser/WordFile.docx");
		assertTrue(actual.length() > 0);
		assertEquals("Word File 123 docx", actual);
	}
	
	@Test
	public void test_getText_WordFile_dotm() throws Exception {
		String actual = new DocxReader().getText("src/test/java/net/stenschmidt/desksearch/parser/WordFile.dotm");
		assertTrue(actual.length() > 0);
		assertEquals("Word File 123\n"
				+ "WordDotmVorlage", actual);
	}
	
	@Test
	public void test_getText_WordFile_dotx() throws Exception {
		String actual = new DocxReader().getText("src/test/java/net/stenschmidt/desksearch/parser/WordFile.dotx");
		assertTrue(actual.length() > 0);
		assertEquals("Word File 123\n"
				+ "WordDotxVorlage", actual);
	}
}
