package server_client.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

import room_booking.RoomReservation;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import exceptions.ForcedReturnException;
import exceptions.SessionExpiredException;

public class ShowAllReservations extends Command{

	@Override
	public String get() {
		return "show-room-reservations";
	}

	@Override
	public String getDescription() {
		return "Shows all reservations for a given room.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
				{
					new Argument(false, "the room Id ", ArgumentType.text)
				}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[] {"'"+this.get()+"' R-205"};
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments,
			int sytax) throws IOException, TimeoutException,
			InterruptedException, ForcedReturnException,
			SessionExpiredException {
		
		String roomID = (String)arguments.get(0);
		HashSet<RoomReservation> res = RequestHandler.getAllReservationsForRoom(roomID);
		if(res == null || res.isEmpty()){
			return "There are no reservations for this room ("+roomID+").";
		}
		return res.toString();
	}

}
