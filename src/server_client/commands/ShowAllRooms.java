package server_client.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

import room_booking.Room;
import server_client.Argument;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.GroupAlreadyExistsException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.InvitationDoesNotExistException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import exceptions.UsernameAlreadyExistsException;

public class ShowAllRooms extends Command {

	@Override
	public String get() {
		return "show-rooms";
	}

	@Override
	public String getDescription() {
		return "shows all existing rooms.";
	}

	@Override
	public String getManual() {
		return "To use this command just enter '"+this.get()+"' and a list of all existing rooms will be shown";
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[0][0];
	}

	@Override
	public String[] getExamples() {
		return new String[] {"There is only one example: '"+this.get()+"'"};
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments,
			int sytax) throws IOException, TimeoutException,
			InterruptedException, ForcedReturnException,
			SessionExpiredException {
		
		StringBuilder sb = new StringBuilder();
		HashSet<Room> rooms = RequestHandler.getAllRooms();
		sb.append("All existing Rooms: \n");
		for(Room r: rooms){
			sb.append("-> ");
			sb.append(r.toString());
			sb.append("\n");
		}
		sb.append("\n");
		return sb.toString();
	}

}
