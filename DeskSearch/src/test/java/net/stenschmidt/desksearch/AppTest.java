package net.stenschmidt.desksearch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.sql.SQLException;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
	/**
	 * Rigorous Test :-)
	 * @throws URISyntaxException 
	 * @throws SQLException 
	 */
	@Test
	public void shouldAnswerWithTrue() throws SQLException, URISyntaxException {
		assertTrue(true);
		
		DeskSearch ds = new DeskSearch();
		//ds.setup();
		
	}
	
	@Test
	public void getInstallDir_Contains_Unix_Path_Seperator() {
		DeskSearch ds = new DeskSearch();
		try {
			assertTrue(ds.getInstallDir().length() > 5);
			assertFalse(ds.getInstallDir().contains("\\"));
			assertTrue(ds.getInstallDir().contains("/"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
