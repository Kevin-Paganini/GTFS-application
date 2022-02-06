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

import java.util.Collection;
import java.util.Optional;

public class TripTabController {

    private Trip trip;

    //Heres a really cool comment
    private Controller controller;

    @FXML
    private Label title;
    @FXML
    private GridPane propertiesGrid;

    public void setTrip(Trip trip){
        this.trip = trip;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Displays the information of the stop in the StopTab
     */
    public void displayTripInfo() {
        if (trip != null) {
            title.setText("Trip: " + trip.getTripID());

            int propertiesIndex = 0;

            String[] values = trip.getColumnValues();
            for (String property : Trip.COLUMN_TITLES) {
                propertiesGrid.add(
                        GUIDefaultElements.getGridCellLabel(
                                property,
                                propertiesIndex),
                        0,
                        propertiesIndex);


                Label valueLabel = new Label(values[propertiesIndex]);

                if (!property.equals("Trip ID") && !property.equals("Route ID")) {
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
                                // changing the Route ID is a little trickier than expected,
                                // so we left ability to change this ability out [DEPRECATED]
//                                case "Route ID":
//                                    Collection<Route> foundRoutes = Database.getInstance().getRoutes(r -> r.getRouteID().equals(val));
//                                    if (foundRoutes.size() > 0) {
//                                        trip.setRouteID(val);
//                                        valueLabel.setText(val);
//                                    } else {
//                                        Alert a = new Alert(Alert.AlertType.ERROR);
//                                        a.setHeaderText("Invalid Route ID");
//                                        a.setContentText("Inputted Route ID " + val + " was not found in the database! Please use an existing route.");
//                                        a.showAndWait();
//                                    }
//
//                                    break;
                                case "Service ID":
                                    if (val.equals("")) {
                                        Alert a = new Alert(Alert.AlertType.ERROR);
                                        a.setHeaderText("Invalid Service ID");
                                        a.setContentText("Inputted Service ID must be a valid String.");
                                        a.showAndWait();
                                    } else {
                                        trip.setServiceID(val);
                                        valueLabel.setText(val);
                                    }
                                    break;
                                case "Trip Headsign":
                                    trip.setTripHeadSign(val);
                                    valueLabel.setText(val);
                                    break;
                                case "Direction ID":
                                    try {
                                        if (StringValidation.isValidInt(val)) {
                                            int id = Integer.parseInt(val);
                                            trip.setDirectionID(id);
                                            valueLabel.setText(val);
                                        } else {
                                            throw new NumberFormatException("Not a valid integer!");
                                        }

                                    } catch (NumberFormatException numberFormatException) {
                                        Alert a = new Alert(Alert.AlertType.ERROR);
                                        a.setHeaderText("Invalid Direction ID");
                                        a.setContentText("Inputted Direction ID " + val + " was not a valid integer!\n\n" + numberFormatException.getMessage());
                                        a.showAndWait();
                                    }

                                    break;
                                case "Block ID":
                                    trip.setBlockID(val);
                                    valueLabel.setText(val);
                                    break;
                                case "Shape ID":
                                    trip.setShapeID(val);
                                    valueLabel.setText(val);
                                    break;

                            }
                        });
                    });     //end editButton action event
                    Pane gridCellLabel = GUIDefaultElements.getGridCellLeftRightNodes(valueLabel, editButton, propertiesIndex);
                    propertiesGrid.add(gridCellLabel, 1, propertiesIndex);
                } else {
                    Pane gridCellLabel = GUIDefaultElements.getGridCellNode(valueLabel, propertiesIndex);
                    propertiesGrid.add(gridCellLabel, 1, propertiesIndex);
                }


                ++propertiesIndex;
            }

            propertiesGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                            "Trip Distance",
                            propertiesIndex),
                    0,
                    propertiesIndex);
            if (trip.getStopTimeIDs().size() > 2) {
                propertiesGrid.add(
                        GUIDefaultElements.getGridCellLabel(
                                Double.toString(trip.getTripDistance()) + " miles",
                                propertiesIndex),
                        1,
                        propertiesIndex);
                ++propertiesIndex;
            } else {
                propertiesGrid.add(
                        GUIDefaultElements.getGridCellLabel(
                                "NA (Trip Had 1 or Fewer Stops)",
                                propertiesIndex),
                        1,
                        propertiesIndex);
                ++propertiesIndex;
            }

            propertiesGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                        "Stops on Trip",
                        propertiesIndex),
                    0,
                    propertiesIndex);
            propertiesGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                            trip.getStopIDs().size() + " stops",
                            propertiesIndex),
                    1,
                    propertiesIndex);
            ++propertiesIndex;

            propertiesGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                        "Stop Times on Trip",
                        propertiesIndex),
                    0,
                    propertiesIndex);
            propertiesGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                            trip.getStopTimeIDs().size() + " stops times",
                            propertiesIndex),
                    1,
                    propertiesIndex);
            ++propertiesIndex;

            propertiesGrid.add(
                    GUIDefaultElements.getGridCellLabel(
                        "Trip Avg Speed",
                        propertiesIndex),
                    0,
                    propertiesIndex);
            if (trip.getAvgTripSpeed() != -1) {
                propertiesGrid.add(
                        GUIDefaultElements.getGridCellLabel(
                                trip.getAvgTripSpeed() + " miles per hour",
                                propertiesIndex),
                        1,
                        propertiesIndex);
            } else {
                propertiesGrid.add(
                        GUIDefaultElements.getGridCellLabel(
                                "0*",
                                propertiesIndex),
                        1,
                        propertiesIndex);
            }
        }
    }
}
