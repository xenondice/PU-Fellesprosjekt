package server_client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;

/**
 * All the commands that a client can use once connected and verified. The classes that extend this interface needs to be
 * added to the ServerClientHandler to become usable by the client.
 */
public interface Command {
	
	/**
	 * Get the one-word command.
	 */
	public String getCommand();
	
	/**
	 * Get a short, one-line description of the command.
	 */
	public String getDescription();
	
	/**
	 * Get a longer description of the command. Doesn't include examples.
	 */
	public String getManual();
	
	/**
	 * Get an descriptive array of the arguments required by the command.
	 */
	public String[] getArguments();
	
	/**
	 * Get an array with examples of how the command might be used.
	 */
	public String[] getExamples();
	
	/**
	 * Run the command. Returns true if the command was run successful and false otherwise.
	 */
	public abstract void run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException;
}