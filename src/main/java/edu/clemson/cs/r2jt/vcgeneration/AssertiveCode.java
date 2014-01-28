/**
 * AssertiveCode.java
 * ---------------------------------
 * Copyright (c) 2014
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
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramVariableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO: Write a description of this module
 */
public class AssertiveCode {

    // ===========================================================
    // Global Variables 
    // ===========================================================

    // Compile Environment
    private CompileEnvironment myInstanceEnvironment;

    // Free Variables
    List<ProgramVariableEntry> myFreeVars;

    // Verification Statements
    List<VerificationStatement> myVerificationStmtList;

    // Final Confirm Statement
    private Exp myConfirm;

    // ===========================================================
    // Constructors
    // ===========================================================

    public AssertiveCode(CompileEnvironment env) {
        myInstanceEnvironment = env;
        myFreeVars = new ArrayList<ProgramVariableEntry>();
        myVerificationStmtList = new ArrayList<VerificationStatement>();
        myConfirm = Exp.getTrueVarExp(env.getTypeGraph());
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Add the <code>Exp</code> containing a new assumes
     * clause.</p>
     *
     * @param e The corresponding assume <code>Exp</code>.
     */
    public void addAssume(Exp e) {
        // Creates a new AssumeStmt
        AssumeStmt assume = new AssumeStmt();
        assume.setAssertion(e);

        // Adds the assume to our list of verification statements
        addCode(assume);
    }

    /**
     * <p>Add a new <code>VerificationStatement</code> to the list</p>
     *
     * @param stmt The corresponding <code>Statement</code>.
     */
    public void addCode(Statement stmt) {
        myVerificationStmtList.add(new VerificationStatement(
                VerificationStatement.CODE, stmt));
    }

    /**
     * <p>Add the <code>Exp</code> containing a new confirm
     * clause.</p>
     *
     * @param e The corresponding confirm <code>Exp</code>.
     */
    public void addConfirm(Exp e) {
        // Creates a new ConfirmStmt
        ConfirmStmt confirm = new ConfirmStmt();
        confirm.setAssertion(e);

        // Adds the confirm to our list of verification statements
        addCode(confirm);
    }

    /**
     * <p>Add the <code>ProgramVariableEntry</code> containing the name
     * and type of a variable.</p>
     *
     * @param pve The corresponding <code>ProgramVariableEntry</code>
     *            stored in the symbol table.
     */
    public void addFreeVar(ProgramVariableEntry pve) {
        // Adds the variable entry into our free variable list
        // if it isn't in our list already.
        if (!myFreeVars.contains(pve)) {
            myFreeVars.add(pve);
        }
    }

    /**
     * <p>Add a Remember statement to the list</p>
     */
    public void addRemember() {
        myVerificationStmtList.add(new VerificationStatement(
                VerificationStatement.REMEMBER, null));
    }

    /**
     * <p>Loop through the list of <code>Statement</code> to the
     * list of verification statements</p>
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
     * <p>Returns the final confirm statement.</p>
     *
     * @return <code>Exp</code> confirm clause.
     */
    public Exp getFinalConfirm() {
        return Exp.copy(myConfirm);
    }

    /**
     * <p>Returns the last <code>VerificationStatement</code> from the list</p>
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
     * <p>Checks if the assertion list is empty or not</p>
     *
     * @return Boolean
     */
    public boolean hasAnotherAssertion() {
        return (!myVerificationStmtList.isEmpty());
    }

    /**
     * <p>Set the final confirm clause</p>
     *
     * @param confirm <code>Exp</code> containing the final confirm clause.
     */
    public void setFinalConfirm(Exp confirm) {
        // By default it is already TrueVarExp
        if (confirm != null) {
            myConfirm = confirm;
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================
}
