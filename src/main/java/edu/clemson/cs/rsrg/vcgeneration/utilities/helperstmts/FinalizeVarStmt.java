/*
 * FinalizeVarStmt.java
 * ---------------------------------
 * Copyright (c) 2018
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
import edu.clemson.cs.rsrg.vcgeneration.proofrules.declarations.operationdecl.ProcedureDeclRule;

/**
 * <p>This is the class that builds a special kind of statement
 * that acts as a placeholder for finalizing a variable declaration. The only usage
 * of this class should be the {@link ProcedureDeclRule}. Since the user cannot
 * supply their own {@code _Finalize} statements, any instances of this
 * class will solely be created by the {@link VCGenerator}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class FinalizeVarStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The variable declaration we are applying the rule to.</p> */
    private final VarDec myVarDec;

    /**
     * <p>The symbol table entry representing program type associated
     * with the variable we are trying to finalize.</p>
     */
    private final SymbolTableEntry myVarTypeEntry;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an helper statement that indicates
     * finalization logic for a variable with known type happens here.</p>
     *
     * @param varDec A variable declaration with known type.
     * @param symbolTableEntry The program type entry associated with {@code varDec}.
     */
    public FinalizeVarStmt(VarDec varDec, SymbolTableEntry symbolTableEntry) {
        super(varDec.getLocation());
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
        sb.append("_Finalize(");
        sb.append(myVarDec.asString(0, innerIndentInc));
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

        FinalizeVarStmt that = (FinalizeVarStmt) o;

        if (!myVarDec.equals(that.myVarDec))
            return false;
        return myVarTypeEntry.equals(that.myVarTypeEntry);
    }

    /**
     * <p>This method returns the program variable we are
     * trying to finalize.</p>
     *
     * @return A {@link VarDec}.
     */
    public final VarDec getVarDec() {
        return myVarDec;
    }

    /**
     * <p>This method returns the symbol table entry associated
     * with the program type for the variable declaration we are
     * trying to finalize.</p>
     *
     * @return A {@link SymbolTableEntry}.
     */
    public final SymbolTableEntry getVarProgramTypeEntry() {
        return myVarTypeEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myVarDec.hashCode();
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
        return new FinalizeVarStmt((VarDec) myVarDec.clone(), myVarTypeEntry);
    }
}