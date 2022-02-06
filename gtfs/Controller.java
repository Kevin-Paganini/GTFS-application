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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import java.io.FileWriter;

/**
 * The Controller of our application's interactions
 *
 * @author keanej and paganinik and senebouttarathk
 */
public class Controller implements Initializable {
	private static final int SEARCH_RESULTS_LIMIT = 10;


	/**
	 * The different GTFS Object types
	 */
	enum GTFS_TYPE {
		STOP,
		ROUTE,
		TRIP,
		STOP_TIME,
		NONE
	}

	@FXML
	private Tab searchTab;
	@FXML
	private Button exportFilesButton;
	@FXML
	private Button importFilesButton;
	@FXML
	private TextField stopIDSearch;
	@FXML
	private Label tripsWithStopDisplay;
	@FXML
	private ComboBox<GTFS_TYPE> gtfsTypeSelect;
	@FXML
	private GridPane dataDisplay;
	@FXML
	private Label textKev;
	@FXML
	private TextField searchBar;
	@FXML
	private GridPane searchResults;
	@FXML
	private TabPane guiTabs;
	@FXML
	private GridPane headersDisplay;

	private DataTablePane searchArea;

	private final Database GTFS;

	private final static int MAX_ROWS = 50;

	private final ArrayList<Stop> stops = new ArrayList<>();
	private final ArrayList<Trip> trips = new ArrayList<>();
	private final ArrayList<Route> routes = new ArrayList<>();
	private ArrayList<StopTime> stopTimes = new ArrayList<>();

	/**
	 * Constructs the controller and gives it a reference to the Database Singleton
	 */
	public Controller() {
		GTFS = Database.getInstance();

	}

	/**
	 * Actions to complete on initialization of the FXML references
	 * @param location The location [UNUSED]
	 * @param resources The resources [UNUSED]
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		for (GTFS_TYPE type: GTFS_TYPE.values()) {
			gtfsTypeSelect.getItems().add(type);
		}
		searchArea = new DataTablePane(guiTabs, searchBar, searchResults, this);
		GTFS.attach(searchArea);
		exportFilesButton.setDisable(true);
		searchTab.setDisable(true);
	}

	/**
	 * Updates search results whenever a key is typed in the search bar
	 * @param e The MouseReleasedEvent
	 */
	@FXML
	private void updateSearchResults(KeyEvent e) {
		searchArea.update();
	}



