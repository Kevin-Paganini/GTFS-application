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

import java.util.ArrayList;
import java.lang.Math;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Class representing a GTFS Trip
 * @author keanej
 */
public class Trip implements GTFSObject {
	/**
	 * The column headers of a trip
	 */
	public final static String[] COLUMNS = new String[]
			{
					"route_id",
					"service_id",
					"trip_id",
					"trip_headsign",
					"direction_id",
					"block_id",
					"shape_id"
			};

	/**
	 * The column titles of a trip (for display purposes)
	 */
	public final static String[] COLUMN_TITLES = new String[]
			{
					"Route ID",
					"Service ID",
					"Trip ID",
					"Trip Headsign",
					"Direction ID",
					"Block ID",
					"Shape ID"
			};

	private String blockID;
	private Optional<Integer> directionID;
	private String routeID;
	private String serviceID;
	private String shapeID;
	private ArrayList<Integer> stopTimeIDs;
	private String tripHeadSign;
	private final String tripID;

	Database GTFS = Database.getInstance();

	public Trip(String blockID,
				Optional<Integer> directionID,
				String routeID,
				String serviceID,
				String shapeID,
				String tripHeadSign,
				String tripID) {
		this.blockID = blockID;
		this.directionID = directionID;
		this.routeID = routeID;
		this.serviceID = serviceID;
		this.shapeID = shapeID;
		this.tripHeadSign = tripHeadSign;
		this.tripID = tripID;
		this.stopTimeIDs = new ArrayList<>();
	}

	public Trip(String blockID,
				int directionID,
				String routeID,
				String serviceID,
				String shapeID,
				String tripHeadSign,
				String tripID) {
		this(	blockID,
				Optional.of(directionID),
				routeID,
				serviceID,
				shapeID,
				tripHeadSign,
				tripID);
	}

	/**
	 * Takes in a header line of the following order:
	 *  [route_id,service_id,trip_id,trip_headsign,direction_id,block_id,shape_id]
	 *  and initializes the trip
	 *
	 * @param tripsFileColumnValues String array with all the values from the trip file column
	 */
	public Trip(String[] tripsFileColumnValues) {
		this(	tripsFileColumnValues[5],
				tripsFileColumnValues[4].isEmpty() ?
						Optional.empty() :
						Optional.of(Integer.parseInt(tripsFileColumnValues[4])),
				tripsFileColumnValues[0],
				tripsFileColumnValues[1],
				tripsFileColumnValues[6],
				tripsFileColumnValues[3],
				tripsFileColumnValues[2]);
	}

	/**
	 * Updates the trip's stop time list by checking through the database.
	 * This should be done everytime stop times or trips are updated.
	 *
	 * @author Kyle S
	 */
	private void updateStopTimeList() {
		stopTimeIDs = new ArrayList<>();
		for (StopTime stopTime : GTFS.getStopTimes()) {
			if (stopTime.getTripID().equals(this.tripID)) {
				stopTimeIDs.add(stopTime.getStopTimeID());
			}
		}
	}

	/**
	 * Helper method to get a stop time object via its stop
	 * time ID.
	 *
	 * @param stopTimeId The stop time id
	 * @return The respective stop time object
	 * @author Kyle S
	 */
	private StopTime getStopTimeByStopTimeId(int stopTimeId) {
		for (StopTime stopTime : GTFS.getStopTimes()) {
			if (stopTime.getStopTimeID() == stopTimeId) {
				return stopTime;
			}
		}
		return null;
	}

	/**
	 * Helper method to get a stop object via its stop ID.
	 *
	 * @param stopId The stop's id
	 * @return The respective stop object
	 * @author Kyle S
	 */
	private Stop getStopByStopId(String stopId) {
		for (Stop stop : GTFS.getStops()) {
			if (stop.getStopID().equals(stopId)) {
				return stop;
			}
		}
		return null;
	}

	/**
	 * Gets the stop from a stop time parameter, even if the parameter
	 * is null.
	 *
	 * @param stopTime The stop time object (can be null)
	 * @return The stop or null
	 * @author Kyle S
	 */
	private Stop getStopFromStopTime(StopTime stopTime) {
		if (stopTime == null) {
			return null;
		}
		return getStopByStopId(stopTime.getStopID());
	}

