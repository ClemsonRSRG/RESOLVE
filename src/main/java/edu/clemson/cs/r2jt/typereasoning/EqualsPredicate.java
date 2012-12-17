package edu.clemson.cs.r2jt.typereasoning;

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.mathtype.MTType;
import edu.clemson.cs.r2jt.mathtype.VariableReplacingVisitor;

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