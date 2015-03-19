package server_client.commands;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

import room_booking.Room;
import room_booking.RoomReservation;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import exceptions.ForcedReturnException;
import exceptions.SessionExpiredException;
import exceptions.StartTimeIsLaterTanEndTimeException;

public class ShowAvailableRooms extends Command{

	@Override
	public String get() {
		return "show-aviable-rooms";
	}

	@Override
	public String getDescription() {
		return "shows all aviable rooms in the given timespan";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
				{
					new Argument(false, "start Time", ArgumentType.date),
					new Argument(false, "end Time", ArgumentType.date),
					new Argument(true, "min size of the room", ArgumentType.number),
				}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[]{
				get()+"22/10/2015 22/11/2015 45",
				get()+"22/10/2015 22/11/2015",
		};
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments,
			int sytax) throws IOException, TimeoutException,
			InterruptedException, ForcedReturnException,
			SessionExpiredException{
		long stime = (long) arguments.get(0);
		long etime = (long) arguments.get(1);
		int minsize = arguments.get(2) == null ? -1: (int)arguments.get(2);
		try {
			ArrayList<Room> freerooms = RequestHandler.getAllFreeRooms(stime, etime, minsize);
			StringBuilder sb = new StringBuilder();
			sb.append("Free Rooms for the period "+new Timestamp(stime).toString()+" - "+new Timestamp(etime).toString()+":\n");
			for(Room r : freerooms){
				sb.append("-> ");
				sb.append(r.toString());
				sb.append("\n");
			}
			return sb.toString();
		} catch (StartTimeIsLaterTanEndTimeException e) {
			return "The startTime must be before the end time!";
		}
	}
}
