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

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class GUIDefaultElements {

    public static Pane getGridCellLabel(String value, int index) {
        return getGridCellNode(new Label(value), index);
    }

    public static Pane getGridCellNode(Node node, int index) {      //TODO: Migrate this to CSS!
        Pane pane = new Pane();
        pane.getStyleClass().add(index % 2 == 0 ? "even-grid-cell" : "odd-grid-cell");
        node.getStyleClass().add(index % 2 == 0 ? "even-grid-cell" : "odd-grid-cell");
        pane.setPadding(new Insets(0, 5, 0 ,5));
        pane.getChildren().add(node);
        pane.setPrefHeight(20);
        pane.setMinHeight(20);
        return pane;
    }

    public static Pane getGridCellNodes(int index, Node ... nodes) {
        Pane pane = new HBox();
        pane.getStyleClass().add(index % 2 == 0 ? "even-grid-cell" : "odd-grid-cell");
        for (Node node: nodes) {
            node.getStyleClass().add(index % 2 == 0 ? "even-grid-cell" : "odd-grid-cell");
            pane.getChildren().add(node);
        }
        pane.setPadding(new Insets(0, 5, 0 ,5));
        pane.setPrefHeight(20);
        pane.setMinHeight(20);
        return pane;
    }

    public static Pane getGridCellLeftRightNodes(Node left, Node right, int index) {
        Pane pane = new HBox();
        pane.getStyleClass().add(index % 2 == 0 ? "even-grid-cell" : "odd-grid-cell");
        left.getStyleClass().add(index % 2 == 0 ? "even-grid-cell" : "odd-grid-cell");
        right.getStyleClass().add(index % 2 == 0 ? "even-grid-cell" : "odd-grid-cell");
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        pane.getChildren().addAll(left, spacer, right);
        pane.setPadding(new Insets(0, 5, 0 ,5));
        pane.setPrefHeight(20);
        pane.setMinHeight(20);
        return pane;
    }
}
