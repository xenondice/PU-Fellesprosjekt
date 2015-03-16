package server_client;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Argument {
	public final boolean optional;
	public final String description;
	public final ArgumentType type;
	
	public static enum ArgumentType {
		number("normal integer number"),
		long_number("long integer number"),
		text("any text"),
		logic("yes/y or no/n"),
		date("DD/MM/YYYY or DD/MM/YYYY HH:MM (24h) or now"),
		password("any text"),
		command("any valid command"),
		argument_type("any type of argument, like \"number\"");
		
		private String help;
		
		private ArgumentType(String help) {
			this.help = help;
		}
		
		public String getHelp() {
			return help;
		}
		
		public Object getValue(String text) {
			if (text == null)
				return null;
			
			switch (this) {
				case number:
					try {
						return Integer.parseInt(text);
					} catch (NumberFormatException e) {
						return null;
					}
				case command:
					return Command.get(text);
				case date:
					if (text.equals("now")) return System.currentTimeMillis();
					
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat format_time = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					
					try {
						return format.parse(text).getTime();
					} catch (ParseException e1) {
					}
					
					try {
						return format_time.parse(text).getTime();
					} catch (ParseException e1) {
					}
					
					return null;
				case logic:
					if (text.equals("yes") || text.equals("y"))
						return true;
					else if (text.equals("no") || text.equals("n"))
						return false;
					else
						return null;
				case long_number:
					try {
						return Long.parseLong(text);
					} catch (NumberFormatException e) {
						return false;
					}
				case password:
					return text;
				case text:
					return text;
				case argument_type:
					return ArgumentType.valueOf(text);
				default:
					return null;
			}
		}
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
