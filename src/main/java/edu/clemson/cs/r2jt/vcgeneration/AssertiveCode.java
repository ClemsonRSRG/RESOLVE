/*
 * AssertiveCode.java
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
package edu.clemson.cs.r2jt.vcgeneration;

/*
 * Libraries
 */
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;

import java.util.*;

/**
 * TODO: Write a description of this module
 */
public class AssertiveCode {

    // ===========================================================
    // Global Variables
    // ===========================================================

    /**
     * <p>
     * Our current final confirm statement.
     * </p>
     */
    private ConfirmStmt myConfirm;

    /**
     * <p>
     * The list of free variables.
     * </p>
     */
    private List<Exp> myFreeVars;

    /**
     * <p>
     * The <code>ResolveConceptualElement</code> that created this object.
     * </p>
     */
    private ResolveConceptualElement myInstantiatingElement;

    /**
     * <p>
     * List of verification statements that we need to apply proof rules to./p>
     */
    private List<VerificationStatement> myVerificationStmtList;

    // ===========================================================
    // Constructors
    // ===========================================================

    public AssertiveCode(CompileEnvironment env,
            ResolveConceptualElement instantiatingElement) {
        myConfirm = new ConfirmStmt(null, Exp.getTrueVarExp(env.getTypeGraph()),
                true);
        myFreeVars = new ArrayList<Exp>();
        myVerificationStmtList = new ArrayList<VerificationStatement>();
        myInstantiatingElement = instantiatingElement;
    }

