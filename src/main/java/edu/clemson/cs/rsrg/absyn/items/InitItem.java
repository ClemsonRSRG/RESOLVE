/**
 * InitItem.java
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

import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.absyn.declarations.programdecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.AuxVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the type initialization items
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class InitItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>List of affected state variables.</p> */
    private final List<AffectsClause> myAffectedVars;

    /** <p>List of auxiliary variable declarations.</p> */
    private final List<AuxVarDec> myAuxVariableDecs;

    /** <p>The ensures expression</p> */
    private final Exp myEnsures;

    /** <p>List of facility declarations.</p> */
    private final List<FacilityDec> myFacilityDecs;

    /** <p>The requires expression</p> */
    private final Exp myRequires;

    /** <p>List of statements.</p> */
    private final List<Statement> myStatements;

    /** <p>List of variable declarations.</p> */
    private final List<VarDec> myVariableDecs;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a type initialization block that happens
     * when a variable of this type is initialized.</p>
     *
     * @param l A {@link Location} representation object.
     * @param affectsVars List of state variables affected by initialization.
     * @param requires A {@link Exp} representing the initialization's
     *                 requires clause.
     * @param ensures A {@link Exp} representing the initialization's
     *                ensures clause.
     * @param facilities List of facility declarations in this block.
     * @param variables List of variables in this block.
     * @param aux_variables List of auxiliary variables in this block.
     * @param statements List of statements in this block.
     */
    public InitItem(Location l, List<AffectsClause> affectsVars, Exp requires,
            Exp ensures, List<FacilityDec> facilities, List<VarDec> variables,
            List<AuxVarDec> aux_variables, List<Statement> statements) {
        super(l);
        myAffectedVars = affectsVars;
        myAuxVariableDecs = aux_variables;
        myEnsures = ensures;
        myFacilityDecs = facilities;
        myRequires = requires;
        myStatements = statements;
        myVariableDecs = variables;
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
        sb.append("InitItem\n");

        printSpace(indentSize, sb);
        sb.append("initialization\n");

        if (!myAffectedVars.isEmpty()) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append("affects ");

            Iterator<AffectsClause> it = myAffectedVars.iterator();
            while (it.hasNext()) {
                sb.append(it.next());

                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(";\n");
        }

        printSpace(indentSize + innerIndentSize, sb);
        sb.append("requires ");
        sb.append(myRequires.asString(0, 0));
        sb.append(";\n");

        printSpace(indentSize + innerIndentSize, sb);
        sb.append("ensures ");
        sb.append(myEnsures.asString(0, 0));
        sb.append(";\n");

        Iterator<FacilityDec> it1 = myFacilityDecs.iterator();
        while (it1.hasNext()) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append(it1.next());
            sb.append("\n");
        }

        Iterator<VarDec> it2 = myVariableDecs.iterator();
        while (it2.hasNext()) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append(it2.next());
            sb.append("\n");
        }

        Iterator<AuxVarDec> it3 = myAuxVariableDecs.iterator();
        while (it3.hasNext()) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append(it3.next());
            sb.append("\n");
        }

        Iterator<Statement> it4 = myStatements.iterator();
        while (it4.hasNext()) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append(it4.next());
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link InitItem} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public InitItem clone() {
        return new InitItem(new Location(myLoc), copyAffectedItems(),
                myRequires.clone(), myEnsures.clone(), copyFacDecs(),
                copyVars(), copyAuxVars(), copyStatements());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link InitItem} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof InitItem) {
            InitItem initItem = (InitItem) o;
            result =
                    myLoc.equals(initItem.myLoc)
                            && myRequires.equals(initItem.myRequires)
                            && myEnsures.equals(initItem.myEnsures);

            if (result) {
                if (myAffectedVars != null && initItem.myAffectedVars != null) {
                    Iterator<AffectsClause> thisVars =
                            myAffectedVars.iterator();
                    Iterator<AffectsClause> eVars =
                            initItem.myAffectedVars.iterator();

                    while (result && thisVars.hasNext() && eVars.hasNext()) {
                        result &= thisVars.next().equals(eVars.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisVars.hasNext()) && (!eVars.hasNext());
                }
            }

            if (result) {
                if (myAuxVariableDecs != null
                        && initItem.myAuxVariableDecs != null) {
                    Iterator<AuxVarDec> thisVars = myAuxVariableDecs.iterator();
                    Iterator<AuxVarDec> eVars =
                            initItem.myAuxVariableDecs.iterator();

                    while (result && thisVars.hasNext() && eVars.hasNext()) {
                        result &= thisVars.next().equals(eVars.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisVars.hasNext()) && (!eVars.hasNext());
                }
            }

            if (result) {
                if (myFacilityDecs != null && initItem.myFacilityDecs != null) {
                    Iterator<FacilityDec> thisDecs = myFacilityDecs.iterator();
                    Iterator<FacilityDec> eDecs =
                            initItem.myFacilityDecs.iterator();

                    while (result && thisDecs.hasNext() && eDecs.hasNext()) {
                        result &= thisDecs.next().equals(eDecs.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisDecs.hasNext()) && (!eDecs.hasNext());
                }
            }

            if (result) {
                if (myStatements != null && initItem.myStatements != null) {
                    Iterator<Statement> thisStatements =
                            myStatements.iterator();
                    Iterator<Statement> eStatements =
                            initItem.myStatements.iterator();

                    while (result && thisStatements.hasNext()
                            && eStatements.hasNext()) {
                        result &=
                                thisStatements.next()
                                        .equals(eStatements.next());
                    }

                    //Both had better have run out at the same time
                    result &=
                            (!thisStatements.hasNext())
                                    && (!eStatements.hasNext());
                }
            }

            if (result) {
                if (myVariableDecs != null && initItem.myVariableDecs != null) {
                    Iterator<VarDec> thisVars = myVariableDecs.iterator();
                    Iterator<VarDec> eVars = initItem.myVariableDecs.iterator();

                    while (result && thisVars.hasNext() && eVars.hasNext()) {
                        result &= thisVars.next().equals(eVars.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisVars.hasNext()) && (!eVars.hasNext());
                }
            }
        }

        return result;
    }

    /**
     * <p>Returns the list of affected state variables in this initialization
     * block.</p>
     *
     * @return A list of {@link AffectsClause} representation objects.
     */
    public List<AffectsClause> getAffectedItems() {
        return copyAffectedItems();
    }

    /**
     * <p>Returns the list of auxiliary variables in this initialization
     * block.</p>
     *
     * @return A list of {@link AuxVarDec} representation objects.
     */
    public List<AuxVarDec> getAuxVariables() {
        return copyAuxVars();
    }

    /**
     * <p>This method returns a deep copy of the ensures expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getEnsures() {
        return myEnsures.clone();
    }

    /**
     * <p>Returns the list of facility declarations in this initialization
     * block.</p>
     *
     * @return A list of {@link FacilityDec} representation objects.
     */
    public List<FacilityDec> getFacilities() {
        return copyFacDecs();
    }

    /**
     * <p>This method returns a deep copy of the requires expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getRequires() {
        return myRequires.clone();
    }

    /**
     * <p>Returns the list of statements in this initialization
     * block.</p>
     *
     * @return A list of {@link Statement} representation objects.
     */
    public List<Statement> getStatements() {
        return copyStatements();
    }

    /**
     * <p>Returns the list of variables in this initialization
     * block.</p>
     *
     * @return A list of {@link VarDec} representation objects.
     */
    public List<VarDec> getVariables() {
        return copyVars();
    }

    /**
     * <p>Returns the symbol in string format.</p>
     *
     * @return Symbol as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("initialization\n");

        if (!myAffectedVars.isEmpty()) {
            sb.append("\taffects ");

            Iterator<AffectsClause> it = myAffectedVars.iterator();
            while (it.hasNext()) {
                sb.append(it.next());

                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(";\n");
        }

        sb.append("\trequires ");
        sb.append(myRequires.toString());
        sb.append(";\n");

        sb.append("\tensures ");
        sb.append(myEnsures.toString());
        sb.append(";\n");

        Iterator<FacilityDec> it1 = myFacilityDecs.iterator();
        while (it1.hasNext()) {
            sb.append("\t");
            sb.append(it1.next());
            sb.append("\n");
        }

        Iterator<VarDec> it2 = myVariableDecs.iterator();
        while (it2.hasNext()) {
            sb.append("\t");
            sb.append(it2.next());
            sb.append("\n");
        }

        Iterator<AuxVarDec> it3 = myAuxVariableDecs.iterator();
        while (it3.hasNext()) {
            sb.append("\t");
            sb.append(it3.next());
            sb.append("\n");
        }

        Iterator<Statement> it4 = myStatements.iterator();
        while (it4.hasNext()) {
            sb.append("\t");
            sb.append(it4.next());
            sb.append("\n");
        }

        return sb.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the affected items.</p>
     *
     * @return A list containing {@link AffectsClause}s.
     */
    private List<AffectsClause> copyAffectedItems() {
        List<AffectsClause> copyArgs = new ArrayList<>();
        for (AffectsClause a : myAffectedVars) {
            copyArgs.add(a.clone());
        }

        return copyArgs;
    }

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the auxiliary variables.</p>
     *
     * @return A list containing {@link AuxVarDec}s.
     */
    private List<AuxVarDec> copyAuxVars() {
        List<AuxVarDec> copyArgs = new ArrayList<>();
        for (AuxVarDec a : myAuxVariableDecs) {
            copyArgs.add(a.clone());
        }

        return copyArgs;
    }

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the facility declarations.</p>
     *
     * @return A list containing {@link FacilityDec}s.
     */
    private List<FacilityDec> copyFacDecs() {
        List<FacilityDec> copyArgs = new ArrayList<>();
        for (FacilityDec f : myFacilityDecs) {
            copyArgs.add(f.clone());
        }

        return copyArgs;
    }

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the statements.</p>
     *
     * @return A list containing {@link Statement}s.
     */
    private List<Statement> copyStatements() {
        List<Statement> copyArgs = new ArrayList<>();
        for (Statement s : myStatements) {
            copyArgs.add(s.clone());
        }

        return copyArgs;
    }

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the variable declarations.</p>
     *
     * @return A list containing {@link VarDec}s.
     */
    private List<VarDec> copyVars() {
        List<VarDec> copyArgs = new ArrayList<>();
        for (VarDec v : myVariableDecs) {
            copyArgs.add(v.clone());
        }

        return copyArgs;
    }
}