	/**
	 * Getting trip distance based on Haversine formula.
	 * https://en.wikipedia.org/wiki/Haversine_formula
	 *
	 * This function will loop through all the stop times
	 * and get the distance between each stop time, then return
	 * the total distance. The very first and last stop times are
	 * connected.
	 *
	 * @return The total distance for the given trip, in miles.
	 * 			The value will be -1.0 if it couldn't be calculated.
	 *
	 * @author Kyle S, Kevin P
	 */
	public double getTripDistance() {

		double returnDist = -1.0;

		updateStopTimeList();

		double totalDist = 0;

		for (int i = 0; i < getStopTimeIDs().size(); i++) {
			int nextIndex = i + 1;

			if (nextIndex < getStopTimeIDs().size()) {
				Stop startStop = getStopFromStopTime(getStopTimeByStopTimeId(getStopTimeIDs().get(i)));
				Stop endStop = getStopFromStopTime(getStopTimeByStopTimeId(getStopTimeIDs().get(nextIndex)));

				if (startStop != null && endStop != null) {
					double lon1 = startStop.getLongitude();
					double lon2 = endStop.getLongitude();
					double lat1 = startStop.getLatitude();
					double lat2 = endStop.getLatitude();

					double theta = lon1 - lon2;
					double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
							Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta)
							);
					dist = Math.acos(dist);
					dist = Math.toDegrees(dist);
					dist = dist * 60 * 1.1515;

					totalDist += dist;
				}
			}
		}

		//System.out.println(getStopTimeIDs());

		if (totalDist > 0) {
			returnDist = totalDist;
		}

		return returnDist;
	}

	/**
	 * Gets column values of the Trip
	 * @return The column values of the Trip
	 */
	@Override
	public String[] getColumnValues() {
		return new String[]
				{
					routeID,
					serviceID,
					tripID,
					tripHeadSign,
					directionID.map(integer -> "" + integer).orElse(""),
					blockID,
					shapeID
			};
	}

	public String getBlockID() {
		return blockID;
	}

	public void setBlockID(String blockID) {
		this.blockID = blockID;
	}

	public Optional<Integer> getDirectionID() {
		return directionID;
	}

	public void setDirectionID(int directionID) {
		this.directionID = Optional.of(directionID);
	}

	public String getRouteID() {
		return routeID;
	}

	public void setRouteID(String routeID) {
		this.routeID = routeID;
	}

	public String getServiceID() {
		return serviceID;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public String getShapeID() {
		return shapeID;
	}

	public void setShapeID(String shapeID) {
		this.shapeID = shapeID;
	}

	public String getTripHeadSign() {
		return tripHeadSign;
	}

	public void setTripHeadSign(String tripHeadSign) {
		this.tripHeadSign = tripHeadSign;
	}

	public String getTripID() {
		return tripID;
	}

	/**
	 * Adds a stop time to a trip based on its unique stop time ID
	 * @param stopTimeId The stop time's stop time ID
	 */
	public void addStopTimeToTrip(int stopTimeId) {
		getStopTimeIDs().add(stopTimeId);
	}

	public ArrayList<Integer> getStopTimeIDs() {
		return stopTimeIDs;
	}

	public List<String> getStopIDs() {
		return getStopTimeIDs().stream().map(stopTime ->
				Database.getInstance().getStopTimes().get(stopTime).getStopID()).collect(Collectors.toList());
	}

	/**
	 *
	 * @return trip speed
	 * @author Kevin
	 */
	public double getAvgTripSpeed() {
		double cookie = 0;
		updateStopTimeList();
		ArrayList<Integer> stopTimeIDs = getStopTimeIDs();
		if (stopTimeIDs.size() < 2) {
			cookie = -1;
			return cookie;
		}
		StopTime startStopTime = this.getStopTimeByStopTimeId(stopTimeIDs.get(0));
		StopTime endStopTime = this.getStopTimeByStopTimeId(stopTimeIDs.get(stopTimeIDs.size() - 1));
		long startDate = startStopTime.getArrival().getTime();
		long endDate = endStopTime.getArrival().getTime();
		long timeDifference = endDate - startDate; // in ms
		double timeDifInMin = TimeUnit.MILLISECONDS.toMinutes(timeDifference);
		double timeDifInHours = timeDifInMin / 60.0;
		double avgSpeed = this.getTripDistance() / timeDifInHours;
		cookie = avgSpeed;
		return cookie;
	}
}