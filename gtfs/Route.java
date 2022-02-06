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
import java.util.List;

/**
 * Class representing a GTFS Route
 * @author keanej
 */
public class Route implements GTFSObject {
	/**
	 * The column headers of a route
	 */
	public final static String[] COLUMNS = new String[]
			{
					"route_id",
					"agency_id",
					"route_short_name",
					"route_long_name",
					"route_desc",
					"route_type",
					"route_url",
					"route_color",
					"route_text_color"
			};

	/**
	 * The column titles of a route (for display purposes)
	 */
	public final static String[] COLUMN_TITLES = new String[]
			{
					"Route ID",
					"Agency ID",
					"Route Short Name",
					"Route Long Name",
					"Route Description",
					"Route Type",
					"Route URL",
					"Route Color",
					"Route Text Color"
			};

	private String agencyID;
	private String routeColor;
	private String routeDesc;
	private final String routeID;
	private String routeLongName;
	private String routeShortName;
	private String routeTxtColor;
	private int routeType;
	private String routeUrl;
	private List<Integer> stopTimeIDs;

	public Route(String agencyID,
				 String routeColor,
				 String routeDesc,
				 String routeID,
				 String routeLongName,
				 String routeShortName,
				 String routeTxtColor,
				 int routeType,
				 String routeUrl) {
		this.agencyID = agencyID;
		this.routeColor = routeColor;
		this.routeDesc = routeDesc;
		this.routeID = routeID;
		this.routeLongName = routeLongName;
		this.routeShortName = routeShortName;
		this.routeTxtColor = routeTxtColor;
		this.routeType = routeType;
		this.routeUrl = routeUrl;
		stopTimeIDs = new ArrayList<>();
	}

	/**
	 * Takes in a header line of the following order:
	 *  [route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color]
	 *  and initializes the route
	 *
	 * @param routesFileColumnValues String array with all the values from the route file column
	 */
	public Route(String[] routesFileColumnValues) {
		this(	routesFileColumnValues[1],
				routesFileColumnValues[7],
				routesFileColumnValues[4],
				routesFileColumnValues[0],
				routesFileColumnValues[3],
				routesFileColumnValues[2],
				routesFileColumnValues[8],
				Integer.parseInt(routesFileColumnValues[5]),
				routesFileColumnValues[6]);
	}

	/**
	 * Gets the column values of the Route
	 * @return The column values of the Route
	 */
	@Override
	public String[] getColumnValues() {
		return new String[]
				{
						routeID,
						agencyID,
						routeShortName,
						routeLongName,
						routeDesc,
						Integer.toString(routeType),
						routeUrl,
						routeColor,
						routeTxtColor
				};
	}

	public String getAgencyID() {
		return agencyID;
	}

	public void setAgencyID(String agencyID) {
		this.agencyID = agencyID;
	}

	public String getRouteColor() {
		return routeColor;
	}

	public void setRouteColor(String routeColor) {
		this.routeColor = routeColor;
	}

	public String getRouteDesc() {
		return routeDesc;
	}

	public void setRouteDesc(String routeDesc) {
		this.routeDesc = routeDesc;
	}

	public String getRouteID() {
		return routeID;
	}

	public String getRouteLongName() {
		return routeLongName;
	}

	public void setRouteLongName(String routeLongName) {
		this.routeLongName = routeLongName;
	}

	public String getRouteShortName() {
		return routeShortName;
	}

	public void setRouteShortName(String routeShortName) {
		this.routeShortName = routeShortName;
	}

	public String getRouteTxtColor() {
		return routeTxtColor;
	}

	public void setRouteTxtColor(String routeTxtColor) {
		this.routeTxtColor = routeTxtColor;
	}

	public String getRouteUrl() {
		return routeUrl;
	}

	public void setRouteUrl(String routeUrl) {
		this.routeUrl = routeUrl;
	}

	public int getRouteType() {
		return routeType;
	}

	public void setRouteType(int routeType) {
		 this.routeType = routeType;
	}

	public List<Integer> getStopTimeIDs() { return this.stopTimeIDs; }

	public void addStopTimeToRoute(int stopTimeID) {
		this.stopTimeIDs.add(stopTimeID);
	}
}