package net.stenschmidt.desksearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class DeskSearchTest {
    @Test
    public void getFulltext_GetText_FromFile() throws IOException {
        DeskSearch ds = new DeskSearch();
        String actual;
        
        actual = ds.getFulltext(Paths.get("src/test/java/net/stenschmidt/desksearch/reader/Word97File.doc"));
        assertEquals("Word97 File 123", actual);
        
        
    }

    @Test
    public void getInstallDir_Contains_Unix_Path_Seperator() {
        try {
            assertTrue(DeskSearch.getInstallDir().length() > 5);
            assertFalse(DeskSearch.getInstallDir().contains("\\"));
            assertTrue(DeskSearch.getInstallDir().contains("/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
