package server_client;

public class Argument {
	public final boolean optional;
	public final String description;
	public final ArgumentType type;
	
	public static enum ArgumentType {
		number,
		long_number,
		text,
		logic,
		date,
		password,
		command,
	}
	
	public Argument(boolean optional, String description, ArgumentType type) {
		this.optional = optional;
		this.description = description;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "(" + description + ")[" + type.name().replace('_', ' ') + "]";
	}
}
