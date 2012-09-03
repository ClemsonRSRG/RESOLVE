package edu.clemson.cs.r2jt.mathtype;

import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.absyn.ModuleDec;

/**
 * <p>A <code>ModuleScopeBuilder</code> refines 
 * {@link ScopeBuilder ScopeBuilder} with additional information specific to 
 * modules, such as a list of imports.</p>
 */
public class ModuleScopeBuilder extends ScopeBuilder 
		implements ModuleScopeInterface {

	private final ModuleIdentifier myModule;
	private final MathSymbolTableBuilder myWorkingSymbolTable;
	
	private final List<ModuleIdentifier> myImportedModules =
		new LinkedList<ModuleIdentifier>();
	
	ModuleScopeBuilder(ModuleDec definingElement,
			IdentifierResolver parent, MathSymbolTableBuilder symbolTable) {
		super(definingElement, parent);
		
		myModule = new ModuleIdentifier(definingElement);
		myWorkingSymbolTable = symbolTable;
	}
	
	@Override
	public ModuleIdentifier getModuleIdentifier() {
		return myModule;
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
		if (!myImportedModules.contains(i) && !myModule.equals(i)) {
			myImportedModules.add(i);
		}
	}
	
	@Override
	public boolean imports(ModuleIdentifier i) {
		return i.equals(myModule) || myImportedModules.contains(i);
	}
	
	@Override
	public List<ModuleIdentifier> getImports() {
		return new LinkedList<ModuleIdentifier>(myImportedModules);
	}
	
	@Override
	public ModuleScope seal(MathSymbolTable finalTable) {
		return new ModuleScope(myModule, myDefiningElement, myParent,
				myBindings, myImportedModules, finalTable);
	}
	
	@Override
	public MathSymbolTableEntry getInnermostBinding(String name, 
				MathSymbolTable.ImportStrategy importStrategy) 
			throws NoSuchSymbolException, DuplicateSymbolException {
	
		MathSymbolTableEntry binding;
		
		try {
			binding = super.getInnermostBinding(name, importStrategy);
		}
		catch (NoSuchSymbolException e) {
			binding = getBindingInImports(name, importStrategy,
					myImportedModules, myWorkingSymbolTable, myModule);
		}
		
		return binding;
	}
	
	@Override
	public List<MathSymbolTableEntry> getAllBindings(String name,
			MathSymbolTable.ImportStrategy importStrategy) {
		
		List<MathSymbolTableEntry> result = super.getAllBindings(name,
				importStrategy);
		buildAllBindingsFromImports(name, result, importStrategy,
				myImportedModules, myWorkingSymbolTable, myModule);

		return result;
	}
	
	@Override
	public void buildAllBindingsList(String symbol,
				List<MathSymbolTableEntry> accumulator, 
				MathSymbolTable.ImportStrategy importStrategy) {
		
		super.buildAllBindingsList(symbol, accumulator, importStrategy);
		buildAllBindingsFromImports(symbol, accumulator, importStrategy,
				myImportedModules, myWorkingSymbolTable, myModule);
	}
	
	/*
	 * The following helper methods factor out code shared between 
	 * ModuleScopeBuilder and ModuleScope. 
	 */
	
	static MathSymbolTableEntry getBindingInImports(String name, 
				MathSymbolTable.ImportStrategy importStrategy, 
				List<ModuleIdentifier> importedModules, 
				ImportRepository importRepository,
				ModuleIdentifier module) 
			throws NoSuchSymbolException, DuplicateSymbolException {
	
		MathSymbolTableEntry binding;

		List<MathSymbolTableEntry> matches = 
			new LinkedList<MathSymbolTableEntry>();
		
		buildAllBindingsFromImports(name, matches, importStrategy,
				importedModules, importRepository, module);
		
		if (matches.isEmpty()) {
			throw new NoSuchSymbolException(name);
		}
		else if (matches.size() > 1) {
			throw new DuplicateSymbolException(name);
		}
		
		binding = matches.get(0);
		
		return binding;
	}
	
	static void buildAllBindingsFromImports(String symbol, 
				List<MathSymbolTableEntry> accumulator,
				MathSymbolTable.ImportStrategy importStrategy, 
				List<ModuleIdentifier> importedModules, 
				ImportRepository importRepository,
				ModuleIdentifier module) {
		
		if (importStrategy.considerImports()) {
			MathSymbolTable.ImportStrategy cascadingStrategy = 
				importStrategy.cascadingStrategy();
			
			IdentifierResolver importedModuleScope;
			for (ModuleIdentifier importedModule : importedModules) {
				try {
					importedModuleScope = 
						importRepository.getModuleScope(importedModule);
				}
				catch (NoSuchSymbolException nsse) {
					throw new IllegalStateException("Module '" + module + 
							"' imports non-existent module '" + importedModule + 
							"'.");
				}
				
				importedModuleScope.buildAllBindingsList(symbol, accumulator,
						cascadingStrategy);
			}
		}
	}
	
}
