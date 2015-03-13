package server_client;

import java.util.ArrayList;
import java.util.List;

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
