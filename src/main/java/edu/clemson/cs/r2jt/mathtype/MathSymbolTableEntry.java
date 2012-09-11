package edu.clemson.cs.r2jt.mathtype;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;

public class MathSymbolTableEntry {

    private final String myName;
    private final ResolveConceptualElement myDefiningElement;
    private final MTType myType;

    public MathSymbolTableEntry(String name,
            ResolveConceptualElement definingElement, MTType type) {
        myName = name;
        myDefiningElement = definingElement;
        myType = type;
    }

    public String getName() {
        return myName;
    }

    public ResolveConceptualElement getDefiningElement() {
        return myDefiningElement;
    }

    public MTType getType() {
        return myType;
    }
}
