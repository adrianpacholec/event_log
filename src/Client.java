import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Client {

	static String urlstr = "http://localhost:8989";

	public static void main(String argv[]) throws IOException {

		Client client = new Client();
		// test

		// User interface
		Scanner input = new Scanner(System.in);
		System.out.println("Uruchomiono klienta, co robimy?");

		String decision;
		do {
			decision = input.nextLine();
			switch (decision) {
			case "post":
				System.out.println("Event type and fields separated with | :");
				client.sendPost(input.nextLine(), new URL(urlstr + "/" + decision));
				break;
			case "get":
				System.out.println("< sending GET request >");
				client.sendGet(new URL(urlstr + "/" + decision));
				break;
			case "add":
				System.out.println("Add new event type:");
				System.out.println("<event_name>|<column1_name> <column1_parameters>|<column2_name> ...");
				client.sendPost(input.nextLine(), new URL(urlstr + "/" + decision));
			case "exit":
				break;
			default:
				System.out.println("Niepoprawna opcja");
				break;
			}

		} while (!decision.equals("exit"));

		System.out.println("Koniec");
		input.close();

	}

	private void sendPost(String message, URL url) throws IOException {

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true); // Triggers POST.

		DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
		writer.writeBytes(message);

		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
			System.out.println(result.toString());
		}
	}

	private void sendGet(URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line + "\n");

		}
		System.out.println("Avaliable events: ");
		System.out.println(result);
	}
}
