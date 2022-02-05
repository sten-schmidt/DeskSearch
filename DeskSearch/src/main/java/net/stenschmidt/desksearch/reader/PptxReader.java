package net.stenschmidt.desksearch.reader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

public class PptxReader {

	public String getText(String filePath) throws Exception {
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
		        XMLSlideShow doc = new XMLSlideShow(bufferedInputStream);
			SlideShowExtractor extractor = new SlideShowExtractor(doc);) {
			return extractor.getText().trim();
		}
	}
}
