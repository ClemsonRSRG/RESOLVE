/**
 * PerformanceFinalItem.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.items;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the performance type finalization items
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class PerformanceFinalItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The ensures expression</p> */
    private final Exp myDuration;

    /** <p>The requires expression</p> */
    private final Exp myManipDisp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a performance type finalization block that happens
     * when a variable of this type is finalized.</p>
     *
     * @param l A {@link Location} representation object.
     * @param duration A {@link Exp} representing the finalization's
     *                 duration clause.
     * @param manip_disp A {@link Exp} representing the finalization's
     *                   manipulative displacement clause.
     */
    public PerformanceFinalItem(Location l, Exp duration, Exp manip_disp) {
        super(l);
        myDuration = duration;
        myManipDisp = manip_disp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("PerformanceFinalItem\n");

        printSpace(indentSize, sb);
        sb.append("finalization\n");

        printSpace(indentSize + innerIndentSize, sb);
        sb.append("duration  ");
        sb.append(myManipDisp.asString(0, 0));
        sb.append(";\n");

        printSpace(indentSize + innerIndentSize, sb);
        sb.append("manip_disp ");
        sb.append(myDuration.asString(0, 0));
        sb.append(";\n");

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link PerformanceFinalItem} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public PerformanceFinalItem clone() {
        return new PerformanceFinalItem(new Location(myLoc), myManipDisp
                .clone(), myDuration.clone());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link PerformanceFinalItem} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof PerformanceFinalItem) {
            PerformanceFinalItem finalItem = (PerformanceFinalItem) o;
            result =
                    myLoc.equals(finalItem.myLoc)
                            && myManipDisp.equals(finalItem.myManipDisp)
                            && myDuration.equals(finalItem.myDuration);
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the duration expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getDuration() {
        return myDuration.clone();
    }

    /**
     * <p>This method returns a deep copy of the manipulative
     * displacement expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getManipDisp() {
        return myManipDisp.clone();
    }

    /**
     * <p>Returns the symbol in string format.</p>
     *
     * @return Symbol as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("finalization\n");

        sb.append("\tduration ");
        sb.append(myManipDisp.toString());
        sb.append(";\n");

        sb.append("\tmanip_disp ");
        sb.append(myDuration.toString());
        sb.append(";\n");

        return sb.toString();
    }

}