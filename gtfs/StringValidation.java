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

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * A group of static methods to check that Strings are of the correct format to be another type
 * @author keanej
 */
public class StringValidation {
    /**
     * Checks that a String is a valid time [HH:mm:ss]
     * @param time The String
     * @return True if a valid time, false otherwise
     */
    public static boolean isValidTime(String time) {
        String pattern = "\\d{2}:\\d{2}:\\d{2}";
        boolean flag = false;
        if (Pattern.matches(pattern, time)) {
            int[] vals = Arrays.stream(time.split(":")).mapToInt(Integer::parseInt).toArray();
            flag = vals[0] < 48 && vals[1] < 60 && vals[2] < 60;
        }
        return flag;
    }

    /**
     * Regex checks that a String is a valid color [6 hex digits]
     * @param color The string
     * @return True if the string is a valid color, false otherwise
     */
    public static boolean isValidColor(String color) {
        String colorPattern = "[0-9a-fA-F]{6}";
        return Pattern.matches(colorPattern, color);
    }

    /**
     * Makes sure that a String is parsable as an int
     * @param value The string value
     * @return True if is valid int, false otherwise
     */
    public static boolean isValidInt(String value) {
        String pattern = "^(\\+|-)?\\d+$";
        return Pattern.matches(pattern, value);
    }

    /**
     * Make sure that a String is a non-negative int
     * @param value The string value
     * @return True is valid non-negative int, false otherwise
     */
    public static boolean isValidNonNegativeInt(String value) {
        String pattern = "^\\d+$";
        return Pattern.matches(pattern, value);
    }

    /**
     * Checks that a coordinate is valid
     * @param value The string value to check
     * @param isLatitude If this is evaluating latitude or longitude
     * @return True if valid coordinate, false otherwise
     */
    public static boolean isValidCoordinate(String value, boolean isLatitude) {
        String pattern = "^[-+]?[0-9]*\\.?[0-9]+$";
        double limit = isLatitude ? 90.0 : 180.0;
        if (Pattern.matches(pattern, value)) {
            double doubleVal = Double.parseDouble(value);
            return doubleVal <= limit && doubleVal >= -limit;
        }
        return false;
    }
}
