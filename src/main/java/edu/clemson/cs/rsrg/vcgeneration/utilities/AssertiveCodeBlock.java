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
package edu.clemson.cs.rsrg.vcgeneration.utilities;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.parsing.data.BasicCapabilities;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;
import java.util.Deque;
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

    /** <p>Name of the {@link ResolveConceptualElement} that created this object.</p> */
    private final PosSymbol myBlockName;

    /** <p>Deque of branching condition(s) that generated this assertive code block.</p> */
    private final Deque<String> myBranchingConditions;

    /** <p>List of free variables.</p> */
    private final List<Exp> myFreeVars;

    /** <p>{@link ResolveConceptualElement} that created this object.</p> */
    private final ResolveConceptualElement myInstantiatingElement;

    /**
     * <p>List of {@link VerificationCondition VCs} we are trying
     * to prove.</p>
     */
    private List<VerificationCondition> myVCs;

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
     * @param name Name of the element that created this assertive
     *             code block.
     */
    public AssertiveCodeBlock(TypeGraph g, ResolveConceptualElement instantiatingElement, PosSymbol name) {
        myBlockName = name;
        myBranchingConditions = new LinkedList<>();
        myFreeVars = new LinkedList<>();
        myInstantiatingElement = instantiatingElement;
        myVCs = new LinkedList<>();
        myStatements = new LinkedList<>();
        myTypeGraph = g;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Adds a new branching condition detail for this assertive code block.</p>
     *
     * @param branchingLoc A {@link Location} that indicates where the
     *                     branching occurred.
     * @param branchingExpAsString The branching condition converted to a
     *                             mathematical {@link Exp}.
     * @param evalResult The results of the evaluation.
     */
    public final void addBranchingCondition(Location branchingLoc,
            String branchingExpAsString, boolean evalResult) {
        myBranchingConditions.push("Expression at " + branchingLoc + " is "
                + evalResult + ". [Exp: " + branchingExpAsString + "]");
    }

    /**
     * <p>Add the {@link Exp} containing the name of the free variable
     * if it is not already in our free variable list.</p>
     *
     * @param var A new variable.
     */
    public final void addFreeVar(Exp var) {
        if (!containsFreeVar(var)) {
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
        for (Exp current : myFreeVars) {
            sb.append(current.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append(" : ");
            sb.append(current.getMathType());
            sb.append("\n");
        }
        sb.append("\n");

        // Statements
        sb.append("Statements:\n");
        for (Statement statement : myStatements) {
            sb.append(statement.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("\n");
        }
        sb.append("\n");

        // VCs
        sb.append("VC(s):\n");
        Iterator<VerificationCondition> conditionIterator = myVCs.iterator();
        while (conditionIterator.hasNext()) {
            VerificationCondition vc = conditionIterator.next();
            sb.append(vc.asString(indentSize + innerIndentInc, innerIndentInc));

            if (conditionIterator.hasNext()) {
                sb.append("\n");
            }
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
                new AssertiveCodeBlock(myTypeGraph, myInstantiatingElement,
                        myBlockName);

        // Copy over any branching conditions
        newBlock.myBranchingConditions.addAll(myBranchingConditions);

        // YS: Collections.copy complains about source does not fit in dest,
        // it probably doesn't know it is a LinkedList, so we manually copy everything.
        for (Exp current : myFreeVars) {
            newBlock.myFreeVars.add(current.clone());
        }

        for (Statement statement : myStatements) {
            newBlock.myStatements.add(statement.clone());
        }

        for (VerificationCondition vc : myVCs) {
            newBlock.myVCs.add(vc.clone());
        }

        return newBlock;
    }

    /**
     * <p>Checks if there is already a free variable that matches the provided
     * expression.</p>
     *
     * @param var A variable expression to be checked.
     *
     * @return {@code true} if it contains this free var,
     * {@code false} otherwise.
     */
    public final boolean containsFreeVar(Exp var) {
        boolean contains = false;
        Iterator<Exp> freeVarIt = myFreeVars.iterator();
        while (freeVarIt.hasNext() && !contains) {
            Exp freeVar = freeVarIt.next();
            contains = freeVar.equivalent(var);
        }

        return contains;
    }

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

        AssertiveCodeBlock block = (AssertiveCodeBlock) o;

        return myBlockName.equals(block.myBlockName)
                && myBranchingConditions.equals(block.myBranchingConditions)
                && myFreeVars.equals(block.myFreeVars)
                && myInstantiatingElement.equals(block.myInstantiatingElement)
                && myVCs.equals(block.myVCs)
                && myStatements.equals(block.myStatements)
                && myTypeGraph.equals(block.myTypeGraph);
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
     * <p>This method returns a collection of branching conditions
     * that generated this assertive code block.</p>
     *
     * @return A {@link Deque} containing details on each condition.
     */
    public final Deque<String> getBranchingConditions() {
        return myBranchingConditions;
    }

    /**
     * <p>This method returns the name for the instantiating element that created
     * this assertive code block.</p>
     *
     * @return The name as a {@link PosSymbol}.
     */
    public final PosSymbol getName() {
        return myBlockName;
    }

    /**
     * <p>This method returns the list of {@code VCs} stored inside
     * this assertive code block.</p>
     *
     * @return A list of {@link VerificationCondition VCs}.
     */
    public final List<VerificationCondition> getVCs() {
        return myVCs;
    }

    /**
     * <p>The type graph containing all the type relationships.</p>
     *
     * @return The type graph for the compiler.
     */
    public final TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myBlockName.hashCode();
        result = 31 * result + myBranchingConditions.hashCode();
        result = 31 * result + myFreeVars.hashCode();
        result = 31 * result + myInstantiatingElement.hashCode();
        result = 31 * result + myVCs.hashCode();
        result = 31 * result + myStatements.hashCode();
        result = 31 * result + myTypeGraph.hashCode();

        return result;
    }

    /**
     * <p>Checks if we have {@link Statement Statements} that we still
     * need to apply proof rules to.</p>
     *
     * @return {@code true} if we have more {@link Statement Statements}
     * that needs to be processed, {@code false} otherwise.
     */
    public final boolean hasMoreStatements() {
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
     * <p>This method replaces the list of {@link VerificationCondition VCs}
     * in this assertive code with {@code vcs}.</p>
     *
     * @param vcs A new list of {@link VerificationCondition VCs}
     *            for this assertive code block.
     */
    public final void setVCs(List<VerificationCondition> vcs) {
        myVCs = vcs;
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