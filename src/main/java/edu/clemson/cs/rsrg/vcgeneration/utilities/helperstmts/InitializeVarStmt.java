/*
 * InitializeVarStmt.java
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
package edu.clemson.cs.rsrg.vcgeneration.utilities.helperstmts;

import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;

/**
 * <p>This is the class that builds a special kind of statement
 * that acts as a placeholder for initialize a variable declaration. The only usage
 * of this class should be the {@link VCGenerator}. Since the user cannot
 * supply their own {@code InitializeVarStmt} statements, any instances of this
 * class will solely be created by the {@link VCGenerator}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class InitializeVarStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The variable declaration we are applying the rule to.</p> */
    private final VarDec myVarDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an helper statement that indicates
     * initialization logic for a variable happens here.</p>
     *
     * @param varDec The variable declaration we are applying the
     *               rule to.
     */
    public InitializeVarStmt(VarDec varDec) {
        super(varDec.getLocation());
        myVarDec = varDec;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return 0;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Statement copy() {
        return new InitializeVarStmt((VarDec) myVarDec.clone());
    }
}