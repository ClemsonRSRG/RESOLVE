package edu.clemson.cs.r2jt.typeandpopulate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;

/**
 * <p>A <code>FinalizedModuleScope</code> is an immutable realization of 
 * {@link ModuleScope ModuleScope}.</p>
 * 
 * <p>Note that <code>FinalizedModuleScope</code> has no public constructor.  
 * <code>FinalizedModuleScope</code>s are acquired through calls to some of the 
 * methods of {@link MathSymbolTable MathSymbolTable}.</p>
 */
public class FinalizedModuleScope extends FinalizedScope implements ModuleScope {

    private final List<ModuleIdentifier> myImportedModules;
    private final List<SymbolTableEntry> myFormalParameters;
    private final MathSymbolTable mySymbolTable;

    FinalizedModuleScope(ModuleIdentifier module,
            ResolveConceptualElement definingElement, Scope parent,
            BaseSymbolTable bindings, List<ModuleIdentifier> importedModules,
            List<SymbolTableEntry> formalParameters, MathSymbolTable symbolTable) {

        super(symbolTable, definingElement, module, parent, bindings);

        myImportedModules = new LinkedList<ModuleIdentifier>(importedModules);
        myFormalParameters = new LinkedList<SymbolTableEntry>(formalParameters);
        mySymbolTable = symbolTable;
    }

    @Override
    public ModuleDec getDefiningElement() {
        return (ModuleDec) myDefiningElement;
    }

    @Override
    public ModuleIdentifier getModuleIdentifier() {
        return myRootModule;
    }

    @Override
    public boolean imports(ModuleIdentifier i) {
        return i.equals(myRootModule) || myImportedModules.contains(i);
    }

    @Override
    public List<ModuleIdentifier> getImports() {
        return new LinkedList<ModuleIdentifier>(myImportedModules);
    }
}
