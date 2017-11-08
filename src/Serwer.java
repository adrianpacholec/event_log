import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Serwer {

	static private int ID;
	static Properties config;
	static JDBC dbConnector;

	public static void main(String[] args) throws Exception {

		config = new Properties();
		InputStream input = new FileInputStream("config.properties");
		config.load(input);
		dbConnector = new JDBC(config.getProperty("DbAdress"),
				config.getProperty("DbPort"),
				config.getProperty("DbName"),
				config.getProperty("DbUser"),
				config.getProperty("DbPass"));
		HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(config.getProperty("Port"))), 0);

		ID = Integer.parseInt(config.getProperty("ServerID"));
		input.close();

		server.createContext("/post", new PostHandler());
		server.createContext("/get", new GetHandler());
		server.createContext("/add", new AddHandler());
		server.createContext("/login", new LoginHandler());
		server.setExecutor(null); // creates a default executor
		server.start();

		Scanner keyboard = new Scanner(System.in);
		System.out.println("Welcome to LogViewer.");
		String decision;
		do {
			System.out.println("-------------------------\nYou can type: ");
			System.out.println("'select' to view data from selected log");
			System.out.println("'delete' to delete selected entries from chosen log");
			System.out.println("'exit' to close the server");
			decision = keyboard.nextLine();
			String table = "", where = "", sort = "", order = "";
			switch (decision) {
			case "delete":
			case "select":
				System.out.println("\nAvaliable tables and their custom columns: \n-------------------------");
				System.out.print(dbConnector.getTables());
				System.out.println("-------------------------\nPlease enter event type: ");
				table = keyboard.nextLine();
				System.out.println("Do you want to choose results to " + decision
						+ " ?\nType [column name] ( = / <> / > / < / >= / <= ) [value] or 'no'.");
				where = keyboard.nextLine();
				if (where.equals("no"))
					where = "";
				if (decision.equals("select")) {
					System.out.println(
							"Do you want to sort results? Type [column name] by which you'd like to sort or 'no'.");
					sort = keyboard.nextLine();
					if (!sort.equals("no")) {

						System.out.println("Ascending or descending? [asc/desc]");
						order = keyboard.nextLine();
					} else
						sort = "";
					dbConnector.displayData(table, where, sort, order);
				} else {
					dbConnector.deleteData(table, where);
				}

				break;
			case "exit":
				keyboard.close();
				server.stop(0);
				break;
			}

		} while (!decision.equals("exit"));

	}

	static class PostHandler implements HttpHandler {

		public void handle(HttpExchange httpExchange) throws IOException {

			// Read the request
			InputStream in = httpExchange.getRequestBody();
			String inputString = new String(convertStreamToString(in));
			String[] attributes = inputString.split("\\|");

			// Get attributes from POST method
			String client_id = attributes[0];
			String event = attributes[1];
			String[] attributesOnly = Arrays.copyOfRange(attributes, 2, attributes.length);
			String response = dbConnector.insertInto(event, client_id, attributesOnly);
			httpExchange.sendResponseHeaders(200, response.length());
			OutputStream os = httpExchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}

	}

	static class GetHandler implements HttpHandler {
		public void handle(HttpExchange httpExchange) throws IOException {

			// prepare the response
			String response = dbConnector.getTables();
			httpExchange.sendResponseHeaders(200, response.length());
			OutputStream os = httpExchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	static class AddHandler implements HttpHandler {
		public void handle(HttpExchange httpExchange) throws IOException {

			// Read the request
			InputStream in = httpExchange.getRequestBody();
			String inputString = new String(convertStreamToString(in));
			String[] attributes = inputString.split("\\|");

			// Get attributes from POST method
			String event = attributes[0];
			String[] attributesOnly = Arrays.copyOfRange(attributes, 1, attributes.length);
			String response = dbConnector.createTable(event, attributesOnly);
			httpExchange.sendResponseHeaders(200, response.length());
			OutputStream os = httpExchange.getResponseBody();
			os.write(response.getBytes());
			os.close();

		}
	}

	static class LoginHandler implements HttpHandler {
		public void handle(HttpExchange httpExchange) throws IOException {

			// prepare the response
			httpExchange.sendResponseHeaders(200, Integer.toString(ID).length());
			OutputStream os = httpExchange.getResponseBody();
			os.write(Integer.toString(ID++).getBytes());
			os.close();

			FileInputStream in = new FileInputStream("config.properties");
			Properties props = new Properties();
			props.load(in);
			in.close();

			FileOutputStream out = new FileOutputStream("config.properties");
			props.setProperty("ServerID", Integer.toString(ID));
			props.store(out, null);
			out.close();
		}
	}
	//https://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
	static String convertStreamToString(java.io.InputStream is) {
		@SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
