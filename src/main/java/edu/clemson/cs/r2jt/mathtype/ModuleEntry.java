package edu.clemson.cs.r2jt.mathtype;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;

public abstract class ModuleEntry extends SymbolTableEntry {

    public ModuleEntry(String name, ResolveConceptualElement definingElement) {
        super(name, definingElement, ModuleIdentifier.GLOBAL);
    }
}
