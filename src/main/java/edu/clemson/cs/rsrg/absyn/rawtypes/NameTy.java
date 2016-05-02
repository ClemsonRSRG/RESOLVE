/**
 * NameTy.java
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
package edu.clemson.cs.rsrg.absyn.rawtypes;

import edu.clemson.cs.rsrg.absyn.Ty;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the raw named types
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class NameTy extends Ty {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The raw type's qualifier</p> */
    private PosSymbol myQualifier;

    /** <p>The raw type's name</p> */
    private final PosSymbol myName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a raw name type.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qualifier A {@link PosSymbol} qualifier object.
     * @param name A {@link PosSymbol} name object.
     */
    public NameTy(Location l, PosSymbol qualifier, PosSymbol name) {
        super(l);
        myQualifier = qualifier;
        myName = name;
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
        sb.append("NameTy\n");

        if (myQualifier != null) {
            sb.append(myQualifier.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append("::");
        }

        if (myName != null) {
            sb.append(myName.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link NameTy} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof NameTy) {
            NameTy eAsNameTy = (NameTy) o;

            if (myQualifier != null && eAsNameTy.myQualifier != null) {
                result = myQualifier.equals(eAsNameTy.myQualifier);
            }
            else if (myQualifier == null && eAsNameTy.myQualifier == null) {
                result = true;
            }

            result &= myName.equals(eAsNameTy.myName);
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public PosSymbol getName() {
        return myName.clone();
    }

    /**
     * <p>This method returns a deep copy of the qualifier name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public PosSymbol getQualifier() {
        return myQualifier.clone();
    }

    /**
     * <p>Sets the qualifier for this raw type.</p>
     *
     * @param qualifier The {@link PosSymbol} representation object.
     */
    public final void setQualifier(PosSymbol qualifier) {
        myQualifier = qualifier;
    }

    /**
     * <p>Returns the raw type in string format.</p>
     *
     * @return Raw type as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (myQualifier != null) {
            sb.append(myQualifier.toString());
            sb.append("::");
        }

        if (myName != null) {
            sb.append(myName.toString());
        }

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by this concrete subclass of {@link Ty} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link Ty} that is a deep copy of the original.
     */
    @Override
    protected Ty copy() {
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }
        PosSymbol newName = myName.clone();

        return new NameTy(new Location(myLoc), newQualifier, newName);
    }

}