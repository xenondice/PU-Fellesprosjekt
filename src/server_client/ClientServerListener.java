package server_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ClientServerListener implements Runnable {
	private BufferedReader server_input;
	private BufferedWriter console_output;
	private Thread client_thread;
	
	public ClientServerListener(BufferedWriter console_output, BufferedReader server_input, Thread client_thread) {
		this.console_output = console_output;
		this.server_input = server_input;
		this.client_thread = client_thread;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				if (server_input.ready()) {
					
					if (!Client.ready()) {
						console_output.write(System.lineSeparator());
						console_output.write(System.lineSeparator());
						console_output.write("Incomming message from server:");
						console_output.write(System.lineSeparator());
						console_output.flush();
					}
					
					while (server_input.ready()) {
						console_output.write(server_input.read());
						console_output.flush();
						client_thread.interrupt();
					}
					
					if (!Client.ready()) {
						console_output.write(System.lineSeparator());
						console_output.flush();
					}
					
					Client.markEnd();
					
					client_thread.interrupt(); //TODO: use Client.ready()
				}
				
				Thread.sleep(Client.SERVER_LISTENER_CHECK_INTERVAL);
			} catch (InterruptedException | IOException e) {
				return;
			}
		}
	}
}
