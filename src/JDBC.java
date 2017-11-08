import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBC {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static String DB_URL;
	// Database credentials
	static String DATABASE;
	static String USER;
	static String PASS;

	public JDBC(String adress, String port, String database, String user, String pass) {
		DB_URL = "jdbc:mysql://" + adress + ":" + port + "/";
		DATABASE = database;
		USER = user;
		PASS = pass;
	}

	public int createDatabase(String dbName) {
		try {
			Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
			// Create database
			Statement statement = connection.createStatement();
			String sql = "CREATE DATABASE " + dbName;
			statement.executeUpdate(sql);
			// End connection
			statement.close();
			connection.close();
			return 200;
		} catch (SQLException wyjatek) {
			return wyjatek.getErrorCode();
		}
	}

	public String createTable(String tableName, String[] columns) {
		try {
			Connection connection = DriverManager.getConnection(DB_URL + DATABASE + "?useSSL=false", USER, PASS);
			// Create table
			Statement statement = connection.createStatement();
			String sql = "CREATE TABLE " + tableName + " (";
			sql += "TIME DATETIME, ";
			sql += "ID INT, ";
			for (String column : columns) {
				sql += column + ", ";
			}
			sql = sql.substring(0, sql.length() - 2);
			sql += ")";
			statement.executeUpdate(sql);
			// End connection
			statement.close();
			connection.close();
			return "Event " + tableName + " successfully added.";
		} catch (SQLException wyjatek) {
			return wyjatek.getMessage();
		}
	}

	public String insertInto(String tableName, String clientID, String[] values) {
		try {
			java.util.Date date = new java.util.Date();
			Properties props = new Properties();
			props.setProperty("user", USER);
			props.setProperty("password", PASS);
			props.setProperty("ssl", "false");
			Connection connection = DriverManager.getConnection(DB_URL + DATABASE + "?useSSL=false", props);
			// Insert into database
			Statement statement = connection.createStatement();
			String sql = "INSERT INTO " + tableName + " VALUES(";
			sql += "'" + new java.sql.Timestamp(date.getTime()) + "', ";
			sql += "'" + clientID + "', ";
			for (String value : values) {
				sql += "'" + value + "', ";
			}
			sql = sql.substring(0, sql.length() - 2);
			sql += ")";
			statement.executeUpdate(sql);
			// End connection
			statement.close();
			connection.close();
			return "Event of type " + tableName + " saved.";
		} catch (SQLException wyjatek) {
			return wyjatek.getMessage();
		}
	}

	public String getTables() {
		StringBuilder events = new StringBuilder();
		try {
			Connection connection = DriverManager.getConnection(DB_URL + DATABASE + "?useSSL=false", USER, PASS);
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
				String tableName = rs.getString(3); // gets table name
				events.append(tableName + ": ");
				ResultSet columns = md.getColumns(null, null, tableName, null);
				columns.next();
				columns.next();
				while (columns.next()) {
					events.append(columns.getString(4) + " (" + columns.getString(6) + ")");
					if (!columns.isLast())
						events.append(" | ");
				}
				events.append("\n");
			}
		} catch (SQLException e) {
			return e.getMessage();
		}
		return events.toString();
	}

	public void deleteData(String table, String where) {
		try {
			Connection connection = DriverManager.getConnection(DB_URL + DATABASE + "?useSSL=false", USER, PASS);
			Statement statement = connection.createStatement();
			String query = "DELETE FROM " + table;
			if (!where.isEmpty())
				query += " WHERE " + where;
			statement.executeUpdate(query);
			System.out.println("Deleted successfully.");

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void displayData(String table, String where, String sort, String order) {
		try {
			Connection connection = DriverManager.getConnection(DB_URL + DATABASE + "?useSSL=false", USER, PASS);
			Statement statement = connection.createStatement();

			System.out.println("\nDisplaying info from " + table + "\n-------------------------");
			String query = "SELECT * FROM " + table;
			if (!where.isEmpty())
				query += " WHERE " + where;
			if (!sort.isEmpty())
				query += " ORDER BY " + sort + " " + order;

			ResultSet rs = statement.executeQuery(query);
			ResultSetMetaData md = rs.getMetaData();

			int colCount = md.getColumnCount();
			for (int i = 1; i <= colCount; i++)
				System.out.print(md.getColumnName(i) + " | ");
			System.out.print("\n");
			while (rs.next()) {
				for (int i = 1; i <= colCount; i++)
					System.out.print(rs.getString(i) + " | ");
				System.out.print("\n");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}