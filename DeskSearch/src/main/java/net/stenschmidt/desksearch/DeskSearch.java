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
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

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
			
			if(args.length == 0) {
				System.out.println("Usage");
				System.out.println("====================================================================");
				System.out.println("Create new Database-File");
				System.out.println("java -jar DeskSearch.jar setup");
				System.out.println("");
				System.out.println("Start H2 Database-Server");
				System.out.println("java -jar DeskSearch.jar start");
				System.out.println("");
				System.out.println("Add files from path to the fulltext index");
				System.out.println("java -jar DeskSearch.jar index <Path to files>");
				System.out.println("");
				System.out.println("Query the fulltext index");
				System.out.println("java -jar DeskSearch.jar search foo [bar fuzz bazz]");
				System.out.println("");
				System.exit(-1);
			}
			
			String command = args[0].trim();
			DeskSearch deskSearch = new DeskSearch();

			if (new File(propertiesFile).exists()) {
				parseProperties();
			}

			switch (command) {
			case "start":
				deskSearch.startServer();
				break;
			case "search":
				// After the 'search' command all strings are searchstrings.
				String[] searchArgs = new String[args.length - 1];
				System.arraycopy(args, 1, searchArgs, 0, searchArgs.length);
				deskSearch.search(searchArgs);
				break;
			case "setup":
				deskSearch.setup();
			case "index":
				String path = args[1].trim();
				System.out.print("Indexing " + path + "...");
				if (new File(path).exists()) {
					deskSearch.index(path);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void setup() throws SQLException, URISyntaxException {
		String dbFilePath = properties.getProperty("dbfile");
		if (!new File(dbFilePath).exists()) {
			// TODO Datenbankdatei anlegen + Setup ausführen
			String setupConnection = "jdbc:h2:" + getInstallDir() + "/DeskSearch.db";
			String sqlCmd = "";
			System.out.println("Setup Connection: " + setupConnection);
			try (var con = DriverManager.getConnection(setupConnection, "", ""); var stm = con.createStatement();) {
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
					// st.setString(3, ""); //Description
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

	void index(String path) {
		try {
			Convert convert = new Convert();
			try (var con = DriverManager.getConnection(properties.getProperty("url"));
					var stm = con.createStatement();) {

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

						String fulltext = "";
						if (file.toString().toLowerCase().endsWith(".pdf")) {
							fulltext = DocParser.readPDF(file.toString());
						}
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

	void search(String... searchArgs) {
		String searchStrings = String.join(" ", searchArgs);
		System.out.println("Searching: " + searchStrings);
		try {
			try (var con = DriverManager.getConnection(properties.getProperty("url"));
					var stm = con.createStatement();) {

				String query = "SELECT SCHEMA, COLUMNS, KEYS, SCORE, ID, NAME, PATH, BYTES, CREATED, MODIFIED, ACCESSED FROM FT_SEARCH_DATA(?, 0, 0) FT, FILES F WHERE F.ID=FT.KEYS[1];";
				// String query = "SELECT SCHEMA, COLUMNS, KEYS, SCORE, ID, NAME, PATH, BYTES,
				// CREATED, MODIFIED, ACCESSED FROM FTL_SEARCH_DATA(?, 0, 0) FT, FILES F WHERE
				// F.ID=FT.KEYS[1];";

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
		try (var stream = new BufferedInputStream(new FileInputStream(propertiesFile));) {
			properties.load(stream);
			System.out.println("properties parsed: url = " + properties.getProperty("url"));
		}
	}

	private void startServer() throws SQLException {
		System.out.println("Starting Server...");
		org.h2.tools.Server.main();
	}
}
