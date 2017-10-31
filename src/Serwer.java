import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Properties;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Serwer {
	static private int ID = 0;

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8989), 0);
		//load last ID from properties file
		Properties config = new Properties();
		InputStream input = new FileInputStream("config.properties");
		config.load(input);
		ID = Integer.parseInt(config.getProperty("ServerID"));
		
		server.createContext("/post", new PostHandler());
		server.createContext("/get", new GetHandler());
		server.createContext("/add", new AddHandler());
		server.createContext("/login", new LoginHandler());
		server.setExecutor(null); // creates a default executor

		server.start();
	}

	static class PostHandler implements HttpHandler {
		public void handle(HttpExchange httpExchange) throws IOException {

			// Read the request
			InputStream in = httpExchange.getRequestBody();
			String inputString = new String(convertStreamToString(in));
			String[] attributes = inputString.split("\\|");

			// Get attributes from POST method
			String event = attributes[0];
			String[] attributesOnly = Arrays.copyOfRange(attributes, 1, attributes.length);

			String response = JDBC.insertInto(event, attributesOnly);

			// prepare the response
			// String response = httpExchange.getRequestMethod() + " method with
			// " + event;

			httpExchange.sendResponseHeaders(200, response.length());
			OutputStream os = httpExchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}

	}

	static class GetHandler implements HttpHandler {
		public void handle(HttpExchange httpExchange) throws IOException {

			// prepare the response
			String response = JDBC.getTables();
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

			String response = JDBC.createTable(event, attributesOnly);

			// prepare the response
			// String response = httpExchange.getRequestMethod() + " method with
			// " + event;

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
		}
	}

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
