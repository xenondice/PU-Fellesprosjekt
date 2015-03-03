package server_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerClientHandler implements Runnable {
	private BufferedReader client_input;
	private BufferedWriter client_output;
	private RequestHandler request_handler;
	
	public ServerClientHandler(Socket user, RequestHandler request_handler) throws IOException {
		client_output = new BufferedWriter(new OutputStreamWriter(user.getOutputStream()));
		client_input = new BufferedReader(new InputStreamReader(user.getInputStream()));
		this.request_handler = request_handler;
	}
	
	private String handleReqest(String reqest) {
		return null; //TODO
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				if (client_input.ready()) {
					String request = "";
					while (client_input.ready())
						request += client_input.readLine();
					client_output.write(handleReqest(request));
					client_output.flush();
				}
				
				Thread.sleep(Client.SERVER_LISTENER_CHECK_INTERVAL); //TODO
			} catch (InterruptedException | IOException e) {
				return;
			}
		}
	}
}