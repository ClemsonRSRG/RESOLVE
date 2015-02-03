/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.typereasoning;

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.VariableReplacingVisitor;

public class EqualsPredicate implements TypeRelationshipPredicate {

    private final MTType myType1;
    private final MTType myType2;
    private final TypeGraph myTypeGraph;

    public EqualsPredicate(TypeGraph g, MTType type1, MTType type2) {
        myType1 = type1;
        myType2 = type2;
        myTypeGraph = g;
    }

    @Override
    public String toString() {
        return myType1 + " = " + myType2;
    }

    @Override
    public TypeRelationshipPredicate replaceUnboundVariablesInTypes(
            Map<String, String> substitions) {

        VariableReplacingVisitor renamer =
                new VariableReplacingVisitor(substitions, myTypeGraph);

        myType1.accept(renamer);
        MTType newType1 = renamer.getFinalExpression();

        renamer = new VariableReplacingVisitor(substitions, myTypeGraph);

        myType2.accept(renamer);
        MTType newType2 = renamer.getFinalExpression();

        return new EqualsPredicate(myTypeGraph, newType1, newType2);
    }

    @Override
    public boolean canBeDemonstratedStatically(MTType canonical1,
            MTType canonical2, Map<String, MTType> typeBindings,
            Map<String, Exp> expressionBindings) {

        MTType substituted1 =
                myType1.getCopyWithVariablesSubstituted(typeBindings);
        MTType substituted2 =
                myType2.getCopyWithVariablesSubstituted(typeBindings);

        //TODO : This was not well considered, it just made some fun stuff work
        //       out right.  So think about if it's ok to make this a "subset
        //       of" predicate rather than an "equals" predicate.

        return myTypeGraph.isSubtype(substituted1, substituted2);
        //return substituted1.equals(substituted2);
    }
}