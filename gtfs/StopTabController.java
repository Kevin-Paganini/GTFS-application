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

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static gtfs.StringValidation.isValidCoordinate;

/**
 * The controller for a singular StopTab
 *
 * @author keanej
 */
public class StopTabController {

    private Stop stop;
    private Controller controller;

    @FXML
    private Label title;
    @FXML
    private GridPane propertiesGrid;
    @FXML
    private GridPane nextTripsGrid;
    @FXML
    private GridPane routesGrid;

    private final Database GTFS = Database.getInstance();
    public static final DateFormat STOP_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Displays the information of the stop in the StopTab
     */
    public void displayStopInfo() {
        if (stop != null) {
            title.setText("Stop: " + stop.getStopID());
            populatePropertiesGrid();
            if (controller != null) {
                populateNextTripsGrid();
                populateRoutesGrid();
            }
        }
    }

    /**
     * Populates the stop properties
     */
    private void populatePropertiesGrid() {
        int propertiesIndex = 0;
        String[] values = stop.getColumnValues();
        for (String property : Stop.COLUMN_TITLES) {
            propertiesGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                            property, propertiesIndex),
                    0,
                    propertiesIndex);


            Label valueLabel = new Label(values[propertiesIndex]);

            if (!property.equals("Stop ID")) {
                Button editButton = new Button("Edit");
                editButton.setId("editButton");
                propertiesGrid.add(editButton, 2, propertiesIndex);

                editButton.setOnAction(actionEvent -> {
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.setTitle("Edit " + property);
                    dialog.setHeaderText("Enter a new value for " + property + ":");
                    dialog.setContentText("Value:");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(val -> {
                        switch (property) {
                            case "Stop Name":
                                stop.setStopName(val);
                                valueLabel.setText(val);
                                break;
                            case "Stop Description":
                                stop.setStopDesc(val);
                                valueLabel.setText(val);
                                break;
                            case "Stop Latitude":
                            case "Stop Longitude":
                                boolean isLat = property.equals("Stop Latitude");
                                try {
                                    double coordinate = Double.parseDouble(val);
                                    boolean valid = isValidCoordinate(val, isLat);
                                    if (valid) {
                                        if (isLat) {
                                            stop.setLatitude(coordinate);
                                        } else {
                                            stop.setLongitude(coordinate);
                                        }
                                        valueLabel.setText(val);
                                    } else {
                                        throw new NumberFormatException(
                                                "Number \"" + val + "\" out of bounds for attribute "
                                                        + (isLat ? "Latitude" : "Longitude") + ".");
                                    }

                                } catch (NumberFormatException numberFormatException) {
                                    Alert a = new Alert(Alert.AlertType.ERROR);
                                    a.setHeaderText("Input Error");
                                    a.setContentText("Inputted coordinate value is not valid!\n\n" + numberFormatException.getMessage());
                                    a.showAndWait();
                                }
                                break;
                        }
                    });
                });     //end editButton action event
                Pane gridCellLabel = GUIDefaultElements.getGridCellLeftRightNodes(valueLabel, editButton, propertiesIndex);
                propertiesGrid.add(gridCellLabel, 1, propertiesIndex);
            }   //end stop id if statement
            else {
                Pane gridCellLabel = GUIDefaultElements.getGridCellNode(valueLabel, propertiesIndex);
                propertiesGrid.add(gridCellLabel, 1, propertiesIndex);
            }


