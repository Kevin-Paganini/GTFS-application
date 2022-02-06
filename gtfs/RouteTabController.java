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

import java.util.List;
import java.util.Optional;

public class RouteTabController {

    private Route route;
    private Controller controller;

    @FXML
    private Label title;
    @FXML
    private GridPane propertiesGrid;
    @FXML
    private GridPane nextTripsGrid;
    @FXML
    public GridPane stopsOnRouteGrid;

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Displays the information of the stop in the StopTab
     */
    public void displayRouteInfo() {
        if (route != null) {
            title.setText("Route: " + route.getRouteID());
            populatePropertiesGrid();
            populateNextTripsGrid();
            populateStopsOnRouteGrid();
        }
    }

    /**
     * This populates the properties area of the route
     */
    private void populatePropertiesGrid() {
        int propertiesIndex = 0;
        String[] values = route.getColumnValues();
        for (String property : Route.COLUMN_TITLES) {
            propertiesGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                            property,
                            propertiesIndex),
                    0,
                    propertiesIndex);

            Label valueLabel = new Label(values[propertiesIndex]);

            if (!property.equals("Route ID")) {
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
                            case "Agency ID":
                                route.setAgencyID(val);
                                valueLabel.setText(val);

                                break;
                            case "Route Short Name":
                                route.setRouteShortName(val);
                                valueLabel.setText(val);
                                break;
                            case "Route Long Name":
                                route.setRouteLongName(val);
                                valueLabel.setText(val);
                                break;
                            case "Route Description":
                                route.setRouteDesc(val);
                                valueLabel.setText(val);
                                break;
                            case "Route URL":
                                route.setRouteUrl(val);
                                valueLabel.setText(val);
                                break;
                            case "Route Color":
                            case "Route Text Color":
                                if (StringValidation.isValidColor(val)) {
                                    if (property.equals("Route Text Color"))
                                        route.setRouteTxtColor(val);
                                    else
                                        route.setRouteColor(val);

                                    valueLabel.setText(val);
                                } else {
                                    Alert a = new Alert(Alert.AlertType.ERROR);
                                    a.setHeaderText("Input Error");
                                    a.setContentText("Inputted color value for " + property + " is not valid!");
                                    a.showAndWait();
                                }
                                break;
                            case "Route Type":
                                try {
                                    if (StringValidation.isValidInt(val)) {
                                        route.setRouteType(Integer.parseInt(val));
                                        valueLabel.setText(val);
                                    } else {
                                        throw new NumberFormatException("Not a valid integer!");
                                    }
                                } catch (NumberFormatException numberFormatException) {
                                    Alert a = new Alert(Alert.AlertType.ERROR);
                                    a.setHeaderText("Input Error");
                                    a.setContentText("Inputted route type value is not valid!\n\n" + numberFormatException.getMessage());
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
    }   // end method populate properties

    /**
     * Populates the grid area for next trips
     */
    private void populateNextTripsGrid() {
        int nextTripIndex = 0;
        List<Pair<Trip, String>> nearestTrips = controller.searchForTripsWithRouteID(route.getRouteID());
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
                nextTripsGrid.add(
                        GUIDefaultElements.getGridCellLabel(
                                p.first.getTripID(),
                                nextTripIndex),
                        0,
                        nextTripIndex);
                nextTripsGrid.add(
                        GUIDefaultElements.getGridCellLabel(
                                p.second,
                                nextTripIndex),
                        1,
                        nextTripIndex);
                ++nextTripIndex;
            }
        }
    }   //end method populate trips

    /**
     * Populates the stops on the route grid
     */
    private void populateStopsOnRouteGrid() {
        int stopsOnRouteIndex = 0;
        List<Stop> stopsOnRoute = controller.searchForStops(route.getRouteID());
        if (stopsOnRoute.isEmpty()) {
            stopsOnRouteGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                            "No Stops Found for Route",
                            stopsOnRouteIndex),
                    0,
                    stopsOnRouteIndex);
            stopsOnRouteGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                            " ",
                            stopsOnRouteIndex),
                    1,
                    stopsOnRouteIndex);
        } else {
            for (Stop stop: stopsOnRoute) {
                if (stop != null) {
                    stopsOnRouteGrid.add(
                            GUIDefaultElements.getGridCellLabel(
                                    stop.getStopID(),
                                    stopsOnRouteIndex),
                            0,
                            stopsOnRouteIndex);
                    ++stopsOnRouteIndex;
                }
            }
        }
    }   //end method populateStopsOnRouteGrid


}   //end class

