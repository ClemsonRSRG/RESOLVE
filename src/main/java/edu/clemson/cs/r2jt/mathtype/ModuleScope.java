package edu.clemson.cs.r2jt.mathtype;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;

/**
 * <p>A <code>ModuleScope</code> refines {@link Scope Scope} with additional
 * information specific to modules, such as a list of imports.</p>
 */
public class ModuleScope extends Scope implements ModuleScopeInterface {

    private final ModuleIdentifier myModuleName;
    private final List<ModuleIdentifier> myImportedModules;
    private final MathSymbolTable mySymbolTable;

    ModuleScope(ModuleIdentifier module,
            ResolveConceptualElement definingElement,
            IdentifierResolver parent,
            Map<String, MathSymbolTableEntry> bindings,
            List<ModuleIdentifier> importedModules, MathSymbolTable symbolTable) {

        super(definingElement, parent, bindings);

        myModuleName = module;
        myImportedModules = new LinkedList<ModuleIdentifier>(importedModules);
        mySymbolTable = symbolTable;
    }

    @Override
    public ModuleIdentifier getModuleIdentifier() {
        return myModuleName;
    }

    @Override
    public boolean imports(ModuleIdentifier i) {
        return i.equals(myModuleName) || myImportedModules.contains(i);
    }

    @Override
    public List<ModuleIdentifier> getImports() {
        return new LinkedList<ModuleIdentifier>(myImportedModules);
    }

    @Override
    public MathSymbolTableEntry getInnermostBinding(String name,
            MathSymbolTable.ImportStrategy importStrategy)
            throws NoSuchSymbolException,
                DuplicateSymbolException {

        MathSymbolTableEntry binding;

        try {
            binding = super.getInnermostBinding(name, importStrategy);
        }
        catch (NoSuchSymbolException e) {
            binding =
                    ModuleScopeBuilder.getBindingInImports(name,
                            importStrategy, myImportedModules, mySymbolTable,
                            myModuleName);
        }

        return binding;
    }

    @Override
    public List<MathSymbolTableEntry> getAllBindings(String name,
            MathSymbolTable.ImportStrategy importStrategy) {

        List<MathSymbolTableEntry> result =
                super.getAllBindings(name, importStrategy);
        ModuleScopeBuilder.buildAllBindingsFromImports(name, result,
                importStrategy, myImportedModules, mySymbolTable, myModuleName);

        return result;
    }

    @Override
    public void buildAllBindingsList(String symbol,
            List<MathSymbolTableEntry> accumulator,
            MathSymbolTable.ImportStrategy importStrategy) {

        super.buildAllBindingsList(symbol, accumulator, importStrategy);
        ModuleScopeBuilder.buildAllBindingsFromImports(symbol, accumulator,
                importStrategy, myImportedModules, mySymbolTable, myModuleName);
    }
}
