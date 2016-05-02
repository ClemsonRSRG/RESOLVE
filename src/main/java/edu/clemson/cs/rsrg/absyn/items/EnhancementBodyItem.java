/**
 * EnhancementBodyItem.java
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
package edu.clemson.cs.rsrg.absyn.items;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the facility declaration arguments
 * for Enhancement Realization extension modules that the compiler builds from
 * the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class EnhancementBodyItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Name of this imported module.</p> */
    private final PosSymbol myEnhancementName;

    /** <p>List of parameters arguments for this enhancement extension.</p> */
    private final List<ModuleArgumentItem> myEnhancementParams;

    /** <p>Profile name for this imported module.</p> */
    private final PosSymbol myProfileName;

    /** <p>Name of the implementation module.</p> */
    private final PosSymbol myEnhancementRealizName;

    /** <p>List of parameters arguments for the enhancement implementation.</p> */
    private final List<ModuleArgumentItem> myEnhancementRealizParams;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a enhancement implementation extension argument
     * for facility declarations.</p>
     *
     * @param name Name of the extended enhancement module.
     * @param params The parameter arguments that are passed to instantiate this enhancement.
     * @param bodyName Name of the enhancement implementation module.
     * @param bodyParams The parameter arguments that are passed to instantiate the enhancement implementation.
     * @param profileName Performance profile name for the extended enhancement module.
     */
    public EnhancementBodyItem(PosSymbol name, List<ModuleArgumentItem> params,
            PosSymbol bodyName, List<ModuleArgumentItem> bodyParams,
            PosSymbol profileName) {
        super(name.getLocation());
        myEnhancementName = name;
        myEnhancementParams = params;
        myEnhancementRealizName = bodyName;
        myEnhancementRealizParams = bodyParams;
        myProfileName = profileName;
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
        sb.append("EnhancementBodyItem\n");

        printSpace(indentSize + innerIndentSize, sb);
        sb.append("enhanced by ");
        sb.append(myEnhancementName.asString(0, innerIndentSize));
        sb.append("(");
        Iterator<ModuleArgumentItem> it = myEnhancementParams.iterator();
        while (it.hasNext()) {
            sb.append(it.next().asString(0, innerIndentSize));

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")\n");

        printSpace(indentSize + innerIndentSize, sb);
        sb.append("realized by ");
        sb.append(myEnhancementRealizName.asString(0, innerIndentSize));
        sb.append("(");
        Iterator<ModuleArgumentItem> it2 = myEnhancementRealizParams.iterator();
        while (it2.hasNext()) {
            sb.append(it2.next().asString(0, innerIndentSize));

            if (it2.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")\n");

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link EnhancementBodyItem} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public EnhancementBodyItem clone() {
        PosSymbol profileName = null;
        if (myProfileName != null) {
            profileName = myProfileName.clone();
        }

        return new EnhancementBodyItem(myEnhancementName.clone(),
                copyEnhArgs(), myEnhancementRealizName.clone(),
                copyEnhRealizArgs(), profileName);
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link EnhancementBodyItem} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof EnhancementBodyItem) {
            EnhancementBodyItem enhancementBodyItem = (EnhancementBodyItem) o;
            result =
                    myEnhancementName
                            .equals(enhancementBodyItem.myEnhancementName)
                            && myEnhancementRealizName
                                    .equals(enhancementBodyItem.myEnhancementRealizName);

            if (result) {
                if (myEnhancementParams != null
                        && enhancementBodyItem.myEnhancementParams != null) {
                    Iterator<ModuleArgumentItem> thisParams =
                            myEnhancementParams.iterator();
                    Iterator<ModuleArgumentItem> eParams =
                            enhancementBodyItem.myEnhancementParams.iterator();

                    while (result && thisParams.hasNext() && eParams.hasNext()) {
                        result &= thisParams.next().equals(eParams.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisParams.hasNext()) && (!eParams.hasNext());
                }
            }

            if (result) {
                if (myEnhancementRealizParams != null
                        && enhancementBodyItem.myEnhancementRealizParams != null) {
                    Iterator<ModuleArgumentItem> thisParams =
                            myEnhancementRealizParams.iterator();
                    Iterator<ModuleArgumentItem> eParams =
                            enhancementBodyItem.myEnhancementRealizParams
                                    .iterator();

                    while (result && thisParams.hasNext() && eParams.hasNext()) {
                        result &= thisParams.next().equals(eParams.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisParams.hasNext()) && (!eParams.hasNext());
                }
            }
        }

        return result;
    }

    /**
     * <p>Returns the symbol representation for the enhancement name.</p>
     *
     * @return A {@link PosSymbol} representation of the enhancement name.
     */
    public PosSymbol getEnhancementName() {
        return myEnhancementName.clone();
    }

    /**
     * <p>Returns the list of arguments for this enhancement
     * extension.</p>
     *
     * @return A list of {@link ModuleArgumentItem} representation objects.
     */
    public List<ModuleArgumentItem> getEnhancementParams() {
        return copyEnhArgs();
    }

    /**
     * <p>Returns the symbol representation for the enhancement realization name.</p>
     *
     * @return A {@link PosSymbol} representation of the enhancement realization name.
     */
    public PosSymbol getEnhancementRealizName() {
        return myEnhancementRealizName.clone();
    }

    /**
     * <p>Returns the list of arguments for the enhancement realization.</p>
     *
     * @return A list of {@link ModuleArgumentItem} representation objects.
     */
    public List<ModuleArgumentItem> getEnhancementRealizParams() {
        return copyEnhRealizArgs();
    }

    /**
     * <p>Returns the symbol representation for the performance profile name.</p>
     *
     * @return A {@link PosSymbol} representation of the performance profile name.
     */
    public PosSymbol getProfileName() {
        return myProfileName.clone();
    }

    /**
     * <p>Returns the symbol in string format.</p>
     *
     * @return Symbol as a string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(myEnhancementName.toString());
        sb.append("(");
        Iterator<ModuleArgumentItem> it = myEnhancementParams.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")\n");

        sb.append(myEnhancementRealizName.toString());
        sb.append("(");

        Iterator<ModuleArgumentItem> it2 = myEnhancementRealizParams.iterator();
        while (it2.hasNext()) {
            sb.append(it2.next().toString());

            if (it2.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the enhancement parameter arguments.</p>
     *
     * @return A list containing {@link ModuleArgumentItem}s.
     */
    private List<ModuleArgumentItem> copyEnhArgs() {
        List<ModuleArgumentItem> copyArgs = new ArrayList<>();
        for (ModuleArgumentItem m : myEnhancementParams) {
            copyArgs.add(m.clone());
        }

        return copyArgs;
    }

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the enhancement realization parameter arguments.</p>
     *
     * @return A list containing {@link ModuleArgumentItem}s.
     */
    private List<ModuleArgumentItem> copyEnhRealizArgs() {
        List<ModuleArgumentItem> copyArgs = new ArrayList<>();
        for (ModuleArgumentItem m : myEnhancementRealizParams) {
            copyArgs.add(m.clone());
        }

        return copyArgs;
    }
}