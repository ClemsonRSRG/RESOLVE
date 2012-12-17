package edu.clemson.cs.r2jt.mathtype;

import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>A <code>ModuleScopeBuilder</code> refines 
 * {@link ScopeBuilder ScopeBuilder} with additional information specific to 
 * modules, such as a list of imports.</p>
 */
public class ModuleScopeBuilder extends ScopeBuilder implements ModuleScope {

    private final MathSymbolTableBuilder myWorkingSymbolTable;

    private final List<ModuleIdentifier> myImportedModules =
            new LinkedList<ModuleIdentifier>();

    ModuleScopeBuilder(TypeGraph g, ModuleDec definingElement, Scope parent,
            MathSymbolTableBuilder symbolTable) {
        super(symbolTable, g, definingElement, parent, new ModuleIdentifier(
                definingElement));

        myWorkingSymbolTable = symbolTable;
    }

    @Override
    public ModuleDec getDefiningElement() {
        return (ModuleDec) myDefiningElement;
    }

    @Override
    public ModuleIdentifier getModuleIdentifier() {
        return myRootModule;
    }

    /**
     * <p>Adds <code>i</code> to the list of modules imported by the module
     * who's scope this <code>ModuleScopeBuilder</code> represents.  Duplicate
     * imports or attempting to import the module represented itself will leave
     * the import list unmodified.</p>
     * 
     * @param i The module to import.
     */
    public void addImport(ModuleIdentifier i) {
        if (!myImportedModules.contains(i) && !myRootModule.equals(i)) {
            myImportedModules.add(i);
        }
    }

    @Override
    public boolean imports(ModuleIdentifier i) {
        return i.equals(myRootModule) || myImportedModules.contains(i);
    }

    @Override
    public List<ModuleIdentifier> getImports() {
        return new LinkedList<ModuleIdentifier>(myImportedModules);
    }

    @Override
    public FinalizedModuleScope seal(MathSymbolTable finalTable) {
        return new FinalizedModuleScope(myRootModule, myDefiningElement,
                myParent, myBindings, myImportedModules, finalTable);
    }
}