    public AssertiveCode(AssertiveCode old) {
        myConfirm = old.myConfirm.clone();
        myFreeVars = new ArrayList<Exp>();
        for (Exp exp : old.myFreeVars) {
            myFreeVars.add(Exp.copy(exp));
        }
        myVerificationStmtList = new ArrayList<VerificationStatement>();
        for (VerificationStatement stmt : old.myVerificationStmtList) {
            myVerificationStmtList.add((VerificationStatement) stmt.clone());
        }
        myInstantiatingElement = old.myInstantiatingElement;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Add the <code>Exp</code> containing a new assumes clause.
     * </p>
     *
     * @param l The location for this assume clause.
     * @param e The corresponding assume <code>Exp</code>.
     * @param b Boolean to determine if this is a stipulate assume clause or
     *        not.
     */
    public void addAssume(Location l, Exp e, boolean b) {
        // Creates a new AssumeStmt
        AssumeStmt assume = new AssumeStmt(l, e, b);

        // Adds the assume to our list of verification statements
        addCode(assume);
    }

    /**
     * <p>
     * Add the changing clause to the list
     * </p>
     */
    public void addChange(List<VariableExp> changeList) {
        myVerificationStmtList.add(new VerificationStatement(
                VerificationStatement.CHANGE, changeList));
    }

    /**
     * <p>
     * Add a new <code>VerificationStatement</code> to the list
     * </p>
     *
     * @param stmt The corresponding <code>Statement</code>.
     */
    public void addCode(Statement stmt) {
        myVerificationStmtList.add(
                new VerificationStatement(VerificationStatement.CODE, stmt));
    }

    /**
     * <p>
     * Add the <code>Exp</code> containing a new confirm clause.
     * </p>
     *
     * @param l The location for this confirm clause.
     * @param e The corresponding confirm <code>Exp</code>.
     * @param b Boolean to determine if we simplify this confirm clause or not.
     */
    public void addConfirm(Location l, Exp e, boolean b) {
        // Creates a new ConfirmStmt
        ConfirmStmt confirm = new ConfirmStmt(l, e, b);

        // Adds the confirm to our list of verification statements
        addCode(confirm);
    }

    /**
     * <p>
     * Add the <code>Exp</code> containing the name of the variable.
     * </p>
     *
     * @param var The name of the variable.
     */
    public void addFreeVar(Exp var) {
        // Check our list looking for var
        boolean inFreeVar = false;
        for (Exp freeVar : myFreeVars) {
            if (freeVar.equals(var)) {
                inFreeVar = true;
                break;
            }
        }

        // Adds the variable into our free variable list
        // if it isn't in our list already.
        if (!inFreeVar) {
            myFreeVars.add(var);
        }
    }

    /**
     * <p>
     * Loop through the list of <code>Statement</code> to the list of
     * verification statements
     * </p>
     *
     * @param statementList The corresponding <code>Statement</code>.
     */
    public void addStatements(List<Statement> statementList) {
        // Loop
        Iterator<Statement> it = statementList.iterator();
        while (it.hasNext()) {
            addCode(it.next());
        }
    }

    /**
     * <p>
     * Loop through the list of <code>VarDec</code> and add them to the list of
     * verification
     * statements
     * </p>
     *
     * @param variableList List of the all variables as <code>VarDec</code>.
     */
    public void addVariableDecs(List<VarDec> variableList) {
        Iterator<VarDec> i = variableList.iterator();
        while (i.hasNext()) {
            VarDec dec = i.next();
            myVerificationStmtList.add(new VerificationStatement(
                    VerificationStatement.VARIABLE, dec.clone()));
        }
    }

    /**
     * <p>
     * Returns a string containing the assertion formatted for output.
     * </p>
     *
     * @return Readable <code>String</code> form of the assertion.
     */
    public String assertionToString() {
        // Create the return string
        String retStr = new String();
        retStr = retStr.concat("\n");

        // Free variables
        retStr = retStr.concat("Free Variables: \n");
        Iterator<Exp> freeVarIt = myFreeVars.iterator();
        while (freeVarIt.hasNext()) {
            Exp current = freeVarIt.next();
            retStr = retStr.concat(current.toString(0) + " : "
                    + current.getMathType().toString());

            if (freeVarIt.hasNext())
                retStr = retStr.concat(", ");
            else
                retStr = retStr.concat("\n");
        }
        retStr = retStr.concat("\n");

        // Verification Statements
        Iterator<VerificationStatement> vsIt =
                myVerificationStmtList.iterator();
        while (vsIt.hasNext()) {
            VerificationStatement current = vsIt.next();

            // All possible types of verification statements
            switch (current.getType()) {
            // Change Verification Statements
            case VerificationStatement.CHANGE:
                retStr = retStr.concat("      Change ");
                List<VariableExp> varList =
                        (List<VariableExp>) current.getAssertion();
                for (int i = 0; i < varList.size(); i++) {
                    retStr = retStr.concat(varList.get(i).toString(0));
                    if (i != varList.size() - 1) {
                        retStr = retStr.concat(", ");
                    }
                }
                break;
            // Code Verification Statements
            case VerificationStatement.CODE:
                retStr = retStr.concat(
                        ((Statement) current.getAssertion()).toString(6));
                break;
            // Variable Verification Statements
            case VerificationStatement.VARIABLE:
                VarDec varDec = (VarDec) current.getAssertion();
                retStr = retStr.concat("      Var " + varDec.getName().getName()
                        + " : "
                        + ((NameTy) varDec.getTy()).getName().getName());
                break;
            }
            retStr = retStr.concat(";\n");
        }

        retStr = retStr.concat(myConfirm.toString(6) + ";");

        return retStr;
    }

    /**
     * <p>
     * Returns a deep copy of the final confirm statement.
     * </p>
     *
     * @return <code>ConfirmStmt</code> confirm clause.
     */
    public ConfirmStmt getFinalConfirm() {
        return myConfirm.clone();
    }

    /**
     * <p>
     * Returns the instantiating element that created this object.
     * </p>
     *
     * @return <code>ResolveConceptualElement</code>
     */
    public ResolveConceptualElement getInstantiatingElement() {
        return myInstantiatingElement;
    }

    /**
     * <p>
     * Returns the free variable with the specified name.
     * </p>
     *
     * @param name Name of the variable.
     * @param isGlobal Check all global free variables.
     *
     * @return The free variable in <code>VarExp</code> form.
     */
    public Exp getFreeVar(PosSymbol name, boolean isGlobal) {
        Exp exp = null;
        for (Exp v : myFreeVars) {
            // Global free variables
            if (isGlobal && v instanceof DotExp) {
                DotExp dotExp = (DotExp) v;
                Exp lastExp = dotExp.getSegments()
                        .get(dotExp.getSegments().size() - 1);
                if (lastExp.containsVar(name.getName(), false)) {
                    exp = v;
                    break;
                }
            }
            // Local free variables
            else if (v instanceof VarExp
                    && ((VarExp) v).getName().equals(name.getName())) {
                exp = v;
                break;
            }
        }

        return exp;
    }

    /**
     * <p>
     * Returns the last <code>VerificationStatement</code> from the list
     * </p>
     *
     * @return <code>VerificationStatement</code> from the list
     */
    public VerificationStatement getLastAssertion() {
        if (!myVerificationStmtList.isEmpty())
            return myVerificationStmtList
                    .remove(myVerificationStmtList.size() - 1);
        else
            return new VerificationStatement();
    }

    /**
     * <p>
     * Checks if the assertion list is empty or not
     * </p>
     *
     * @return Boolean
     */
    public boolean hasAnotherAssertion() {
        return (!myVerificationStmtList.isEmpty());
    }

    /**
     * <p>
     * Set the final confirm clause
     * </p>
     *
     * @param confirm <code>Exp</code> containing the final confirm clause.
     * @param simplify True if we can simplify the confirm, false otherwise.
     */
    public void setFinalConfirm(Exp confirm, boolean simplify) {
        // By default it is already TrueVarExp
        if (confirm != null) {
            myConfirm =
                    new ConfirmStmt(confirm.getLocation(), confirm, simplify);
        }
    }
}
