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

/**
 * Class representing a GTFS Stop
 * @author keanej
 */
public class Stop implements GTFSObject {
	/**
	 * The column headers of a stop
	 */
	public static final String[] COLUMNS = new String[]
			{
				"stop_id",
				"stop_name",
				"stop_desc",
				"stop_lat",
				"stop_lon"
			};

	/**
	 * The column titles of a stop (for display purposes)
	 */
	public static final String[] COLUMN_TITLES = new String[]
			{
					"Stop ID",
					"Stop Name",
					"Stop Description",
					"Stop Latitude",
					"Stop Longitude"
			};

	private double latitude;
	private double longitude;
	private String stopDesc;
	private final String stopID;
	private String stopName;

	public Stop(double latitude,
				double longitude,
				String stopDesc,
				String stopID,
				String stopName){
		this.latitude = latitude;
		this.longitude = longitude;
		this.stopDesc = stopDesc;
		this.stopID = stopID;
		this.stopName = stopName;
	}

	/**
	 * Takes in a header line of the following order:
	 *  [stop_id,stop_name,stop_desc,stop_lat,stop_lon]
	 *  and initializes the stop
	 *
	 * @param stopsFileColumnValues String array with all the values from the stop file column
	 */
	public Stop(String[] stopsFileColumnValues) {
		this(	Double.parseDouble(stopsFileColumnValues[3]),
				Double.parseDouble(stopsFileColumnValues[4]),
				stopsFileColumnValues[2],
				stopsFileColumnValues[0],
				stopsFileColumnValues[1]);
	}

	public static String[] getColumns() {
		return COLUMNS;
	}

	/**
	 * Get the Stop's column values
	 * @return the Stops' column values
	 */
	@Override
	public String[] getColumnValues() {
		return new String[] { stopID, stopName, stopDesc, Double.toString(latitude), Double.toString(longitude) } ;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getStopDesc() {
		return stopDesc;
	}

	public void setStopDesc(String stopDesc) {
		this.stopDesc = stopDesc;
	}

	public String getStopID() {
		return stopID;
	}

	public String getStopName() {
		return stopName;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public String toString() {
		StringBuilder returnString = new StringBuilder();
		for (String val : getColumnValues()) {
			returnString.append(val);
			returnString.append(",");
		}
		returnString.delete(returnString.length() - 1, returnString.length());
		return returnString.toString();
	}
}