	/**
	 * Export files, the gui stuff to make it work very nice
	 * opens four file choosers for the user to save each file individually
	 *
	 * @param actionEvent The button press
	 * @author Kevin, Kyle
	 */
	public void exportFilesAction(ActionEvent actionEvent) {

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Export GTFS Files Location");
//		chooser.setInitialDirectory(new File("./.export"));			//TODO: Change to downloads folder
		/* This code can be used to change to downloads folder:
		String home = System.getProperty("user.home");
		File downloadsFolder = new File(home + "/Downloads/");
		*/
		File selectedDirectory = chooser.showDialog(null);

		try {
			//Create stops file
			createExportFile(selectedDirectory,
					"stops.txt",
					"stop_id,stop_name,stop_desc,stop_lat,stop_lon",
					GTFS.getStops());

			//Create routes file
			createExportFile(selectedDirectory,
					"routes.txt",
					"route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color",
					GTFS.getRoutes());

			//Create trips file
			createExportFile(selectedDirectory,
					"trips.txt",
					"route_id,service_id,trip_id,trip_headsign,direction_id,block_id,shape_id",
					GTFS.getTrips());

			//Create stop times file
			createExportFile(selectedDirectory,
					"stop_times.txt",
					"trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type",
					GTFS.getStopTimes());

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Files Exported");
			alert.setContentText("Files have been successfully exported to " + selectedDirectory);
			alert.showAndWait();

		} catch (IOException ioException) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error Exporting GTFS Files!");
			alert.setContentText("There was an error exporting GTFS files!\n\nError: " +
					ioException.getMessage());
			alert.showAndWait();
		} catch (NullPointerException nullPointerException) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error Exporting GTFS Files!");
			alert.setContentText("No directory was specified, so there was an error exporting GTFS files!\n\nError: " +
					nullPointerException.getMessage());
			alert.showAndWait();
		}

	}

	/**
	 * Display the number of trips associated with the Stop ID/Stop
	 * specified in the stopIDSearch text field
	 *
	 * @param keyEvent When a key is released
	 */
	@FXML
	private void displayTripsWithStopID(KeyEvent keyEvent) {
		Stop searchedStop = GTFS.getStops().getValue(stopIDSearch.getText());
		if (searchedStop != null) {
			Collection<String> tripsWithStopID = getTripsOnStop(searchedStop.getStopID());
			tripsWithStopDisplay.setText("" + tripsWithStopID.size() + " Trips on stop.");
		} else {
			if (stopIDSearch.getText().isEmpty()) {
				tripsWithStopDisplay.setText("");
			} else {
				tripsWithStopDisplay.setText("Invalid Stop ID");
			}
		}
	}

	/**
	 * Imports files when the button is pressed
	 * @param e The button press event
	 * @throws FileNotFoundException if a file is invalid
	 * @throws ParseException if parsing a data can't be done
	 */
	@FXML
	public void importFilesAction(ActionEvent e) throws FileNotFoundException, ParseException {
		stops.clear();
		routes.clear();
		trips.clear();
		stopTimes.clear();


		// this was Kevin's code even though I (Jonny) touched it
		FileChooser fc = new FileChooser();
		fc.setTitle("Open stops.txt");
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt files", "stops.txt"));
		File selectedFile = fc.showOpenDialog(null);
		importFile(selectedFile.getAbsolutePath(), GTFS_TYPE.STOP);

		fc = new FileChooser();
		fc.setTitle("Open routes.txt");
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt files", "routes.txt"));
		selectedFile = fc.showOpenDialog(null);
		importFile(selectedFile.getAbsolutePath(), GTFS_TYPE.ROUTE);

		fc = new FileChooser();
		fc.setTitle("Open trips.txt");
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt files", "trips.txt"));
		selectedFile = fc.showOpenDialog(null);
		importFile(selectedFile.getAbsolutePath(), GTFS_TYPE.TRIP);

		fc = new FileChooser();
		fc.setTitle("Open stop_times.txt");
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt files", "stop_times.txt"));
		selectedFile = fc.showOpenDialog(null);
		importFile(selectedFile.getAbsolutePath(), GTFS_TYPE.STOP_TIME);

		GTFS.initDatabase(stops, routes, trips, stopTimes);
		GTFS.distributeStopTimes();
	}

	private boolean receivedAllFiles(List<String> files) {
		for (String filename: new String[]{"stop_times.txt", "routes.txt", "stops.txt", "trips.txt"}) {
			if (!files.contains(filename)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Imports files when the button is pressed
	 * @param e The button press event
	 * @throws FileNotFoundException if a file is invalid
	 * @throws ParseException if parsing a data can't be done
	 */
	@FXML
	public void importFilesAction2(ActionEvent e) throws FileNotFoundException, ParseException {
		stops.clear();
		routes.clear();
		trips.clear();
		// don't need to clear stopTime since always going to be a new ArrayList

		// this was Kevin's code even though I (Jonny) touched it
		FileChooser fc = new FileChooser();
		fc.setTitle("Open files");

		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt files",
				"routes.txt",
				"stop_times.txt",
				"trips.txt",
				"stops.txt"));
		List<File> selectedFiles = fc.showOpenMultipleDialog(null);

		if (selectedFiles != null) {
			List<String> filenames = selectedFiles.stream().map(File::getName).collect(Collectors.toList());
			if ((selectedFiles.size() == 4) && receivedAllFiles(filenames)) {
				for (File file : selectedFiles) {
					importFile(file.getAbsolutePath(), fileType(file));
				}
				GTFS.initDatabase(stops, routes, trips, stopTimes);
				// this makes sure that we don't accidentally delete data
				// in the database that holds the same reference
				stopTimes = new ArrayList<>();
				GTFS.distributeStopTimes();
				searchTab.setDisable(false);
				exportFilesButton.setDisable(false);
			} else {
				displayAlert(0, GTFS_TYPE.NONE);
			}
		} else {
			displayAlert(0, GTFS_TYPE.NONE);
		}
	}

	/**
	 * Imports a file and loads GTFS objects into temporary storage in temporary storage in Controller
	 * @param filename The path of the file
	 * @param type The GTFS object type
	 * @return True as long as the end of the method is reached
	 * @throws FileNotFoundException if the file could not be found
	 * @throws ParseException if an arrival/departure time could not be parsed
	 */
	private boolean importFile(String filename, GTFS_TYPE type) throws FileNotFoundException, ParseException {
		Scanner in;
		File file = new File(filename);
		in = new Scanner(file);
		String headers = in.nextLine(); // to validate headers
		boolean headersCorrect = validationLogicHeaders(type, headers); // validating headers
		if (headersCorrect) {
			while (in.hasNextLine()) {
				switch (type) {
					case STOP:
					{
						//create and add the object
						Stop stop = validateStopValues(in.nextLine());
						if (stop != null) {
							stops.add(stop);
						}
						break;
					}
					case ROUTE:
					{
						Route route = validateRouteValues(in.nextLine());
						if (route != null) {
							routes.add(route);
						}
						break;
					}
					case TRIP:
					{
						Trip trip = validateTripValues(in.nextLine());
						if (trip != null) {
							trips.add(trip);
						}
						break;
					}
					case STOP_TIME:
					{
						StopTime stopTime = validateStopTimeValues(in.nextLine());
						if (stopTime != null) {
							stopTimes.add(stopTime);
						}
						break;
					}

				}
			}

		} else {
			displayAlert(5, type);

		}
		// this started off as Kevin's too, I (Jonny) just reword a little to integrate with Database from Kyle
		in.close();
		return true;
	}

	/**
	 * Helper method in case to determine which GTFS file is specified
	 * @param file A file
	 * @return The GTFS file's type
	 * @throws IllegalArgumentException if it is not a GTFS file
	 */
	private GTFS_TYPE fileType(File file) throws IllegalArgumentException {
		GTFS_TYPE answer;
		if (file.getAbsolutePath().contains("stops.txt")){
			answer = GTFS_TYPE.STOP;
		}
		else if (file.getAbsolutePath().contains("stop_times.txt")){
			answer = GTFS_TYPE.STOP_TIME;
		}
		else if (file.getAbsolutePath().contains("routes.txt")){
			answer = GTFS_TYPE.ROUTE;
		}
		else if (file.getAbsolutePath().contains("trips.txt")){
			answer = GTFS_TYPE.TRIP;
		} else {
			Alert a = new Alert(Alert.AlertType.NONE);
			a.setContentText("The file name you chose is wrong.");
			a.show();
			throw new IllegalArgumentException("You chose the wrong file.");
		}
		return answer;
	}

	/**
	 * Returns the COLUMNS constant from a GTFSObject. If no such String[] constant
	 * is found, null is returned instead. Error messages will be outputted
	 * to the console.
	 *
	 * @param gtfsClass The GTFSObject class to look into.
	 * @return null or a found String[] COLUMNS constant
	 * @author Kyle S.
	 */
	private String[] getGTFSClassHeaderColumns(Class<? extends GTFSObject> gtfsClass) {
		String[] gtfsCols = null;
		try {
			gtfsCols = (String[]) gtfsClass.getDeclaredField("COLUMNS").get(null);
		} catch (NoSuchFieldException | IllegalAccessException noFieldException) {
			System.out.println(noFieldException.getMessage());
		}
		return gtfsCols;
	}

	/**
	 * Updates the DataGridView with a different GTFS Object type's data points
	 * @param actionEvent The event of changing the selected value in the GTFS object type combo box
	 *
	 * @author keanj, senek
	 */
	@FXML
	private void updateGTFSTypeDisplay(ActionEvent actionEvent) {
		dataDisplay.getChildren().clear();
		dataDisplay.getColumnConstraints().clear();
		headersDisplay.getChildren().clear();
		headersDisplay.getColumnConstraints().clear();

		String[] colHeaders = null;
		Iterator<? extends GTFSObject> iterator = null;
		int size = 0;

		switch(gtfsTypeSelect.getValue()) {
			case STOP: {
				colHeaders = getGTFSClassHeaderColumns(Stop.class);
				iterator = GTFS.getStops().iterator();
				size = GTFS.getStops().size();
				break;
			}
			case ROUTE: {
				colHeaders = getGTFSClassHeaderColumns(Route.class);
				iterator = GTFS.getRoutes().iterator();
				size = GTFS.getRoutes().size();
				break;
			}
			case TRIP: {
				colHeaders = getGTFSClassHeaderColumns(Trip.class);
				iterator = GTFS.getTrips().iterator();
				size = GTFS.getTrips().size();
				break;
			}
			case STOP_TIME: {
				colHeaders = getGTFSClassHeaderColumns(StopTime.class);
				iterator = GTFS.getStopTimes().iterator();
				size = GTFS.getStopTimes().size();
				break;
			}
		}

		if (colHeaders != null) {
			int headerCol = 0;
			for (String tableColumn: colHeaders) {
				ColumnConstraints columnConstraints = new ColumnConstraints();
				columnConstraints.setPercentWidth(100);
				dataDisplay.getColumnConstraints().add(columnConstraints);
				headersDisplay.getColumnConstraints().add(columnConstraints);
				Label headerLabel = new Label(tableColumn);
				headerLabel.getStyleClass().add("table-header");
//				headerLabel.setId("headerLabel");
				headersDisplay.add(headerLabel, headerCol++, 0);
			}
		}

		if (iterator != null && size > 0) {
			int row = 0;
			while (row <= MAX_ROWS && iterator.hasNext()) {
				int col = 0;
				for (String value : iterator.next().getColumnValues()) {
					Label dataLabel = new Label(value);
//					dataLabel.setId("dataLabel");
					dataDisplay.add(GUIDefaultElements.getGridCellNode(dataLabel, row), col++, row);
				}
				++row;
			}

			if (iterator.hasNext()) {
				dataDisplay.add(
						GUIDefaultElements.getGridCellNode(
								new Label(Integer.toString(size - MAX_ROWS) + " more elements ..."),
								row),
						0, row);
				for (int i = 1; i < Objects.requireNonNull(colHeaders).length; ++i) {
					dataDisplay.add(
							GUIDefaultElements.getGridCellNode(
									new Label(" "),
									row),
							i, row);
				}
			}
		}
	}	//end method updateGTFSTypeDisplay


	private boolean validationLogicHeaders(GTFS_TYPE type, String headers){
		boolean cookie = false;
		if(type == GTFS_TYPE.ROUTE)
		{
			cookie = validateRouteHeaders(headers);
		}
		else if(type == GTFS_TYPE.STOP)
		{
			cookie = validateStopHeaders(headers);
		}
		else if (type == GTFS_TYPE.TRIP)
		{
			cookie = validateTripHeaders(headers);
		}
		else if (type == GTFS_TYPE.STOP_TIME)
		{
			cookie = validateStopTimeHeaders(headers);
		}
		else if (type == GTFS_TYPE.NONE)
		{
			cookie = false;
		}
		return cookie;
	}
	/**
	 * Validates the headers of stops file
	 * @param stopHeaders The string of stop headers
	 * @return true if headers are valid
	 */
	public boolean validateStopHeaders(String stopHeaders) {
		boolean cookie = false;
		if (stopHeaders != null) {
			String[] gtfsObjectValues = stopHeaders.split(",");
			if (gtfsObjectValues.length == 5) {
				String stopID = gtfsObjectValues[0].trim().toLowerCase(Locale.ROOT);
				String stopName = gtfsObjectValues[1].trim().toLowerCase(Locale.ROOT);
				String stopDesc = gtfsObjectValues[2].trim().toLowerCase(Locale.ROOT);
				String stopLat = gtfsObjectValues[3].trim().toLowerCase(Locale.ROOT);
				String stopLon = gtfsObjectValues[4].trim().toLowerCase(Locale.ROOT);
				if (stopID.equals("stop_id")
						&& stopName.equals("stop_name")
						&& stopDesc.equals("stop_desc")
						&& stopLat.equals("stop_lat")
						&& stopLon.equals("stop_lon")) {
					cookie = true;
				}
			}
		}
		return cookie;
	}

	/**
	 * Validates the headers of route file
	 * @param routeHeaders The string of route headers
	 * @return true if headers are valid
	 */
	public boolean validateRouteHeaders(String routeHeaders) {
		boolean cookie = false;
		if (routeHeaders != null) {
			String[] gtfsObjectValues = routeHeaders.split(",");
			if (gtfsObjectValues.length == 9) {
				String routeID = gtfsObjectValues[0].trim().toLowerCase(Locale.ROOT);
				String agencyID = gtfsObjectValues[1].trim().toLowerCase(Locale.ROOT);
				String routeShortName = gtfsObjectValues[2].trim().toLowerCase(Locale.ROOT);
				String routeLongName = gtfsObjectValues[3].trim().toLowerCase(Locale.ROOT);
				String routeDesc = gtfsObjectValues[4].trim().toLowerCase(Locale.ROOT);
				String routeType = gtfsObjectValues[5].trim().toLowerCase(Locale.ROOT);
				String routeURL = gtfsObjectValues[6].trim().toLowerCase(Locale.ROOT);
				String routeColor = gtfsObjectValues[7].trim().toLowerCase(Locale.ROOT);
				String routeTextColor = gtfsObjectValues[8].trim().toLowerCase(Locale.ROOT);
				if (routeID.equals("route_id")
						&& agencyID.equals("agency_id")
						&& routeShortName.equals("route_short_name")
						&& routeLongName.equals("route_long_name")
						&& routeDesc.equals("route_desc")
						&& routeType.equals("route_type")
						&& routeURL.equals("route_url")
						&& routeColor.equals("route_color")
						&& routeTextColor.equals("route_text_color")) {
					cookie = true;
				}
			}
		}
		return cookie;
	}

	/**
	 * Validates the headers of trips file
	 * @param tripHeaders The string of trip headers
	 * @return true if headers are valid
	 */
	public boolean validateTripHeaders(String tripHeaders) {
		boolean cookie = false;
		if (tripHeaders != null) {
			String[] gtfsObjectValues = tripHeaders.split(",");
			if (gtfsObjectValues.length == 7) {
				String routeID = gtfsObjectValues[0].trim().toLowerCase(Locale.ROOT);
				String serviceID = gtfsObjectValues[1].trim().toLowerCase(Locale.ROOT);
				String tripID = gtfsObjectValues[2].trim().toLowerCase(Locale.ROOT);
				String tripHeadSign = gtfsObjectValues[3].trim().toLowerCase(Locale.ROOT);
				String directionID = gtfsObjectValues[4].trim().toLowerCase(Locale.ROOT);
				String blockID = gtfsObjectValues[5].trim().toLowerCase(Locale.ROOT);
				String shapeID = gtfsObjectValues[6].trim().toLowerCase(Locale.ROOT);
				if (routeID.equals("route_id")
						&& serviceID.equals("service_id")
						&& tripID.equals("trip_id")
						&& tripHeadSign.equals("trip_headsign")
						&& directionID.equals("direction_id")
						&& blockID.equals("block_id")
						&& shapeID.equals("shape_id")) {
					cookie = true;
				}
			}
		}
		return cookie;
	}

	/**
	 * Validates the headers of stop_times file
	 * @param stopTimeHeaders The string of stop time headers
	 * @return true if headers are valid
	 */
	public boolean validateStopTimeHeaders(String stopTimeHeaders) {
		boolean cookie = false;
		if (stopTimeHeaders != null) {
			String[] gtfsObjectValues = stopTimeHeaders.split(",");
			if (gtfsObjectValues.length == 8) {
				String tripID = gtfsObjectValues[0].trim().toLowerCase(Locale.ROOT);
				String arrivalTime = gtfsObjectValues[1].trim().toLowerCase(Locale.ROOT);
				String departureTime = gtfsObjectValues[2].trim().toLowerCase(Locale.ROOT);
				String stopID = gtfsObjectValues[3].trim().toLowerCase(Locale.ROOT);
				String stopSequence = gtfsObjectValues[4].trim().toLowerCase(Locale.ROOT);
				String stopHeadSign = gtfsObjectValues[5].trim().toLowerCase(Locale.ROOT);
				String pickupType = gtfsObjectValues[6].trim().toLowerCase(Locale.ROOT);
				String dropOffType = gtfsObjectValues[7].trim().toLowerCase(Locale.ROOT);
				if (tripID.equals("trip_id")
						&& arrivalTime.equals("arrival_time")
						&& departureTime.equals("departure_time")
						&& stopID.equals("stop_id")
						&& stopSequence.equals("stop_sequence")
						&& stopHeadSign.equals("stop_headsign")
						&& pickupType.equals("pickup_type")
						&& dropOffType.equals("drop_off_type")) {
					cookie = true;
				}
			}
		}
		return cookie;
	}

	/**
	 *
	 * @param alertID tells what type of error
	 * @param type tells file type of which error happened in - you can pass in null if you want
	 */
	private void displayAlert(int alertID, GTFS_TYPE type){
		try {
			Alert a = new Alert(Alert.AlertType.ERROR);
			switch (alertID) {
				case 0:
					a.setContentText("Four GTFS files were not selected." +
							(searchTab.isDisabled() ? "" : " Previously loaded data has been retained.") +
							" Please try again.");
					break;
				case 1:
					a.setContentText("Values in stops.txt are formatted incorrectly.");
					break;
				case 2:
					a.setContentText("Values in routes.txt are formatted incorrectly.");
					break;
				case 3:
					a.setContentText("Values in trips.txt are formatted incorrectly.");
				case 4:
					a.setContentText("Values in stop_times.txt are formatted incorrectly.");
					break;
				case 5:
					switch (type) {
						case STOP:
							a.setContentText("Values in stops.txt are formatted incorrectly.");
							break;
						case ROUTE:
							a.setContentText("Headers in routes.txt are written incorrectly.");
							break;
						case TRIP:
							a.setContentText("Headers in trips.txt are written incorrectly.");
							break;
						case STOP_TIME:
							a.setContentText("Headers in stop_times.txt are written incorrectly.");
							break;
					}
					break;
			}
			a.showAndWait();
		} catch(ExceptionInInitializerError | NoClassDefFoundError exception) {
			System.out.println("Initialize Error Caught (If Running Tests, Ignore Because GUI Related):\n\t" + exception.getMessage());
		}
	}
	/**
	 * Validates a stop's values based on a String describing the Stop
	 * @param stopValues The string of the stop values
	 * @return The Stop object if valid, null otherwise
	 */
	public Stop validateStopValues(String stopValues) {
		// lat long are required
		if (stopValues == null) return null;
		String[] stopValuesArr = stopValues.split(",");
		if (stopValuesArr.length != 5) return null;
		for (int i = 0; i < stopValuesArr.length; ++i) {
			stopValuesArr[i] = stopValuesArr[i].trim();
		}
		if (	!stopValuesArr[0].isEmpty() &&
				!stopValuesArr[1].isEmpty() &&
				StringValidation.isValidCoordinate(stopValuesArr[3], true) &&
				StringValidation.isValidCoordinate(stopValuesArr[4], false)) {
			return new Stop(stopValuesArr);
		} else {
			displayAlert(1, null);
			return null;
		}
	}

	/**
	 * Validates a route's values based on a String describing the Route
	 * @param routeValues The route values
	 * @return The Route object if valid, null otherwise
	 */
	public Route validateRouteValues(String routeValues) {
		// only require a route ID and route_color
		if (routeValues == null) return null;
		String[] routeValuesArr = routeValues.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		if (routeValuesArr.length == 8) {
			routeValuesArr = Arrays.copyOf(routeValuesArr, 9);
		}
		if (routeValuesArr.length != 9) return null;
		for (int i = 0; i < routeValuesArr.length; ++i) {
			if (routeValuesArr[i] == null) {
				routeValuesArr[i] = "";
			} else {
				routeValuesArr[i] = routeValuesArr[i].trim();
			}
		}
		if (	!routeValuesArr[0].isEmpty() &&
				(routeValuesArr[5].isEmpty() || StringValidation.isValidInt(routeValuesArr[5])) &&
				StringValidation.isValidColor(routeValuesArr[7]) &&
				(routeValuesArr[8].isEmpty() || StringValidation.isValidColor(routeValuesArr[8]))) {
			return new Route(routeValuesArr);
		} else {
			displayAlert(2, null);
			return null;
		}
	}

	/**
	 * Validates a trip's values based on a String describing the Trip
	 * @param tripValues The trip values
	 * @return The Trip object if valid, null otherwise
	 */
	public Trip validateTripValues(String tripValues) {
		// do not require a service ID
		if (tripValues == null) return null;
		String[] tripValuesArr = tripValues.split(",");
		if (tripValuesArr.length != 7) return null;
		for (int i = 0; i < tripValuesArr.length; ++i) {
			tripValuesArr[i] = tripValuesArr[i].trim();
		}
		if (	!tripValuesArr[0].isEmpty() &&
				!tripValuesArr[2].isEmpty() &&
				(StringValidation.isValidInt(tripValuesArr[4])|| tripValuesArr[4].isEmpty()) &&

				!tripValuesArr[6].isEmpty()) {
			// tripValuesArr[3] did not use to check null it checked isEmpty() there were cases where it was
			//returning an empty string which should be ok as seen in the trips file
			// when Scrolling through the trips.txt file look for the first column to have number 7 thats where this case happens
			return new Trip(tripValuesArr);

		} else {
			displayAlert(3, null);
			return null;
		}
	}

	/**
	 * Validates a stop time's values based on a String describing the Stop Time
	 * @param stopTimeValues The stop time values
	 * @return The Stop Time object if valid, otherwise null
	 * @throws ParseException if parsing date causes exception (shouldn't)
	 */
	public StopTime validateStopTimeValues(String stopTimeValues) throws ParseException {
		// schedule is the same every day
		if (stopTimeValues == null) return null;
		stopTimeValues = stopTimeValues.replaceAll(",", ",~");
		String[] stopTimeValuesArr = stopTimeValues.split(",");
		if (stopTimeValuesArr.length != 8) return null;
		for (int i = 0; i < stopTimeValuesArr.length; ++i) {
			stopTimeValuesArr[i] = stopTimeValuesArr[i].trim();
			stopTimeValuesArr[i] = stopTimeValuesArr[i].replaceAll("~", "");
		}
		if (	!stopTimeValuesArr[0].isEmpty() &&
				StringValidation.isValidTime(stopTimeValuesArr[1]) &&
				StringValidation.isValidTime(stopTimeValuesArr[2]) &&
				!stopTimeValuesArr[3].isEmpty() &&
				StringValidation.isValidNonNegativeInt(stopTimeValuesArr[4]) &&
				(StringValidation.isValidInt(stopTimeValuesArr[6]) || stopTimeValuesArr[6].isEmpty()) &&
				(StringValidation.isValidInt(stopTimeValuesArr[7]) || stopTimeValuesArr[7].isEmpty())) {
			return new StopTime(stopTimeValuesArr);
		} else {
			displayAlert(4, null);
			return null;
		}
	}

	/**
	 * @param stopsFilePath
	 * @param routesFilePath
	 * @param tripsFilePath
	 * @param stopTimesFilePath
	 */
	public boolean appendFiles(String stopsFilePath, String routesFilePath, String tripsFilePath, String stopTimesFilePath){
		return false;
	}

	/**
	 * 
	 * @param meters
	 */
	private double convertToMiles(double meters){
		return 0;
	}

	/**
	 * 
	 * @param stopsFilePath
	 * @param routeFilesPath
	 * @param tripsFilePath
	 * @param stopTimesFilePath
	 */
	public boolean exportFiles(String stopsFilePath, String routeFilesPath, String tripsFilePath, String stopTimesFilePath){
		return false;
	}

	/**
	 * 
	 * @param tripID
	 */
	public double [] getBusLocation(String tripID){
		return null;
	}

	/**
	 * 
	 * @param tripID
	 */
	public double getTripAvgSpeed(String tripID){
		return 0;
	}

	/**
	 * Gets the distance of a trip based on the provided trip ID.
	 * The trip ID is defined from the import file.
	 *
	 * @param tripID The ID of the trip to get the distance for
	 * @return The total distance for the given trip
	 * 			The value will be -1.0 if no trip is found
	 * 			or if the trip distance couldn't be calculated.
	 *
	 * @author Kyle S.
	 */
	public double getTripDistance(String tripID) {
		if (tripID == null) {
			return -1.0;
		}

		Trip foundTrip = null;
		for (Trip trip : GTFS.getTrips()) {
			if (trip.getTripID().equals(tripID)) {
				foundTrip = trip;
			}
		}

		double returnVal = -1.0;

		if (foundTrip != null) {
			returnVal = foundTrip.getTripDistance();
		}

		return returnVal;
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts decimal degrees to radians (helper functions):*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts radians to decimal degrees (helper function):*/
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	/**
	 *

	/**
	 * gets the number of distinct trips a stopID appears in
	 * @param stopID
	 */
	public Collection <String> getTripsOnStop(String stopID){
		if(stopID == null) {
			return null;
		}
		Collection<StopTime> stopTimes = GTFS.getStopTimes();
		Iterator<StopTime> iterator = stopTimes.iterator();
		Collection<String> tripsOnStop = new ArrayList<>();
		// while loop
		while (iterator.hasNext()) {
			StopTime stopTime = iterator.next();
			if(stopID.equals(stopTime.getStopID())){
				tripsOnStop.add(stopTime.getTripID());
			}
		}
		return tripsOnStop.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 
	 * @param routeID
	 */
	private Route searchForRoute(String routeID){
		return null;
	}

	/**
	 * Feature 5: Search with a stop ID and get out the routes that hit this stop.
	 *
	 * @param stopID The STOP_ID to search by
	 */
	public List<Route> searchForRoutesWithStopID(String stopID){
		return GTFS.getStopTimes(
				stopTime -> stopTime.getStopID().equals(stopID)).stream().map(
				stopTime -> GTFS.getTrips().getValue(stopTime.getTripID())).map(
				trip -> GTFS.getRoutes().getValue(trip.getRouteID())
		).distinct().collect(Collectors.toList());
	}


	/**
	 * Feature 6: Search with a ROUTE_ID and get all stops that are on the route
	 *
	 * @param routeID The ROUTE_ID to search by
	 */
	public List<Stop> searchForStops(String routeID) {
		return GTFS.getRoutes().getValue(routeID).getStopTimeIDs().stream().
				map(stopTimeID -> GTFS.getStopTimes().get(stopTimeID)).
				map(stopTime -> GTFS.getStops().getValue(stopTime.getStopID())).
				distinct().collect(Collectors.toList());
	}

	/**
	 * WARNING: Make sure that the stop times are sorted in here
	 *
	 * Feature 7: Search with a ROUTE_ID and get out the next trips with this route.
	 *
	 * @param routeID The ROUTE_ID to search by
	 */
	public List<Pair<Trip, String>> searchForTripsWithRouteID(String routeID) {
		return GTFS.getTrips(
				trip -> trip.getRouteID().equals(routeID)).stream().
						filter(trip -> StopTime.STOP_TIME_FORMAT.format(GTFS.getStopTimes().get(trip.getStopTimeIDs().get(0)).getArrival()).
								compareTo(StopTime.STOP_TIME_FORMAT.format(new Date())) > 0).
								sorted(Comparator.comparing(a -> GTFS.getStopTimes().get(a.getStopTimeIDs().get(0)).getArrival())).
						limit(SEARCH_RESULTS_LIMIT).
						map(trip -> new Pair<>(
								trip, StopTime.STOP_TIME_FORMAT.format(
								GTFS.getStopTimes().get(trip.getStopTimeIDs().get(0)).getArrival()
								))).
						collect(Collectors.toList());
	}

	/**
	 * Feature 8: Search with a STOP_ID and get out the trips that are the soonest after containing this stop
	 *
	 * @param stopID The STOP_ID to search by
	 */
	public List<Pair<Trip, String>> searchForTripsWithStopID(String stopID) {
		return GTFS.getStopTimes(
				stopTime -> StopTime.STOP_TIME_FORMAT.format(stopTime.getArrival()).
						compareTo(StopTime.STOP_TIME_FORMAT.format(new Date())) > 0).stream().
				filter(stopTime -> stopTime.getStopID().equals(stopID)).
				sorted(Comparator.comparing(StopTime::getArrival)).limit(SEARCH_RESULTS_LIMIT).
				map(stopTime -> new Pair<>(
						GTFS.getTrips().getValue(stopTime.getTripID()),
						StopTime.STOP_TIME_FORMAT.format(stopTime.getArrival()
						))).
				collect(Collectors.toList());
	}

	/**
	 * 
	 * @param routeID
	 * @param routeProperties
	 */
	public boolean updateRoute(String routeID, HashMap<String, String> routeProperties){
		return false;
	}

	/**
	 * 
	 * @param stopID
	 * @param stopProperties
	 */
	public boolean updateStop(int stopID, HashMap<String, String> stopProperties){
		return false;
	}

	/**
	 * 
	 * @param stopTimeID
	 * @param stopTimeProperties
	 */
	public boolean updateStopTime(int stopTimeID, HashMap<String, String> stopTimeProperties){
		return false;
	}

	/**
	 * 
	 * @param tripID
	 * @param tripProperties
	 */
	public boolean updateTrip(String tripID, HashMap<String, String> tripProperties){
		return false;
	}

	//---- CREATING FILES FOR EXPORT

	/**
	 * This function is the primary function used to create an export file and write to it
	 * based on the provided GTFSObject collection.
	 *
	 * @param saveLoc The location/directory where to save the files
	 * @param fileName The name of the new export file
	 * @param headLine The header line/first line of the file
	 * @param gtfsObjCol The collection of GTFSObject objects to export
	 *
	 * @throws IOException There was a problem creating or writing the file
	 *
	 * @author Kyle Senebouttarath
	 */
	public void createExportFile(File saveLoc, String fileName, String headLine, Iterable<? extends GTFSObject> gtfsObjCol) throws IOException {
		File exportFile = new File(saveLoc, fileName);
		exportFile.createNewFile();
		FileWriter fileWriter = new FileWriter(exportFile);
		fileWriter.write(headLine);

		for (GTFSObject gtfsObj : gtfsObjCol) {
			String[] dataCols = gtfsObj.getColumnValues();
			StringBuilder outputLine = new StringBuilder();

			outputLine.append("\n");
			Arrays.stream(dataCols).forEach(str -> {
				outputLine.append(str);
				outputLine.append(",");
			});
			outputLine.deleteCharAt(outputLine.length() - 1);	//Removes last comma
			fileWriter.write(outputLine.toString());
			fileWriter.flush();
		}

		fileWriter.close();
	}

	/**
	 * This was so that I can make tests for getTripDistance
	 * @param e
	 */
	@FXML
	public void distanceTripStuffForKevin(ActionEvent e){
		Stop firstStop = GTFS.getStops().getValue("2398");
		Stop lastStop = GTFS.getStops().getValue("2745");
		double firstStopLat = firstStop.getLatitude();
		double firstStopLon = firstStop.getLongitude();
		double lastStopLat = lastStop.getLatitude();
		double lastStopLon = lastStop.getLongitude();
		String msg = "FirstStopLat: " + firstStopLat + "\n" +
				"FirstStopLon: " + firstStopLon + "\n" +
				"LastStopLat: " + lastStopLat + "\n" +
				"LastStopLon: " + lastStopLon;
		textKev.setText(msg);

	}

}