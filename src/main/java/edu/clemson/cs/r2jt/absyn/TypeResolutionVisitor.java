/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the Clemson University nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University. Contributors to the initial version are:
 * 
 * Steven Atkinson
 * Greg Kulczycki
 * Kunal Chopra
 * John Hunt
 * Heather Keown
 * Ben Markle
 * Kim Roche
 * Murali Sitaraman
 */
/*
 * TypeResolutionVisitor.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.data.Location;

public class TypeResolutionVisitor {

    public Type getCrossTypeExpType(CrossTypeExpression data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getArbitraryExpType(ArbitraryExpTy data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getImplicitTypeParameterExp(TypeAssertionExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getDoubleExpType(DoubleExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getBetweenExpType(BetweenExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getAlternativeExpType(AlternativeExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getProgramFunctionExpType(ProgramFunctionExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getCharExpType(CharExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getTupleTyType(TupleTy data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getDotExpType(DotExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getPrefixExpType(PrefixExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getFunctionExpType(FunctionExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getOldExpType(OldExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getProgramDoubleExpType(ProgramDoubleExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getIntegerExpType(IntegerExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getCartProdTyType(CartProdTy data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getProgramDotExpType(ProgramDotExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getEqualsExpType(EqualsExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getProgramIntegerExpType(ProgramIntegerExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getProgramCharExpType(ProgramCharExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getProgramStringExpType(ProgramStringExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getQuantExpType(QuantExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getStringExpType(StringExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getLambdaExpType(LambdaExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getArrayTyType(ArrayTy data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getIterativeExpType(IterativeExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getSetExpType(SetExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getVariableNameExpType(VariableNameExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getVarExpType(VarExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getProgramParamExpType(ProgramParamExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getAltItemExpType(AltItemExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getTypeFunctionExpType(TypeFunctionExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getProgramOpExpType(ProgramOpExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getVariableRecordExpType(VariableRecordExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getFieldExpType(FieldExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getNameTyType(NameTy data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getVariableDotExpType(VariableDotExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getUnaryMinusExpType(UnaryMinusExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getTupleExpType(TupleExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getOutfixExpType(OutfixExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getVariableArrayExpType(VariableArrayExp data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getConstructedTyType(ConstructedTy data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getFunctionTyType(FunctionTy data)
            throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getInfixExpType(InfixExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getIfExpType(IfExp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getRecordTyType(RecordTy data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public Type getGoalExpType(GoalExp data) throws TypeResolutionException {
        return null;
    }

    public Type getSuppositionExpType(SuppositionExp data)
            throws TypeResolutionException {
        return null;
    }

    public Type getDeductionExpType(DeductionExp data)
            throws TypeResolutionException {
        return null;
    }

    public Type getSuppositionDeductionExpType(SuppositionDeductionExp data)
            throws TypeResolutionException {
        return null;
    }

    public Type getProofDefinitionExpType(ProofDefinitionExp data)
            throws TypeResolutionException {
        return null;
    }

    public Type getJustifiedExpType(JustifiedExp data)
            throws TypeResolutionException {
        return null;
    }

    public Type getJustificationExpType(JustificationExp data)
            throws TypeResolutionException {
        return null;
    }

    public Type getHypDesigExpType(HypDesigExp data)
            throws TypeResolutionException {
        return null;
    }

    public Type getMathRefExpType(MathRefExp data)
            throws TypeResolutionException {
        return null;
    }

    /*
     * The methods below have been added to facilitate built-in expressions
     * and their associated typechecking
     */
    public Type getMathExpType(Exp data) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return null;
    }

    public boolean matchTypes(Location loc, Type t1, Type t2, boolean b1,
            boolean b2) throws TypeResolutionException {
        assert false : "This method should not be called.";
        return false;
    }

}
