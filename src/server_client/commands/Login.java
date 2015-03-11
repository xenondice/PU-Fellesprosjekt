package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.UserDoesNotExistException;
import exceptions.WrongPasswordException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import user.User;

public class Login extends Command {

	@Override
	public String getCommand() {
		return "login";
	}

	@Override
	public String getDescription() {
		return "Login with an existing user.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public String[] getArguments() {
		return new String[]{
			"username",
			"password"
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		try {
			User user = RequestHandler.logIn(arguments.get(0), arguments.get(1));
			if (user != null) {
				handler.setUser(user);
				return "Successfully logged in!";
			}
		} catch (UserDoesNotExistException | WrongPasswordException e) {
		}
		return "Invalid password and/or username!";
	}
}
