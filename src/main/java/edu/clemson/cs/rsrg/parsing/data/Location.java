/**
 * Location.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.parsing.data;

import edu.clemson.cs.rsrg.init.file.ResolveFile;

/**
 * <p>This class points to the location within a ResolveFile.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
@Deprecated
public class Location {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The file that this location points to.</p> */
    private final ResolveFile myFile;

    /** <p>The position that this location points to.</p> */
    private final Pos myPosition;

    /** <p>The additional details about this location.</p> */
    private final String myLocationDetails;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructor takes all the information relevant
     * in identifying a particular location in the file.</p>
     *
     * @param file File that this location points to.
     * @param lineNumber Line number inside the file.
     * @param columnNumber Column number inside the file.
     * @param locationDetails Additional details about the location.
     */
    public Location(ResolveFile file, int lineNumber, int columnNumber,
            String locationDetails) {
        myFile = file;
        myPosition = new Pos(lineNumber, columnNumber);

        if (locationDetails == null) {
            locationDetails = "";
        }

        myLocationDetails = locationDetails;
    }

    /**
     * <p>Copy constructor</p>
     *
     * @param l The location object to copy.
     */
    public Location(Location l) {
        myFile = l.myFile;
        myPosition = new Pos(l.myPosition);
        myLocationDetails = new String(l.myLocationDetails);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Equals method to compare two locations.</p>
     *
     * @param loc The location object to compare.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    public boolean equals(Location loc) {
        boolean result;
        if (loc == null) {
            result = false;
        }
        else {
            result =
                    (myFile.equals(loc.myFile)
                            && myPosition.equals(loc.myPosition) && myLocationDetails
                            .equals(loc.myLocationDetails));
        }
        return result;
    }

    /**
     * <p>Getter for the current column.</p>
     *
     * @return Column number
     */
    public int getColumn() {
        return myPosition.myCurrColumn;
    }

    /**
     * <p>Getter for extended details for this location.</p>
     *
     * @return Location details
     */
    public String getDetails() {
        return myLocationDetails;
    }

    /**
     * <p>Getter for the current file.</p>
     *
     * @return <code>ResolveFile</code> object
     */
    public ResolveFile getFile() {
        return myFile;
    }

    /**
     * <p>Getter for the current filename.</p>
     *
     * @return Filename as a String.
     */
    public String getFilename() {
        return myFile.getName();
    }

    /**
     * <p>Getter for the current line.</p>
     *
     * @return Line number
     */
    public int getLine() {
        return myPosition.myCurrline;
    }

    /**
     * <p>Returns the location in string format.</p>
     *
     * @return Location as a string.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        // Append the name of the file and the extension
        sb.append(myFile.getName());
        sb.append(myFile.getModuleType().getExtension());

        // Append the line number and the column number
        sb.append("(");
        sb.append(myPosition.myCurrline);
        sb.append(":");
        sb.append(myPosition.myCurrColumn);
        sb.append(")");

        return sb.toString();
    }

    // ===========================================================
    // Inner Class for Position
    // ===========================================================

    /**
     * <p>Private inner class to store the position in the file.</p>
     */
    private class Pos {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>Current line number.</p> */
        private final int myCurrline;

        /** <p>Current column number.</p> */
        private final int myCurrColumn;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructor converts the two integers corresponding
         * to the line and column numbers into an object representation.</p>
         *
         * @param line Current line number.
         * @param column Current column number.
         */
        private Pos(int line, int column) {
            myCurrline = line;
            myCurrColumn = column;
        }

        /**
         * <p>Copy constructor</p>
         *
         * @param p The position object to copy.
         */
        private Pos(Pos p) {
            myCurrline = p.myCurrline;
            myCurrColumn = p.myCurrColumn;
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>Equals method to compare two positions.</p>
         *
         * @param pos The position object to compare.
         *
         * @return True if all the fields are equal, false otherwise.
         */
        public boolean equals(Pos pos) {
            boolean result;
            if (pos == null) {
                result = false;
            }
            else {
                result =
                        (myCurrline == pos.myCurrline && myCurrColumn == pos.myCurrColumn);
            }
            return result;
        }

        /**
         * <p>Returns the position in string format.</p>
         *
         * @return Position as a string.
         */
        public String toString() {
            return "(" + myCurrline + "," + myCurrColumn + ")";
        }

    }

}