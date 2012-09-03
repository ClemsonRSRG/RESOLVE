package edu.clemson.cs.r2jt.mathtype;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;

/**
 * <p>A <code>ScopeBuilder</code> represents a working mapping from unqualified 
 * symbol names represented as <code>String</code>s to information about those
 * symbols including their {@link MTType MTType} and the AST nodes that defined
 * them, when considered within the scope of a particular scope-introducing
 * AST node.  Each <code>ScopeBuilder</code> has a parent 
 * <code>{@link IdentifierResolver IdentifierResolve}</code> whose symbols are 
 * accessible to it, rooted in a <code>ModuleScopeBuilder</code>.</p>
 * 
 * <p>Note that <code>ScopeBuilder</code> has no public constructor.  
 * <code>ScopeBuilders</code>s are acquired through calls to some of the methods
 * of {@link MathSymbolTableBuilder MathSymbolTableBuilder}.</p>
 */
public class ScopeBuilder extends IdentifierResolver {

	protected final ResolveConceptualElement myDefiningElement;
	protected IdentifierResolver myParent;
	protected final List<ScopeBuilder> myChildren = 
		new LinkedList<ScopeBuilder>();
	
	protected final Map<String, MathSymbolTableEntry> myBindings = 
		new HashMap<String, MathSymbolTableEntry>();
	
	ScopeBuilder(ResolveConceptualElement definingElement,
			IdentifierResolver parent) {
		
		myDefiningElement = definingElement;
		myParent = parent;
	}
	
	/**
	 * <p>Returns the AST node that introduced this <code>ScopeBuilder</code>.
	 * </p>
	 * 
	 * @return The AST node that introduced this <code>ScopeBuilder</code>.
	 */
	public ResolveConceptualElement getDefiningElement() {
		return myDefiningElement;
	}
	
	IdentifierResolver getParent() {
		return myParent;
	}
	
	void setParent(IdentifierResolver parent) {
		myParent = parent;
	}
	
	void addChild(ScopeBuilder b) {
		myChildren.add(b);
	}
	
	List<ScopeBuilder> children() {
		return new LinkedList<ScopeBuilder>(myChildren);
	}
		
	Scope seal(MathSymbolTable finalTable) {
		return new Scope(myDefiningElement, myParent, myBindings);
	}
	
	/**
	 * <p>Modifies the current working scope to add a new binding for a
	 * symbol with an unqualified name, <code>name</code>, defined by the AST
	 * node <code>definingElement</code> and of type <code>type</code>.</p>
	 * 
	 * @param name The unqualified name of the symbol.
	 * @param definingElement The AST Node that introduced the symbol.
	 * @param type The declared type of the symbol.
	 * 
	 * @throws DuplicateSymbolException If such a symbol is already defined 
	 *             directly in the scope represented by this 
	 *             <code>ScopeBuilder</code>.  Note that this exception is not
	 *             thrown if the symbol is defined in a parent scope or an
	 *             imported module.
	 */
	public void addBinding(String name, 
				ResolveConceptualElement definingElement, MTType type) 
			throws DuplicateSymbolException {
		
		sanityCheckBindArguments(name, definingElement, type);
		
		myBindings.put(name, 
				new MathSymbolTableEntry(name, definingElement, type));
	}
	
	private void sanityCheckBindArguments(String name, 
				ResolveConceptualElement definingElement, MTType type) 
			throws DuplicateSymbolException {
		
		MathSymbolTableEntry curLocalEntry = myBindings.get(name);
		if (curLocalEntry != null) {
			throw new DuplicateSymbolException(curLocalEntry);
		}
		
		if (name == null || name.equals("")) {
			throw new IllegalArgumentException("Symbol table entry name must " +
					"be non-null and contain at least one character.");
		}
		
		if (type == null) {
			throw new IllegalArgumentException("Symbol table entry type must " +
					"be non-null.");
		}
	}
	
	@Override
	public MathSymbolTableEntry getInnermostBinding(String name, 
				MathSymbolTable.ImportStrategy importStrategy) 
			throws NoSuchSymbolException, DuplicateSymbolException {
		
		return getInnermostBinding(name, myBindings, myParent, importStrategy);
	}
	
	@Override
	public List<MathSymbolTableEntry> getAllBindings(String name,
				MathSymbolTable.ImportStrategy importStrategy) {
		return getAllBindings(name, myBindings, myParent, importStrategy);
	}
	
	@Override
	public void buildAllBindingsList(String symbol,
				List<MathSymbolTableEntry> accumulator, 
				MathSymbolTable.ImportStrategy importStrategy) {
		
		buildAllBindingsList(symbol, myBindings, myParent, accumulator,
				importStrategy);
	}
	
	/*
	 * The following helper methods factor out code shared between ScopeBuilder
	 * and Scope. 
	 */
		
	static MathSymbolTableEntry getInnermostBinding(String name,
				Map<String, MathSymbolTableEntry> bindings, 
				IdentifierResolver parent, 
				MathSymbolTable.ImportStrategy importStrategy)  
			throws NoSuchSymbolException, DuplicateSymbolException {
		
		MathSymbolTableEntry result = bindings.get(name);
		
		if (result == null) {
			result = parent.getInnermostBinding(name, importStrategy);
		}
		
		return result;
	}
	
	static List<MathSymbolTableEntry> getAllBindings(String name,
				Map<String, MathSymbolTableEntry> bindings,
				IdentifierResolver parent, 
				MathSymbolTable.ImportStrategy importStrategy) {
		
		List<MathSymbolTableEntry> bindingList = 
			new LinkedList<MathSymbolTableEntry>();
		
		buildAllBindingsList(name, bindings, parent, bindingList,
				importStrategy);
		
		return bindingList;
	}
	
	static void buildAllBindingsList(String symbol, 
				Map<String, MathSymbolTableEntry> bindings,
				IdentifierResolver parent,
				List<MathSymbolTableEntry> accumulator, 
				MathSymbolTable.ImportStrategy importStrategy) {
		
		if (bindings.containsKey(symbol)) {
			accumulator.add(bindings.get(symbol));
		}
		
		parent.buildAllBindingsList(symbol, accumulator, importStrategy);
	}
}
