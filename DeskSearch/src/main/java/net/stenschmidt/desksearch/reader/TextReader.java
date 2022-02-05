package net.stenschmidt.desksearch.reader;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TextReader {

	public String getText(String filePath) throws Exception {
		String result = "";
		try {
			result = new String(Files.readAllBytes(Paths.get(filePath))); 
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return result;
	}

}