            ++propertiesIndex;
        }


        propertiesGrid.add(
                GUIDefaultElements.getGridCellLabel(
                        "Number of Trips",
                        propertiesIndex),
                0,
                propertiesIndex);
        propertiesGrid.add(
                GUIDefaultElements.getGridCellLabel(
                        "" + controller.getTripsOnStop(stop.getStopID()).size(),
                        propertiesIndex),
                1,
                propertiesIndex);

    }   //end method populatePropertiesGrid


    /**
     * Populates the next trips grid
     */
    private void populateNextTripsGrid() {
        int nextTripIndex = 0;
        List<Pair<Trip, String>> nearestTrips = controller.searchForTripsWithStopID(stop.getStopID());
        if (nearestTrips.isEmpty()) {
            nextTripsGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                            "No More Trips Today",
                            nextTripIndex),
                    0,
                    nextTripIndex);
            nextTripsGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                            " ",
                            nextTripIndex),
                    1,
                    nextTripIndex);
        } else {
            for (Pair<Trip, String> p : nearestTrips) {
                int finalNextTripIndex = nextTripIndex;
                nextTripsGrid.add(GUIDefaultElements.getGridCellNode(new ActionButton(
                        p.first.getTripID(),
                        event -> {
                            try {
                                openPrompt(p, finalNextTripIndex);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }), nextTripIndex), 0, nextTripIndex);
                nextTripsGrid.add(
                        GUIDefaultElements.getGridCellLabel(
                                p.second,
                                nextTripIndex),
                        1,
                        nextTripIndex);
                ++nextTripIndex;
            }
        }
    }   //end method populate trips grid

    /**
     * Populates the routes grid
     */
    private void populateRoutesGrid() {
        List<Route> routesWithStop = controller.searchForRoutesWithStopID(stop.getStopID());
        int routeIndex = 0;
        if (routesWithStop.isEmpty()) {
            routesGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                            "No Routes With This Stop",
                            routeIndex),
                    0,
                    routeIndex);
        } else {
            for (Route route : controller.searchForRoutesWithStopID(stop.getStopID())) {
                routesGrid.add(
                        GUIDefaultElements.getGridCellLabel(
                                route.getRouteID(),
                                routeIndex),
                        0,
                        routeIndex);
                ++routeIndex;
            }
        }
    }   //end method populate routes grid

    /**
     * Opens a prompt for updating the stop times for a stop
     *
     * @param p The stop time
     * @param nextTripIndex The table index of one of the next few trips
     */
    private void openPrompt(Pair<Trip, String> p, int nextTripIndex) throws ParseException {
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Update Stop Time");
        dialog.setHeaderText("Update Arrival Time");
        dialog.setContentText("Please enter your new arrival time (HH:mm:ss):");

        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){
            String arrivalString = result.get();

                Collection<StopTime> stopTimes= GTFS.getStopTimes();
                for(StopTime stopTime: stopTimes){
                    if(stopTime.getTripID().equals(p.first.getTripID())){
                        if (StringValidation.isValidTime(arrivalString)) {
                            Date arrival = STOP_TIME_FORMAT.parse(arrivalString);
                            stopTime.setArrival(arrival);
                            nextTripsGrid.add(GUIDefaultElements.getGridCellLabel(arrivalString, nextTripIndex), 1, nextTripIndex);
                        } else {
                            Alert a = new Alert(Alert.AlertType.ERROR);
                            a.setContentText("Please enter a valid time.");
                            a.setTitle("Incorrect time format. Please try again.");
                            a.showAndWait();
                        }
                        break;
                    }
                }
        }

        dialog.setTitle("Update Stop Time");
        dialog.setHeaderText("Update Departure Time");
        dialog.setContentText("Please enter your new departure time (HH:mm:ss):");

        result = dialog.showAndWait();
        if(result.isPresent()){
            String departureString = result.get();

            Collection<StopTime> stopTimes= GTFS.getStopTimes();
            for(StopTime stopTime: stopTimes){
                if(stopTime.getTripID().equals(p.first.getTripID())){
                    if (StringValidation.isValidTime(departureString)) {
                        Date departure = STOP_TIME_FORMAT.parse(departureString);
                        stopTime.setDeparture(departure);
                    } else {
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setContentText("Please enter a valid time.");
                        a.setTitle("Incorrect time format. Please try again.");
                        a.showAndWait();
                    }
                    break;
                }
            }
        }

    }
}   //end class StopTabController
