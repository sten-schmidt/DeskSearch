package net.stenschmidt.desksearch.parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class DocReader {

	public String getText(String filePath) throws Exception {
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
				HWPFDocument doc = new HWPFDocument(bufferedInputStream);
				WordExtractor wordExtractor = new WordExtractor(doc);) {
			return wordExtractor.getText();
		}
	}

}
