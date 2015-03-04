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
					while (server_input.ready()) {
						console_output.write(server_input.read());
						console_output.flush();
						client_thread.interrupt();
					}
					Client.markEnd();
				}
				
				Thread.sleep(Client.SERVER_LISTENER_CHECK_INTERVAL);
			} catch (InterruptedException | IOException e) {
				return;
			}
		}
	}
}
