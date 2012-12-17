package edu.clemson.cs.r2jt.mathtype;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

public class VariableReplacingVisitor extends MutatingVisitor {

    private final Map<String, MTType> mySubstitutions;

    public VariableReplacingVisitor(Map<String, String> substitutions,
            TypeGraph g) {

        mySubstitutions = convertToMTNamedMap(substitutions, g);
    }

    public VariableReplacingVisitor(Map<String, MTType> substitutions) {
        mySubstitutions = new HashMap<String, MTType>(substitutions);
    }

    private static Map<String, MTType> convertToMTNamedMap(
            Map<String, String> original, TypeGraph g) {

        Map<String, MTType> result = new HashMap<String, MTType>();

        for (Map.Entry<String, String> entry : original.entrySet()) {
            result.put(entry.getKey(), new MTNamed(g, entry.getValue()));
        }

        return result;
    }

    @Override
    public void endMTNamed(MTNamed t) {

        if (mySubstitutions.containsKey(t.name)) {
            try {
                getInnermostBinding(t.name);
                //This is bound to some inner scope
            }
            catch (NoSuchElementException e) {
                replaceWith(mySubstitutions.get(t.name));
            }
        }
    }

}
