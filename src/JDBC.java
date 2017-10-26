import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBC {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/";

	// Database credentials
	static final String TABLE = "pracownicy";
	static final String DATABASE = "test";
	static final String USER = "root";
	static final String PASS = "test";

	public static void main(String[] args) {

		// String polaczenieURL =
		// "jdbc:mysql://localhost:3306/test?user=root&password=test";
		// Tworzymy proste zapytanie doa bazy danych

		// Connection connection = null;
		// Statement statement = null;

		// try {
		// Register JDBC driver
		try {
			Class.forName(JDBC_DRIVER);

			getTables();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// createDatabase(DATABASE);
		// createTable(TABLE);
		// insertInto(TABLE);

		// // Uruchamiamy zapytanie do bazy danych
		// statement = connection.createStatement();
		//
		// // PreparedStatement prepStatement =
		// // connection.prepareStatement("SELECT * FROM pracownicy WHERE
		// // Imie=? AND Nazwisko=?");
		// // prepStatement.setString(1, "Henryk");
		// // prepStatement.setString(2, "Skoczylas");
		//
		// // Wyniki, iterator
		// String query = "Select * FROM " + TABLE;
		// ResultSet resultset = statement.executeQuery(query);
		//
		// displayData(resultset);
		// statement.execute("SHUTDOWN");
		// statement.close();
		// connection.close();
		// } catch (ClassNotFoundException wyjatek) {
		// wyjatek.printStackTrace();
		// System.out.println("Problem ze sterownikiem");
		// }
		//
		// catch (SQLException wyjatek) {
		// wyjatek.printStackTrace();
		// System.out.println(
		// "Problem z logowaniem\nProsze sprawdzic:\n nazwę użytkownika, hasło,
		// nazwę bazy danych lub adres IP serwera");
		// System.out.println("SQLException: " + wyjatek.getMessage());
		// System.out.println("SQLState: " + wyjatek.getSQLState());
		// System.out.println("VendorError: " + wyjatek.getErrorCode());
		// }

		// finally {
		// try {
		// if (connection != null)
		// connection.close();
		// } catch (SQLException se) {
		// se.printStackTrace();
		// }

		// }

	}

	static void createDatabase(String dbName) {
		try {
			Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);

			// Create database
			Statement statement = connection.createStatement();
			String sql = "CREATE DATABASE " + dbName;
			statement.executeUpdate(sql);

			// End connection
			statement.close();
			connection.close();

		} catch (SQLException wyjatek) {
			wyjatek.printStackTrace();
			System.out.println("SQLException: " + wyjatek.getMessage());
			System.out.println("SQLState: " + wyjatek.getSQLState());
			System.out.println("VendorError: " + wyjatek.getErrorCode());
		}

	}

	static void createTable(String tableName, String[] columns) {
		try {
			Connection connection = DriverManager.getConnection(DB_URL + DATABASE, USER, PASS);
			// Create table
			Statement statement = connection.createStatement();
			String sql = "CREATE TABLE " + tableName + " (";
			for (String column : columns) {
				sql += column + ", ";
			}
			sql = sql.substring(0, sql.length() - 2);
			sql += ")";

			statement.executeUpdate(sql);
			// End connection
			statement.close();
			connection.close();

		} catch (SQLException wyjatek) {
			wyjatek.printStackTrace();
			System.out.println("SQLException: " + wyjatek.getMessage());
			System.out.println("SQLState: " + wyjatek.getSQLState());
			System.out.println("VendorError: " + wyjatek.getErrorCode());
		}

	}

	static void insertInto(String tableName, String[] values) {
		try {
			Properties props = new Properties();
			props.setProperty("user",USER);
			props.setProperty("password",PASS);
			props.setProperty("ssl","false");
			Connection connection = DriverManager.getConnection(DB_URL + DATABASE, props);
			// Insert into database
			Statement statement = connection.createStatement();

			String sql = "INSERT INTO " + tableName + " VALUES(";
			for (String value : values) {
				sql += "'" + value + "', ";
			}
			sql = sql.substring(0, sql.length() - 2);
			sql += ")";
			statement.executeUpdate(sql);
			// End connection
			statement.close();
			connection.close();

		} catch (SQLException wyjatek) {
			wyjatek.printStackTrace();
			System.out.println("SQLException: " + wyjatek.getMessage());
			System.out.println("SQLState: " + wyjatek.getSQLState());
			System.out.println("VendorError: " + wyjatek.getErrorCode());
		}
	}

	static String getTables() {
		StringBuilder events = new StringBuilder();
		try {
			Connection connection = DriverManager.getConnection(DB_URL + DATABASE, USER, PASS);
			DatabaseMetaData md = connection.getMetaData();

			ResultSet rs = md.getTables(null, null, "%", null);

			while (rs.next()) {
				String tableName = rs.getString(3); // gets table name
				events.append(tableName + ": ");
				ResultSet columns = md.getColumns(null, null, tableName, null);
				while (columns.next()) {
					events.append(columns.getString(4) + " (" + columns.getString(6) + ")");
					if (!columns.isLast())
						events.append(" | ");
				}
				events.append("\n");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return events.toString();
	}

	static void displayData(ResultSet rs) {
		try {
			// daneZBazy = rs.getString(1);
			// System.out.println("\n" + daneZBazy + " ");
			// daneZBazy = rs.getString(2);
			// System.out.println(daneZBazy + " ");
			// daneZBazy = rs.getString(3);
			// System.out.println(daneZBazy);

			while (rs.next()) {
				System.out.print(rs.getString(1) + " | " + rs.getString(2) + " | " + rs.getString(3) + "\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}