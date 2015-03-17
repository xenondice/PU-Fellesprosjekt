package server_client;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Makes Harry Potter appear!
 * 
 * ..
 * 
 *
 */
public class Wizard {
	private List<Argument> arguments;
	
	public Wizard() {
		arguments = new ArrayList<>();
	}
	
	public void add(Argument argument) {
		arguments.add(argument);
	}
	
	public int size() {
		return arguments.size();
	}
	
	public Argument get(int i) {
		return arguments.get(i);
	}
}
