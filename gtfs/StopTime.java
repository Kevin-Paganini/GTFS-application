/*
 * This file is part of gtfstransitproject_group5.
 *
 * gtfstransitproject_group5 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gtfstransitproject_group5 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with gtfstransitproject_group5.  If not, see <https://www.gnu.org/licenses/>.

 * Course: CS 2030 031
 * Fall 2021
 * GTFS Transit Project
 * Names: Jonathan Keane, Kevin Paganini, Kyle Senebouttarath
 * Created: 10/4/2021
 * Professor: Dr. Wright
 */
package gtfs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * Class representing a GTFS Stop Time
 * @author keanej
 */
public class StopTime implements GTFSObject {
	/**
	 * The column headers of a stop time
	 */
	public final static String[] COLUMNS = new String[]
			{
					"trip_id",
					"arrival_time",
					"departure_time",
					"stop_id",
					"stop_sequence",
					"stop_headsign",
					"pickup_type",
					"drop_off_type"
			};

	/**
	 * The column titles of a stop time (for display purposes)
	 */
	public final static String[] COLUMN_TITLES = new String[]
			{
					"Trip ID",
					"Arrival Time",
					"Departure Time",
					"Stop ID",
					"Stop Sequence",
					"Stop Headsign",
					"Pickup Type",
					"Drop Off Type"
			};

	private Date arrival;
	private Date departure;
	private Optional<Integer> dropOffType;
	private Optional<Integer> pickupType;
	private String stopHeadSign;
	private String stopID;
	private int stopSequence;
	private final int stopTimeID;
	private String tripID;

	public static final DateFormat STOP_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static int nextStopTimeID = 0;

	public StopTime(Date arrival,
					Date departure,
					Optional<Integer> dropOffType,
					Optional<Integer> pickupType,
					String stopHeadSign,
					String stopID,
					int stopSequence,
					String tripID) {
		this.arrival = arrival;
		this.departure = departure;
		this.dropOffType = dropOffType;
		this.pickupType = pickupType;
		this.stopHeadSign = stopHeadSign;
		this.stopID = stopID;
		this.stopSequence = stopSequence;
		this.stopTimeID = nextStopTimeID++;
		this.tripID = tripID;
	}

	public StopTime(Date arrival,
					Date departure,
					int dropOffType,
					int pickupType,
					String stopHeadSign,
					String stopID,
					int stopSequence,
					String tripID) {
		this(
				arrival,
				departure,
				Optional.of(dropOffType),
				Optional.of(pickupType),
				stopHeadSign,
				stopID,
				stopSequence,
				tripID);
	}

	/**
	 * Takes in a header line of the following order:
	 *  [trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type]
	 *  and initializes the stop time
	 *
	 * @param stopTimesFileColumnValues String array with all the values from the stop time file column
	 */
	public StopTime(String[] stopTimesFileColumnValues) throws ParseException {
		this(	STOP_TIME_FORMAT.parse(stopTimesFileColumnValues[1]),
				STOP_TIME_FORMAT.parse(stopTimesFileColumnValues[2]),
				stopTimesFileColumnValues[7].isEmpty() ?
						Optional.empty() :
						Optional.of(Integer.parseInt(stopTimesFileColumnValues[7])),
				stopTimesFileColumnValues[6].isEmpty() ?
						Optional.empty() :
						Optional.of(Integer.parseInt(stopTimesFileColumnValues[6])),
				stopTimesFileColumnValues[5],
				stopTimesFileColumnValues[3],
				Integer.parseInt(stopTimesFileColumnValues[4]),
				stopTimesFileColumnValues[0]);
	}

	/**
	 * Gets the column values of the Stop Time
	 * @return The column values of the Stop Time
	 */
	@Override
	public String[] getColumnValues() {
		return new String[] {
				tripID,
				STOP_TIME_FORMAT.format(arrival),
				STOP_TIME_FORMAT.format(departure),
				stopID,
				Integer.toString(stopSequence),
				stopHeadSign,
				pickupType.map(integer -> "" + integer).orElse(""),
				dropOffType.map(integer -> "" + integer).orElse("")
		};
	}

	public Date getArrival() {
		return arrival;
	}

	public void setArrival(Date arrival) {
		this.arrival = arrival;
	}

	public void setArrival(String arrivalString) throws ParseException {
		setArrival(STOP_TIME_FORMAT.parse(arrivalString));
	}

	public Date getDeparture() {
		return departure;
	}

	public void setDeparture(Date departure) {
		this.departure = departure;
	}

	public void setDeparture(String departureString) throws ParseException {
		setDeparture(STOP_TIME_FORMAT.parse(departureString));
	}

	public Optional<Integer> getDropOffType() {
		return dropOffType;
	}

	public void setDropOffType(int dropOffType) {
		this.dropOffType = Optional.of(dropOffType);
	}

	public Optional<Integer> getPickupType() {
		return pickupType;
	}

	public void setPickupType(int pickupType) {
		this.pickupType = Optional.of(pickupType);
	}

	public String getStopHeadSign() {
		return stopHeadSign;
	}

	public void setStopHeadSign(String stopHeadSign) {
		this.stopHeadSign = stopHeadSign;
	}

	public String getStopID() {
		return stopID;
	}

	public void setStopID(String stopID) {
		this.stopID = stopID;
	}

	public int getStopSequence() {
		return stopSequence;
	}

	public void setStopSequence(int stopSequence) {
		this.stopSequence = stopSequence;
	}

	public int getStopTimeID() {
		return stopTimeID;
	}

	public String getTripID() {
		return tripID;
	}

	public void setTripID(String tripID) {
		this.tripID = tripID;
	}

	public static int getNextStopTimeID() {
		return nextStopTimeID;
	}

	public static void setNextStopTimeID(int nextStopTimeID) {
		StopTime.nextStopTimeID = nextStopTimeID;
	}

	public static void resetStopTimeIDCounter() {
		StopTime.nextStopTimeID = 0;
	}
}