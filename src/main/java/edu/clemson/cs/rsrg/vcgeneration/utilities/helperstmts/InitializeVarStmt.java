/*
 * InitializeVarStmt.java
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
package edu.clemson.cs.rsrg.vcgeneration.utilities.helperstmts;

import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;

/**
 * <p>
 * This is the class that builds a special kind of statement that acts as a
 * placeholder for
 * initialize a variable declaration. Since the user cannot supply their own
 * {@code _Initialize}
 * statements, any instances of this class will solely be created by the
 * {@link VCGenerator} and/or
 * by our various different {@code proof rules}.
 * </p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class InitializeVarStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A flag that indicates whether or not we have a generic program variable.
     * </p>
     */
    private final boolean myIsGenericVarFlag;

    /**
     * <p>
     * The variable declaration we are applying the rule to.
     * </p>
     */
    private final VarDec myVarDec;

    /**
     * <p>
     * The symbol table entry representing program type associated with the
     * variable we are trying to
     * initialize.
     * </p>
     */
    private final SymbolTableEntry myVarTypeEntry;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs an helper statement that indicates initialization logic
     * for a variable happens
     * here.
     * </p>
     *
     * @param varDec A variable declaration.
     * @param symbolTableEntry The program type entry associated with
     *        {@code varDec}.
     * @param isGenericVar A flag that indicates if this is variable with
     *        generic program type.
     */
    public InitializeVarStmt(VarDec varDec, SymbolTableEntry symbolTableEntry,
            boolean isGenericVar) {
        super(varDec.getLocation());
        myIsGenericVarFlag = isGenericVar;
        myVarDec = varDec;
        myVarTypeEntry = symbolTableEntry;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("_Initialize(");
        sb.append(myVarDec.getName().asString(0, innerIndentInc));
        sb.append(" : ");
        sb.append(myVarTypeEntry.getName());
        sb.append(");");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        InitializeVarStmt that = (InitializeVarStmt) o;

        if (myIsGenericVarFlag != that.myIsGenericVarFlag)
            return false;
        if (!myVarDec.equals(that.myVarDec))
            return false;
        return myVarTypeEntry.equals(that.myVarTypeEntry);
    }

    /**
     * <p>
     * This method returns the program variable we are trying to initialize.
     * </p>
     *
     * @return A {@link VarDec}.
     */
    public final VarDec getVarDec() {
        return myVarDec;
    }

    /**
     * <p>
     * This method returns the symbol table entry associated with the program
     * type for the variable
     * declaration we are trying to initialize.
     * </p>
     *
     * @return A {@link SymbolTableEntry}.
     */
    public final SymbolTableEntry getVarProgramTypeEntry() {
        return myVarTypeEntry;
    }

    /**
     * <p>
     * This method returns whether or not the program variable we are trying to
     * initialize has a
     * generic program type.
     * </p>
     *
     * @return {@code true} if has generic program type, {@code false}
     *         otherwise.
     */
    public final boolean isGenericVar() {
        return myIsGenericVarFlag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = (myIsGenericVarFlag ? 1 : 0);
        result = 31 * result + myVarDec.hashCode();
        result = 31 * result + myVarTypeEntry.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Statement copy() {
        return new InitializeVarStmt((VarDec) myVarDec.clone(), myVarTypeEntry,
                myIsGenericVarFlag);
    }
}
