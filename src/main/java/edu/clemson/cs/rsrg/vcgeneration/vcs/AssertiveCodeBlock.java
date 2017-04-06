/*
 * AssertiveCodeBlock.java
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
package edu.clemson.cs.rsrg.vcgeneration.vcs;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.parsing.data.BasicCapabilities;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>This class represents an assertive code block that the {@link VCGenerator}
 * uses to apply the various different proof rules.</p>
 *
 * @author Heather Keown Harton
 * @author Yu-Shan Sun
 * @version 3.0
 */
public class AssertiveCodeBlock implements BasicCapabilities, Cloneable {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>List of free variables.</p> */
    private final List<Exp> myFreeVars;

    /** <p>{@link ResolveConceptualElement} that created this object.</p> */
    private final ResolveConceptualElement myInstantiatingElement;

    /**
     * <p>List of {@link Sequent Sequents} representing the VCs
     * we are trying to prove.</p>
     */
    private List<Sequent> mySequents;

    /**
     * <p>List of {@link Statement Statements} that we
     * need to apply proof rules to./p>
     */
    private final LinkedList<Statement> myStatements;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new assertive code block for the
     * {@link VCGenerator}.</p>
     *
     * @param g The current type graph.
     * @param instantiatingElement The element that created this
     *                             assertive code block.
     */
    public AssertiveCodeBlock(TypeGraph g, ResolveConceptualElement instantiatingElement) {
        myFreeVars = new LinkedList<>();
        myInstantiatingElement = instantiatingElement;
        mySequents = new LinkedList<>();
        myStatements = new LinkedList<>();
        myTypeGraph = g;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Add the {@link Exp} containing the name of the free variable
     * if it is not already in our free variable list.</p>
     *
     * @param var A new variable.
     */
    public final void addFreeVar(Exp var) {
        if (!myFreeVars.contains(var)) {
            myFreeVars.add(var);
        }
    }

    /**
     * <p>Adds a new statement to the assertive code block.</p>
     *
     * @param statement A new {@link Statement}.
     */
    public final void addStatement(Statement statement) {
        myStatements.add(statement);
    }

    /**
     * <p>Adds new statements to the assertive code block.</p>
     *
     * @param statements A list of new {@link Statement Statements}.
     */
    public final void addStatements(List<Statement> statements) {
        myStatements.addAll(statements);
    }

    /**
     * <p>This method creates a special indented
     * text version of the instantiated object.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentInc The additional indentation increment
     *                       for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();

        // Free variables
        sb.append("Free Variables:\n");
        Iterator<Exp> freeVarIt = myFreeVars.iterator();
        while (freeVarIt.hasNext()) {
            Exp current = freeVarIt.next();
            sb.append(current);
            sb.append(" : ");
            sb.append(current.getMathType());

            if (freeVarIt.hasNext()) {
                sb.append(", ");
            }
            else {
                sb.append("\n");
            }
        }
        sb.append("\n");

        // Statements
        for (Statement statement : myStatements) {
            sb.append(statement.asString(indentSize, innerIndentInc));
            sb.append("\n");
        }
        sb.append("\n");

        // Sequents
        sb.append("Sequents:\n");
        for (Sequent sequent : mySequents) {
            sb.append(sequent.toString());
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default {@code clone} method implementation.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final AssertiveCodeBlock clone() {
        AssertiveCodeBlock newBlock =
                new AssertiveCodeBlock(myTypeGraph, myInstantiatingElement);

        Collections.copy(newBlock.myFreeVars, myFreeVars);
        Collections.copy(newBlock.mySequents, mySequents);
        Collections.copy(newBlock.myStatements, myStatements);

        return newBlock;
    }

    /**
     * <p>This method returns the instantiating element that created
     * this assertive code block.</p>
     *
     * @return A {@link ResolveConceptualElement}.
     */
    public final ResolveConceptualElement getInstantiatingElement() {
        return myInstantiatingElement;
    }

    /**
     * <p>This method returns the list of sequents stored inside
     * this assertive code block.</p>
     *
     * @return A list of {@link Sequent Sequents}.
     */
    public final List<Sequent> getSequents() {
        return mySequents;
    }

    /**
     * <p>Checks if we have {@link Statement Statements} that we still
     * need to apply proof rules to./p>
     *
     * @return {@code true} if we have more {@link Statement Statements}
     * that needs to be processed, {@code false} otherwise.
     */
    public final boolean hasAnotherAssertion() {
        return (!myStatements.isEmpty());
    }

    /**
     * <p>This method removes the last {@link Statement} that is stored
     * inside this assertive code block.</p>
     *
     * @return A {@link Statement} representation object.
     */
    public final Statement removeLastSatement() {
        return myStatements.removeLast();
    }

    /**
     * <p>This method replaces the list of {@link Sequent Sequents}
     * in this assertive code with {@code sequents}.</p>
     *
     * @param sequents A new list of {@link Sequent Sequents}
     *                 for this assertive code block.
     */
    public final void setSequents(List<Sequent> sequents) {
        mySequents = sequents;
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return asString(0, 4);
    }

}