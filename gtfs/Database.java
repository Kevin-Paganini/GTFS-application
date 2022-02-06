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

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Database class to act as our subject for the project
 * This is a Singleton class
 * @author senebk & keanej
 */
public class Database implements Subject {

	//---------------- SINGLETON ----------------\\

	private static Database databaseInstance = null;

	//---------------- ATTRIBUTES ----------------\\

	private Collection<Observer> observers;

	private Trie<Stop> stops;
	private Trie<Route> routes;
	private Trie<Trip> trips;
	private ArrayList<StopTime> stopTimes;

	//---------------- CONSTRUCTOR METHODS ----------------\\

	private Database() {
		observers = new ArrayList<>();

		routes = new Trie<>();
		stops = new Trie<>();
		trips = new Trie<>();
		stopTimes = new ArrayList<>();
	}

	/**
	 * Singleton method to get the one and only Database object.
	 * Use .getInstance() to get this from any class.
	 *
	 * @return
	 */
	public static Database getInstance() {
		if (databaseInstance == null) {
			databaseInstance = new Database();
		}
		return databaseInstance;
	}

	//---------------- METHODS ----------------\\

	//---- GETTERS

	public Trie<Stop> getStops() {
		return stops;
	}

	public Trie<Route> getRoutes() {
		return routes;
	}

	public Trie<Trip> getTrips() {
		return trips;
	}

	public ArrayList<StopTime> getStopTimes() {
		return stopTimes;
	}

	/**
	 * Use a mask to get a filtered collection of routes.
	 * @param mask The function to compare routes against
	 */
	public List<Route> getRoutes(Predicate<Route> mask) {
		List<Route> results = new ArrayList<>();
		for (Route route: routes) {
			if (mask.test(route)) {
				results.add(route);
			}
		}
		return results;
	}

	/**
	 * Use a mask to get a filtered collection of stops.
	 * @param mask The function to compare stops against
	 */
	public List<Stop> getStops(Predicate<Stop> mask) {
		List<Stop> results = new ArrayList<>();
		for (Stop stop: stops) {
			if (mask.test(stop)) {
				results.add(stop);
			}
		}
		return results;
	}

	/**
	 * Use a mask to get a filtered collection of stop times.
	 * @param mask The function to compare stop times against
	 */
	public List<StopTime> getStopTimes(Predicate<StopTime> mask) {
		return stopTimes.stream().filter(mask).collect(Collectors.toList());
	}

	/**
	 * Use a query to get a filtered collection of trips.
	 * @param mask The function to compare trips against
	 */
	public List<Trip> getTrips(Predicate<Trip> mask) {
		List<Trip> results = new ArrayList<>();
		for (Trip trip: trips) {
			if (mask.test(trip)) {
				results.add(trip);
			}
		}
		return results;
	}

	//---- UPDATES AND SETTERS

	/**
	 * Initialize the database object with GTFS data collections.
	 *
	 * @param stops Stop object collection.
	 * @param routes Route object collection.
	 * @param trips Trip object collection.
	 * @param stopTimes StopTime object collection.
	 * @return If the initialization was successfully completed.
	 */
	public boolean initDatabase(Collection<Stop> stops, Collection<Route> routes, Collection<Trip> trips, ArrayList<StopTime> stopTimes) {
		initStops(stops);
		initRoutes(routes);
		initTrips(trips);
		initStopTimes(stopTimes);
		StopTime.resetStopTimeIDCounter();
		notifyObservers();
		return true;
	}

	/**
	 * Append more GTFS object collections onto co-existing collection data.
	 *
	 * @param stops Stop object collection.
	 * @param routes Route object collection.
	 * @param trips Trip object collection.
	 * @param stopTimes StopTime object collection.
	 * @return If the adding was successfully completed.
	 */
	public boolean appendDatabase(Collection<Stop> stops, Collection<Route> routes, Collection<Trip> trips, Collection<StopTime> stopTimes) {
		appendStops(stops);
		appendRoutes(routes);
		appendTrips(trips);
		appendStopTimes(stopTimes);

		notifyObservers();

		return true;
	}

	/**
	 * Initializes collection of stops.
	 *
	 * @param stops Stop object collection.
	 * @return If the initialization was successful.
	 */
	public boolean initStops(Collection<Stop> stops) {
		this.stops = new Trie<>();
		for (Stop stop: stops) {
			this.stops.addKey(stop.getStopID(), stop);
		}
		notifyObservers();
		return true;
	}

