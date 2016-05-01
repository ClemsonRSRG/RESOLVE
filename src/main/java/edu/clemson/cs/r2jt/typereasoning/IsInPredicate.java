/**
 * IsInPredicate.java
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
