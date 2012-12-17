package edu.clemson.cs.r2jt.typereasoning;

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.mathtype.MTType;

public interface TypeRelationshipPredicate {

    public TypeRelationshipPredicate replaceUnboundVariablesInTypes(
            Map<String, String> substitions);

    public boolean canBeDemonstratedStatically(MTType canonical1,
            MTType canonical2, Map<String, MTType> typeBindings,
            Map<String, Exp> expressionBindings);
}
