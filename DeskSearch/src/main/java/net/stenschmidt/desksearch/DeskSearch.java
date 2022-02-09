package net.stenschmidt.desksearch;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Properties;

import net.stenschmidt.desksearch.reader.*;

public class DeskSearch {

    private static final String propertiesFile = "DeskSearch.properties";
    static Properties properties = new Properties();

    static {
        try {
            createProperties();
            parseProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String... args) {
        try {

            if (args.length == 0) {
                System.out.println("DeskSearch usage");
                System.out.println("====================================================================");
                System.out.println("Create new Database-File");
                System.out.println("java -jar DeskSearch.jar setup");
                System.out.println("");
                System.out.println("Compact the Database-File");
                System.out.println("java -jar DeskSearch.jar compact");
                System.out.println("");
                System.out.println("Start H2 Database-Server");
                System.out.println("java -jar DeskSearch.jar server");
                System.out.println("");
                System.out.println("Add files from path to the fulltext index");
                System.out.println("java -jar DeskSearch.jar index <Path to files>");
                System.out.println("");
                System.out.println("Query the fulltext index");
                System.out.println("java -jar DeskSearch.jar search foo [bar fuzz bazz]");
                System.out.println("");
                System.out.println("Query existing words from index using wildcards");
                System.out.println("java -jar DeskSearch.jar words %JAVA%");
                System.out.println("");
                System.exit(-1);
            }

            String command = args[0].trim();
            DeskSearch deskSearch = new DeskSearch();

            if (new File(propertiesFile).exists()) {
                parseProperties();
            }

            switch (command) {
            case "server":
                deskSearch.startServer();
                break;
            case "search":
                // After the 'search' command all strings are searchstrings.
                String[] searchArgs = new String[args.length - 1];
                System.arraycopy(args, 1, searchArgs, 0, searchArgs.length);
                deskSearch.search(searchArgs);
                break;
            case "words":
                // After the 'words' command all strings are searchstrings.
                deskSearch.findWords(args[1]);
                break;
            case "setup":
                deskSearch.setup();
                break;
            case "compact":
                deskSearch.compact();
                break;
            case "index":
                String path = args[1].trim();
                System.out.print("Indexing " + path + "...");
                if (new File(path).exists()) {
                    deskSearch.index(path);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void compact() throws URISyntaxException, SQLException {
        String setupConnection = "jdbc:h2:" + getInstallDir() + "/DeskSearch.db";
        try (Connection con = DriverManager.getConnection(setupConnection, "", "");
                Statement stm = con.createStatement();) {
            String sqlCmd = "shutdown compact;";
            stm.execute(sqlCmd);
        }
    }

    void setup() throws SQLException, URISyntaxException {
        String dbFilePath = properties.getProperty("dbfile");
        if (!new File(dbFilePath).exists()) {
            String setupConnection = "jdbc:h2:" + getInstallDir() + "/DeskSearch.db";
            String sqlCmd = "";
            System.out.println("Setup Connection: " + setupConnection);
            try (Connection con = DriverManager.getConnection(setupConnection, "", "");
                    Statement stm = con.createStatement();) {
                sqlCmd = "create table files(id bigint auto_increment primary key, name varchar(255), path varchar(4000), fulltext varchar(1048576), bytes bigint, created timestamp, modified timestamp, accessed timestamp, indexed timestamp);";
                stm.execute(sqlCmd);

                sqlCmd = "CREATE ALIAS IF NOT EXISTS FT_INIT FOR 'org.h2.fulltext.FullText.init';";
                stm.execute(sqlCmd);

                sqlCmd = "CALL FT_INIT();";
                stm.execute(sqlCmd);

                Timestamp tsNow = new Timestamp(System.currentTimeMillis());

                String insertQuery = new StringBuilder().append("insert into files ")
                        .append("(name, path, fulltext, created, modified, accessed, bytes, indexed) values ")
                        .append("(?,?,?,?,?,?,?,?);").toString();

                try (PreparedStatement st = con.prepareStatement(insertQuery)) {
                    st.setString(1, "dummy");
                    st.setString(2, "c:\\dummy.pdf");
                    st.setString(3, "dummy foo bar"); // Fulltext
                    st.setTimestamp(4, tsNow);
                    st.setTimestamp(5, tsNow);
                    st.setTimestamp(6, tsNow);
                    st.setLong(7, 0);
                    st.setTimestamp(8, tsNow);
                    st.executeUpdate();
                }

                sqlCmd = "CALL FT_CREATE_INDEX('PUBLIC', 'FILES', 'NAME,PATH,FULLTEXT');";
                stm.execute(sqlCmd);
            }
        }
    }

    /*
     * #REINDEX # -> TimeStampNow merken # -> Query Path already exists # -> Ja ->
     * Update # -> No -> Insert # -> Delete from files where indexed < TimeStampNow
     */

    void index(String path) {
        try {
            Convert convert = new Convert();
            try (Connection con = DriverManager.getConnection(properties.getProperty("url"));
                    Statement stm = con.createStatement();) {

                try (PreparedStatement st = con.prepareStatement("delete from FILES where PATH like ? ESCAPE '!'")) {
                    st.setString(1, path + "%");
                    st.executeUpdate();
                }

                Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach(file -> {

                    System.out.println(file);
                    try {
                        BasicFileAttributes fileAttributes = Files.readAttributes(file, BasicFileAttributes.class);

                        Timestamp tsCreate = convert.toTimestamp(fileAttributes.creationTime());
                        Timestamp tsModified = convert.toTimestamp(fileAttributes.lastModifiedTime());
                        Timestamp tsAccess = convert.toTimestamp(fileAttributes.lastAccessTime());
                        Timestamp tsNow = new Timestamp(System.currentTimeMillis());
                        long fileSize = fileAttributes.size();

                        String insertQuery = new StringBuilder().append("insert into files ")
                                .append("(name, path, fulltext, created, modified, accessed, bytes, indexed) values ")
                                .append("(?,?,?,?,?,?,?,?);").toString();

                        String fulltext = getFulltext(file);

                        try (PreparedStatement st = con.prepareStatement(insertQuery)) {
                            st.setString(1, file.getFileName().toString());
                            st.setString(2, file.toString());
                            st.setString(3, fulltext); // Fulltext
                            st.setTimestamp(4, tsCreate);
                            st.setTimestamp(5, tsModified);
                            st.setTimestamp(6, tsAccess);
                            st.setLong(7, fileSize);
                            st.setTimestamp(8, tsNow);
                            st.executeUpdate();
                        }
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                });
                System.out.println("done.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getFulltext(Path file) throws IOException {
        String fulltext = "";
        String fileExtension = "";

        try {
            int dotPos = file.toString().lastIndexOf('.');
            fileExtension = file.toString().toLowerCase().substring(dotPos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (fileExtension) {
        /*
         * TODO case ".rtf" case ".7z" case ".zip" case ".tar" case ".tar.gz" case
         * ".tar.bz2"
         */
        case ".pdf":
            fulltext = PdfReader.readPDF(file.toString());
            break;
        case ".pptx":
            try {
                fulltext = new PptxReader().getText(file.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        case ".ppt":
            try {
                fulltext = new PptReader().getText(file.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        case ".dotx":
        case ".dotm":
        case ".docx":
        case ".docm":
            try {
                fulltext = new DocxReader().getText(file.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        case ".dot":
        case ".doc":
            try {
                fulltext = new DocReader().getText(file.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        case ".txt":
        case ".log":
        case ".info":
        case ".nfo":
        case ".html":
        case ".htm":
        case ".eml":
        case ".xml":
        case ".bat":
        case ".cmd":
        case ".ps1":
        case ".sh":
        case ".py":
        case ".java":
        case ".properties":
            try {
                fulltext = new TextReader().getText(file.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        case ".xls":
            try {
                fulltext = new XlsReader().getText(file.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        case ".xlsx":
            try {
                fulltext = new XlsxReader().getText(file.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;

        }
        return fulltext;
    }

    void search(String... searchArgs) {
        String searchStrings = String.join(" ", searchArgs);
        System.out.println("Searching: " + searchStrings);
        try {
            try (Connection con = DriverManager.getConnection(properties.getProperty("url"));
                    Statement stm = con.createStatement();) {

                String query = "SELECT SCHEMA, COLUMNS, KEYS, SCORE, ID, NAME, PATH, BYTES, CREATED, MODIFIED, ACCESSED FROM FT_SEARCH_DATA(?, 0, 0) FT, FILES F WHERE F.ID=FT.KEYS[1] ORDER BY PATH;";

                try (PreparedStatement st = con.prepareStatement(query)) {
                    st.setString(1, searchStrings);
                    ResultSet rs = st.executeQuery();

                    int count = 0;
                    if (null != rs) {
                        while (rs.next()) {
                            System.out.println(++count + ": " + rs.getString("PATH"));
                        }
                    }
                }

            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }

    void findWords(String searchString) {
        try {

            if (searchString.length() > 0) {
                searchString = searchString.toUpperCase();
            }

            try (Connection con = DriverManager.getConnection(properties.getProperty("url"));
                    Statement stm = con.createStatement();) {

                String query = "select NAME from FT.WORDS where NAME like '" + searchString + "';";

                try (PreparedStatement st = con.prepareStatement(query)) {
                    // st.setString(1, searchString);
                    ResultSet rs = st.executeQuery();

                    int count = 0;
                    if (null != rs) {
                        while (rs.next()) {
                            System.out.println(++count + ": " + rs.getString("NAME"));
                        }
                    }
                }

            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    static String getInstallDir() throws URISyntaxException {
        String result = new File(DeskSearch.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                .getParentFile().getPath().replace("\\", "/");
        System.out.println("getInstallDir: " + result);
        return result;
    }

    static void createProperties() throws IOException, URISyntaxException {
        File f = new File(propertiesFile);
        if (!f.exists()) {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(propertiesFile)));) {
                out.write(String.format("dbfile = %s/%s\n", getInstallDir(), "DeskSearch.db"));
                out.write(String.format("url = jdbc:h2:tcp://localhost/%s/DeskSearch.db\n", getInstallDir()));
            }
        }
    }

    static void parseProperties() throws FileNotFoundException, IOException {
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(propertiesFile));) {
            properties.load(stream);
        }
    }

    private void startServer() throws SQLException {
        System.out.println("Starting Server...");
        String[] serverArgs = new String[2];
        serverArgs[0] = "-web";
        serverArgs[1] = "-tcp";
        org.h2.tools.Server.main(serverArgs);
    }
}
