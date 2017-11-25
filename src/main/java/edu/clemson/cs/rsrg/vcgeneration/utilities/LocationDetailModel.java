/*
 * LocationDetailModel.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration.utilities;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;

/**
 * <p>Since {@link Location} objects stored inside any {@link ResolveConceptualElement} is immutable,
 * the {@link VCGenerator} needs a way to store the new {@link Location} and its associated detail message.
 * Therefore, this class is an helper class that stores both source and destination {@link Location Locations}
 * as well as the detail message.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class LocationDetailModel {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>The destination {@link Location} where we want the detail message
     * to be displayed at.</p>
     */
    private final Location myDestLoc;

    /** <p>Message to be displayed by the {@link VCGenerator}.</p> */
    private final String myDetailMessage;

    /** <p>The source {@link Location} from a {@link ResolveConceptualElement}.</p> */
    private final Location mySrcLoc;

    // ===========================================================
    // Constructors
    // ===========================================================

    public LocationDetailModel(Location srcLoc, Location destLoc, String message) {
        myDestLoc = destLoc;
        myDetailMessage = message;
        mySrcLoc = srcLoc;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default {@code equals} method implementation.</p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LocationDetailModel that = (LocationDetailModel) o;

        if (!myDestLoc.equals(that.myDestLoc))
            return false;
        if (!myDetailMessage.equals(that.myDetailMessage))
            return false;
        return mySrcLoc.equals(that.mySrcLoc);
    }

    /**
     * <p>This method returns the location to be displayed by the
     * {@link VCGenerator}.</p>
     *
     * @return A {@link Location}.
     */
    public final Location getDestinationLoc() {
        return myDestLoc;
    }

    /**
     * <p>This method returns the message associated with the
     * destination {@link Location}.</p>
     *
     * @return A string.
     */
    public final String getDetailMessage() {
        return myDetailMessage;
    }

    /**
     * <p>This method returns the location from the source
     * {@link ResolveConceptualElement}.</p>
     *
     * @return A {@link Location}.
     */
    public final Location getSourceLoc() {
        return mySrcLoc;
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myDestLoc.hashCode();
        result = 31 * result + myDetailMessage.hashCode();
        result = 31 * result + mySrcLoc.hashCode();
        return result;
    }

}