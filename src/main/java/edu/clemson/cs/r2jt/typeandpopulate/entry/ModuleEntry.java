package edu.clemson.cs.r2jt.typeandpopulate.entry;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;

public abstract class ModuleEntry extends SymbolTableEntry {

    public ModuleEntry(String name, ResolveConceptualElement definingElement) {
        super(name, definingElement, ModuleIdentifier.GLOBAL);
    }
}
