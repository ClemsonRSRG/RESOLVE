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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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

public class IsInPredicate implements TypeRelationshipPredicate {

    private final MTType myElement;
    private final MTType myDeclaredType;
    private final TypeGraph myTypeGraph;

    public IsInPredicate(TypeGraph g, MTType element, MTType declaredType) {
        myElement = element;
        myDeclaredType = declaredType;
        myTypeGraph = g;
    }

    @Override
    public String toString() {
        return myElement + " : " + myDeclaredType;
    }

    @Override
    public TypeRelationshipPredicate replaceUnboundVariablesInTypes(
            Map<String, String> substitions) {

        VariableReplacingVisitor renamer =
                new VariableReplacingVisitor(substitions, myTypeGraph);

        myElement.accept(renamer);
        MTType newType1 = renamer.getFinalExpression();

        renamer = new VariableReplacingVisitor(substitions, myTypeGraph);

        myDeclaredType.accept(renamer);
        MTType newType2 = renamer.getFinalExpression();

        return new IsInPredicate(myTypeGraph, newType1, newType2);
    }

    @Override
    public boolean canBeDemonstratedStatically(MTType canonical1,
            MTType canonical2, Map<String, MTType> typeBindings,
            Map<String, Exp> expressionBindings) {

        MTType substitutedElement =
                myElement.getCopyWithVariablesSubstituted(typeBindings);
        MTType substitutedDeclaredType =
                myDeclaredType.getCopyWithVariablesSubstituted(typeBindings);

        return myTypeGraph.isKnownToBeIn(substitutedElement,
                substitutedDeclaredType);
    }
}
