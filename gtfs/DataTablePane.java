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

import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A pane for displaying a data table
 * This will be an observer of our Database
 * @author keanej, senebk
 */
public class DataTablePane implements Observer {

	private static final int STOP_SEARCH_COLUMN = 0;
	private static final int ROUTE_SEARCH_COLUMN = 1;
	private static final int TRIP_SEARCH_COLUMN = 2;

	private Database GTFS;

	private GridPane searchResults;
	private TextField searchBar;
	private TabPane guiTabs;
	private Controller controller;

	public DataTablePane(TabPane guiTabs, TextField searchBar, GridPane searchResults, Controller controller) {
		this.guiTabs = guiTabs;
		this.searchBar = searchBar;
		this.searchResults = searchResults;
		this.controller = controller;

		GTFS = Database.getInstance();
	}

	public void update() {
		searchResults.getChildren().clear();

		int stopInd = 0;
		ArrayList<Stop> nearestStops = GTFS.getStops().nearestObjects(searchBar.getText(), 5); //The Trie class continues to impress
		if (nearestStops != null) {
			for (Stop stop : nearestStops) {
				if (stop != null) {
					searchResults.add(
							new ActionButton(
									stop.getStopID(),
									event -> addStopTab(stop)),
							STOP_SEARCH_COLUMN,
							stopInd++);
				}
			}
		}

		int routeInd = 0;
		ArrayList<Route> nearestRoutes = GTFS.getRoutes().nearestObjects(searchBar.getText(), 5);
		if (nearestRoutes != null) {
			for (Route route : nearestRoutes) {
				if (route != null) {
					searchResults.add(
							new ActionButton(
									route.getRouteID(),
									event -> addRouteTab(route)),
							ROUTE_SEARCH_COLUMN,
							routeInd++);
				}
			}
		}

		int tripInd = 0;
		ArrayList<Trip> nearestTrips = GTFS.getTrips().nearestObjects(searchBar.getText(), 5);
		if (nearestTrips != null) {
			for (Trip trip : nearestTrips) {
				if (trip != null) {
					searchResults.add(new ActionButton(
									trip.getTripID(),
									event -> addTripTab(trip)),
							TRIP_SEARCH_COLUMN,
							tripInd++);
				}
			}
		}


	}

	/**
	 * Creates and adds a StopTab based on the given Stop
	 * @param stop The stop
	 */
	void addStopTab(Stop stop) {
		try {
			guiTabs.getTabs().add(new StopTab(stop, controller));
		} catch (IOException exception) {
			System.out.println("Could not read FXML");
		}
	}

	private void addRouteTab(Route route) {
		try {
			guiTabs.getTabs().add(new RouteTab(route, controller));
		} catch (IOException exception) {
			System.out.println("Could not read FXML");
		}
	}

	private void addTripTab(Trip trip) {
		try {
			guiTabs.getTabs().add(new TripTab(trip, controller));
		} catch (IOException exception) {
			System.out.println("Could not read FXML");
		}
	}

}	//end class DataTablePane