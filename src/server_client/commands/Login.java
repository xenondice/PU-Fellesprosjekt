package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.UserDoesNotExistException;
import exceptions.WrongPasswordException;
import server_client.Argument;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.Argument.ArgumentType;
import user.User;

public class Login extends Command {

	@Override
	public String get() {
		return "login";
	}

	@Override
	public String getDescription() {
		return "Login with an existing user.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Login with an existing user.\n"
				+ "Preferably use wiz login as this will hide your password when you type it in!";
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "username", ArgumentType.text),
				new Argument(false, "password", ArgumentType.password),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		try {
			User user = RequestHandler.logIn((String) arguments.get(0), (String) arguments.get(1));
			if (user != null) {
				handler.setUser(user);
				return "Successfully logged in!";
			}
		} catch (UserDoesNotExistException | WrongPasswordException e) {
		}
		return "Invalid password and/or username!";
	}
}
