/**
 * MathTypeTheoremAST.java
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
package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@code MathTypeTheoremAST} is a theorem that allows users to
 * statically establish relationships among {@code MTType}s and other
 * classes/sets that are otherwise unable to be automatically inferred by
 * typechecking alone.
 */
public class MathTypeTheoremAST extends DeclAST {

    private final List<MathVariableAST> myUniversalVars =
            new ArrayList<MathVariableAST>();
    private ExprAST myAssertion;

    public MathTypeTheoremAST(Token start, Token stop, Token name,
            List<MathVariableAST> universals, ExprAST assertion) {
        super(start, stop, name);
        myAssertion = assertion;
        myUniversalVars.addAll(universals);
    }

    public List<MathVariableAST> getUniversalVariables() {
        return myUniversalVars;
    }

    public ExprAST getAssertion() {
        return myAssertion;
    }
}