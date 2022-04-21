/*
 * Location.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.parsing.data;

import edu.clemson.rsrg.init.file.ResolveFile;

/**
 * <p>
 * This class points to the location within a {@link ResolveFile}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class Location implements Cloneable {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The file that this location points to.
     * </p>
     */
    private final ResolveFile myFile;

    /**
     * <p>
     * The position that this location points to.
     * </p>
     */
    private final Pos myPosition;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructor takes all the information relevant in identifying a particular location in the file.
     * </p>
     *
     * @param file
     *            File that this location points to.
     * @param lineNumber
     *            Line number inside the file.
     * @param columnNumber
     *            Column number inside the file.
     */
    public Location(ResolveFile file, int lineNumber, int columnNumber) {
        myFile = file;
        myPosition = new Pos(lineNumber, columnNumber);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method overrides the default clone method implementation for the {@link Location} class.
     * </p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final Location clone() {
        return new Location(myFile, myPosition.myCurrline, myPosition.myCurrColumn);
    }

    /**
     * <p>
     * Equals method to compare two locations.
     * </p>
     *
     * @param o
     *            Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Location location = (Location) o;

        return myFile.equals(location.myFile) && myPosition.equals(location.myPosition);
    }

    /**
     * <p>
     * Getter for the current column.
     * </p>
     *
     * @return Column number
     */
    public final int getColumn() {
        return myPosition.myCurrColumn;
    }

    /**
     * <p>
     * Getter for the current file.
     * </p>
     *
     * @return <code>ResolveFile</code> object
     */
    public final ResolveFile getFile() {
        return myFile;
    }

    /**
     * <p>
     * Getter for the current filename.
     * </p>
     *
     * @return Filename as a String.
     */
    public final String getFilename() {
        return myFile.getName();
    }

    /**
     * <p>
     * Getter for the current line.
     * </p>
     *
     * @return Line number
     */
    public final int getLine() {
        return myPosition.myCurrline;
    }

    /**
     * <p>
     * This method overrides the default {@code hashCode} method implementation for the {@code Location} class.
     * </p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myFile.hashCode();
        result = 31 * result + myPosition.hashCode();
        return result;
    }

    /**
     * <p>
     * Returns the location in string format.
     * </p>
     *
     * @return Location as a string.
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();

        // Append the name of the file and the extension
        sb.append(myFile.getName());
        sb.append(".");
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
     * <p>
     * Private inner class to store the position in the file.
     * </p>
     */
    private class Pos {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * Current line number.
         * </p>
         */
        private final int myCurrline;

        /**
         * <p>
         * Current column number.
         * </p>
         */
        private final int myCurrColumn;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This constructor converts the two integers corresponding to the line and column numbers into an object
         * representation.
         * </p>
         *
         * @param line
         *            Current line number.
         * @param column
         *            Current column number.
         */
        private Pos(int line, int column) {
            myCurrline = line;
            myCurrColumn = column;
        }

        /**
         * <p>
         * Copy constructor
         * </p>
         *
         * @param p
         *            The position object to copy.
         */
        private Pos(Pos p) {
            myCurrline = p.myCurrline;
            myCurrColumn = p.myCurrColumn;
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>
         * Equals method to compare two positions.
         * </p>
         *
         * @param o
         *            Object to be compared.
         *
         * @return {@code true} if all the fields are equal, {@code false} otherwise.
         */
        @Override
        public final boolean equals(Object o) {
            // Check to see if it is null or if the classes are different
            if (o == null || getClass() != o.getClass()) {
                return false;
            } else {
                // Simply check if the references are the same.
                // Checking integers won't do the trick!
                return this == o;
            }
        }

        /**
         * <p>
         * This method overrides the default {@code hashCode} method implementation for the {@code Pos} class.
         * </p>
         *
         * @return The hash code associated with the object.
         */
        @Override
        public final int hashCode() {
            int result = myCurrline;
            result = 31 * result + myCurrColumn;
            return result;
        }

        /**
         * <p>
         * Returns the position in string format.
         * </p>
         *
         * @return Position as a string.
         */
        @Override
        public final String toString() {
            return "(" + myCurrline + "," + myCurrColumn + ")";
        }

    }

}
