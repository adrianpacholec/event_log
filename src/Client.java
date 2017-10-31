import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

public class Client {

	static String urlstr;
	static int ClientID;

	public static void main(String argv[]) throws IOException {

		Client client = new Client();

		client.login();

		// User interface
		Scanner input = new Scanner(System.in);
		System.out.println("Client is running. Type help for command list.");

		String decision;
		do {
			decision = input.nextLine();
			try {
				switch (decision) {
				case "post":
					System.out.println("Event type and fields separated with | :");
					client.sendPost(input.nextLine(), new URL(urlstr + "/" + decision));
					break;
				case "get":
					client.sendGet(new URL(urlstr + "/" + decision));
					break;
				case "add":
					System.out.println("Add new event type:");
					System.out.println("<event_name>|<column1_name> <column1_parameters>|<column2_name> ...");
					client.sendPost(input.nextLine(), new URL(urlstr + "/" + decision));
					break;
				case "help":
					System.out.println(
							"Avaliable commands:\nget : send request for list of avaliable events\nadd : declare new type of event\npost : send event to server\nexit : exit");
					break;
				case "exit":
					break;
				default:
					System.out.println("Option not avaliable. Type help for avaliable options.");
					break;
				}
			} catch (ConnectException e) {
				e.getMessage();
				System.out.println("Unable to connect to server. [" + e.getMessage() + "].");
			}

		} while (!decision.equals("exit"));

		System.out.println("Bye");
		input.close();

	}

	private void login() throws IOException {

		Properties config = new Properties();
		InputStream input = new FileInputStream("config_client.properties");
		config.load(input);
		urlstr = "http://" + config.getProperty("ServerIP") + ":" + config.getProperty("Port") + "/login";
		String configID = config.getProperty("ClientID");
		URL url = new URL(urlstr);
		if (configID.equals("")) {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStream in = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			ClientID = Integer.parseInt(reader.readLine());
		} else
			ClientID = Integer.parseInt(configID);

		System.out.println("Logged with ID: " + ClientID);
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
