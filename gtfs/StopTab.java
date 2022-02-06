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

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.io.IOException;

/**
 * Extension of a Tab that creates a new Tab with the structuring of stopTab.fxml
 * for displaying stop information
 *
 * @author keanej
 */
public class StopTab extends Tab {
    /**
     * Initializes a StopTab and its controller
     * @param stop The Stop
     * @param controller The main Controller
     * @throws IOException Occurs when cannot read the fxml file
     */
    public StopTab(Stop stop, Controller controller) throws IOException {
        super("STOP: " + stop.getStopID());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("stopTab.fxml"));
        Parent root = loader.load();
        ((StopTabController)loader.getController()).setStop(stop);
        ((StopTabController)loader.getController()).setController(controller);
        ((StopTabController)loader.getController()).displayStopInfo();
        super.setContent(root);
    }
}
