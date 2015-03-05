package server_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.timer.TimerNotification;
import javax.naming.TimeLimitExceededException;

public class ServerClientHandler implements Runnable {
	private BufferedReader client_input;
	private BufferedWriter client_output;
	private RequestHandler request_handler;
	
	public ServerClientHandler(Socket user, RequestHandler request_handler) throws IOException {
		client_output = new BufferedWriter(new OutputStreamWriter(user.getOutputStream()));
		client_input = new BufferedReader(new InputStreamReader(user.getInputStream()));
		this.request_handler = request_handler;
	}
	
	private List<String> formatRequest(String request) throws IOException {
		List<String> splitted_answer = new ArrayList<>();
		
		Pattern regex_pattern = Pattern.compile("([^\\s\"']+)|\"([^\"]*)\"|'([^']*)'");
		Matcher matcher = regex_pattern.matcher(request);
		
		while (matcher.find())
			for (int i = 1; i <= 3; i++)
				if (matcher.group(i) != null)
					splitted_answer.add(matcher.group(i).toLowerCase());
		
		return splitted_answer;
	}
	
	private List<String> expectInput() throws TimeLimitExceededException, IOException {
		try {
			for (int i = 0; i < Client.WAIT_BEFORE_TIMEOUT; i += Client.SERVER_LISTENER_CHECK_INTERVAL) { //TODO: move to RH
				if (client_input.ready()) {
					String request = "";
					while (client_input.ready())
						request += client_input.readLine();
					return formatRequest(request);
				}
				Thread.sleep(Client.SERVER_LISTENER_CHECK_INTERVAL);
			}
			throw new TimeLimitExceededException();
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	private int verifyInt(String argument) throws IOException, TimeLimitExceededException {
		while (true) {
			try {
				return Integer.parseInt(argument);
			} catch (NumberFormatException e) {
				argument = ask("Argument \"" + argument + "\" is not an integer, try again:", 1).get(0);
			}
		}
	}
	
	private void send() throws IOException {
		client_output.flush();
	}
	
	private List<String> ask(String question, int number_of_arguments) throws IOException, TimeLimitExceededException {
		while (true) {
			message(question);
			send();
			
			List<String> response = expectInput();
			
			if (response.size() == number_of_arguments)
				return response;
			
			message("Please provide " + number_of_arguments + " argument(s)!");
		}
	}
	
	private void message(String message) throws IOException {
		client_output.write(message);
		client_output.write(System.lineSeparator());
	}
	
	private boolean askYesOrNo(String question) throws IOException, TimeLimitExceededException {
		while (true) {
			List<String> response = ask(question + " (answer with yes[y] or no[n])", 1);
			
			if (response.get(0).equals("yes") || response.get(0).equals("y"))
				return true;
			else if (response.get(0).equals("no") || response.get(0).equals("n"))
				return false;
			
			message("Invalid response!");
		}
	}
	
	private String handleRequest(String request) throws IOException {
		
		List<String> formatted = formatRequest(request);
		
		if (formatted.isEmpty())
			return "Invalid request!";
		
		String command = formatted.get(0);
		List<String> arguments = formatted.subList(1, formatted.size()-1);
		
		switch (command) {
			case "asd":
				break;
			default:
				break;
		}
		
		return "Test";
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				while (client_input.ready()) {
					message(handleRequest(client_input.readLine()));
					send();
				}
				Thread.sleep(Client.SERVER_LISTENER_CHECK_INTERVAL); //TODO: Add in requesthandler
			} catch (InterruptedException | IOException e) {
				return;
			}
		}
	}
}