import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Serwer {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8989), 0);
		server.createContext("/post", new PostHandler());
		server.createContext("/get", new GetHandler());
		server.createContext("/add", new AddHandler());
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

			JDBC.insertInto(event, attributesOnly);

			// prepare the response
			String response = httpExchange.getRequestMethod() + " method with " + event;

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

			JDBC.createTable(event, attributesOnly);

			// prepare the response
			String response = httpExchange.getRequestMethod() + " method with " + event;

			httpExchange.sendResponseHeaders(200, response.length());
			OutputStream os = httpExchange.getResponseBody();
			os.write(response.getBytes());
			os.close();

		}
	}

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
