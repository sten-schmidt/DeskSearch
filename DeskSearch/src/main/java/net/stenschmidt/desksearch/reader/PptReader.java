package net.stenschmidt.desksearch.reader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.sl.extractor.SlideShowExtractor;

public class PptReader {

	public String getText(String filePath) throws Exception {
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
		        HSLFSlideShow doc = new HSLFSlideShow(bufferedInputStream);
			SlideShowExtractor extractor = new SlideShowExtractor(doc);) {
			return extractor.getText().trim();
		}
	}
}