	/**
	 * Initializes collection of routes.
	 *
	 * @param routes Route object collection.
	 * @return If the initialization was successful.
	 */
	public boolean initRoutes(Collection<Route> routes) {
		this.routes = new Trie<>();
		for (Route route: routes) {
			this.routes.addKey(route.getRouteID(), route);
		}
		notifyObservers();
		return true;
	}

	/**
	 * Initializes collection of trips.
	 *
	 * @param trips Trip object collection.
	 * @return If the initialization was successful.
	 */
	public boolean initTrips(Collection<Trip> trips) {
		this.trips = new Trie<>();
		for (Trip trip: trips) {
			this.trips.addKey(trip.getTripID(), trip);
		}
		notifyObservers();
		return true;
	}

	/**
	 * Initializes collection of stop times.
	 *
	 * @param stopTimes StopTime object collection.
	 * @return If the initialization was successful.
	 */
	public boolean initStopTimes(ArrayList<StopTime> stopTimes) {
		this.stopTimes = stopTimes;
		notifyObservers();
		return true;
	}

	public void distributeStopTimes() {
		for (int i = 0; i < stopTimes.size(); ++i) {
			String tripID = stopTimes.get(i).getTripID();
			Trip trip = trips.getValue(tripID);
			if (trip != null) {
				trip.addStopTimeToTrip(i);
				String routeID = trip.getRouteID();
				Route route = routes.getValue(routeID);
				if (route != null) {
					route.addStopTimeToRoute(i);
				}
			}
		}
	}


	/**
	 * Appends collection of stops to existing collection.
	 *
	 * @param stops Stop object collection.
	 * @return If the adding was successful.
	 */
	public boolean appendStops(Collection<Stop> stops) {
		for (Stop stop: stops) {
			this.stops.addKey(stop.getStopID(), stop);
		}
		notifyObservers();
		return true;
	}

	/**
	 * Appends collection of routes to existing collection.
	 *
	 * @param routes Route object collection.
	 * @return If the adding was successful.
	 */
	public boolean appendRoutes(Collection<Route> routes) {
		for (Route route: routes) {
			this.routes.addKey(route.getRouteID(), route);
		}
		notifyObservers();
		return true;
	}

	/**
	 * Appends collection of trips to existing collection.
	 *
	 * @param trips Trip object collection.
	 * @return If the adding was successful.
	 */
	public boolean appendTrips(Collection<Trip> trips) {
		for (Trip trip: trips) {
			this.trips.addKey(trip.getTripID(), trip);
		}
		notifyObservers();
		return true;
	}

	/**
	 * Appends collection of stop times to existing collection.
	 *
	 * @param stopTimes StopTime object collection.
	 * @return If the adding was successful.
	 */
	public boolean appendStopTimes(Collection<StopTime> stopTimes) {
		this.stopTimes.addAll(stopTimes);
		notifyObservers();
		return true;
	}

	/**
	 * Updates the route collection with a given update query.
	 *
	 * @param query The update query.
	 * @return If the query was successful.
	 */
	public boolean updateRoutes(String query) {
		//Do update stuff

		notifyObservers();
		return false;
	}

	/**
	 * Updates the stop collection with a given update query.
	 *
	 * @param query The update query.
	 * @return If the query was successful.
	 */
	public boolean updateStops(String query) {
		//Do update stuff

		notifyObservers();
		return false;
	}

	/**
	 * Updates the stop time collection with a given update query.
	 *
	 * @param query The update query.
	 * @return If the query was successful.
	 */
	public boolean updateStopTimes(String query) {
		//Do update stuff

		notifyObservers();
		return false;
	}

	/**
	 * Updates the trips collection with a given update query.
	 *
	 * @param query The update query.
	 * @return If the query was successful.
	 */
	public boolean updateTrips(String query) {
		//Do update stuff

		notifyObservers();
		return false;
	}

	//---- OBSERVER SUBJECT

	/**
	 * Adds an observer from the subject.
	 * @param o Observer object
	 */
	public void attach(Observer o) {
		observers.add(o);
	}

	/**
	 * Removes an observer from the subject.
	 * @param o Observer object
	 */
	public void detach(Observer o) {
		observers.remove(o);
	}

	/**
	 * Calls the update method for every observer connected.
	 */
	public void notifyObservers() {
		observers.forEach(Observer::update);
	}

}	//end class Database