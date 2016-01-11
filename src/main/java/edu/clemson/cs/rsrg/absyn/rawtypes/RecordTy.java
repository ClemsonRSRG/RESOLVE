/**
 * RecordTy.java
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
package edu.clemson.cs.rsrg.absyn.rawtypes;

import edu.clemson.cs.rsrg.absyn.Ty;
import edu.clemson.cs.rsrg.absyn.variables.VarDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the raw record types
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class RecordTy extends Ty {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The raw type's fields</p> */
    private final List<VarDec> myInnerFields;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a raw record type.</p>
     *
     * @param l A {@link Location} representation object.
     * @param fields A list of {@link VarDec} representing the fields
     *               inside this raw record type.
     */
    public RecordTy(Location l, List<VarDec> fields) {
        super(l);
        myInnerFields = fields;
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
        sb.append("RecordTy\n");

        for (VarDec v : myInnerFields) {
            sb
                    .append(v.asString(indentSize + innerIndentSize,
                            innerIndentSize));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link RecordTy} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof RecordTy) {
            RecordTy eAsRecordTy = (RecordTy) o;

            if (myInnerFields != null && eAsRecordTy.myInnerFields != null) {
                Iterator<VarDec> thisInnerFields = myInnerFields.iterator();
                Iterator<VarDec> eInnerFields =
                        eAsRecordTy.myInnerFields.iterator();

                while (result && thisInnerFields.hasNext()
                        && eInnerFields.hasNext()) {
                    result &=
                            thisInnerFields.next().equals(eInnerFields.next());
                }

                //Both had better have run out at the same time
                result &=
                        (!thisInnerFields.hasNext())
                                && (!eInnerFields.hasNext());
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of all the fields.</p>
     *
     * @return The {@link VarDec} representation object.
     */
    public final List<VarDec> getFields() {
        List<VarDec> copyFields = new ArrayList<>();
        for (VarDec v : myInnerFields) {
            copyFields.add(v.clone());
        }

        return copyFields;
    }

    /**
     * <p>Returns the raw type in string format.</p>
     *
     * @return Raw type as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (VarDec v : myInnerFields) {
            sb.append(v.toString());
            sb.append("\n");
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
        return new RecordTy(new Location(myLoc), getFields());
    }

}