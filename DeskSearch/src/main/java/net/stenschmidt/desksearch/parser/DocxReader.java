package net.stenschmidt.desksearch.parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class DocxReader {

	public String getText(String filePath) throws Exception {
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
				XWPFDocument doc = new XWPFDocument(bufferedInputStream);
				XWPFWordExtractor wordExtractor = new XWPFWordExtractor(doc);) {
			return wordExtractor.getText().trim();
		}
	}

}
