package edu.clemson.cs.r2jt.mathtype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;

/**
 * <p>A <code>Scope</code> represents an immutable mapping from unqualified 
 * symbol names represented as <code>String</code>s to information about those
 * symbols including their {@link MTType MTType} and the AST nodes that defined
 * them, when considered within the scope of a particular scope-introducing
 * AST node.  Each <code>Scope</code> has a parent <code>Scope</code> whose
 * symbols are accessible to it, rooted in a <code>ModuleScope</code>.</p>
 * 
 * <p>Note that <code>Scope</code> has no public constructor.  
 * <code>Scope</code>s are acquired through calls to some of the methods of 
 * {@link MathSymbolTable MathSymbolTable}.</p>
 */
public class Scope extends IdentifierResolver {

    private final ResolveConceptualElement myDefiningElement;
    private final IdentifierResolver myParent;

    private final Map<String, MathSymbolTableEntry> myBindings =
            new HashMap<String, MathSymbolTableEntry>();

    Scope(ResolveConceptualElement definingElement, IdentifierResolver parent,
            Map<String, MathSymbolTableEntry> bindings) {

        myDefiningElement = definingElement;
        myParent = parent;
        myBindings.putAll(bindings);
    }

    /**
     * <p>Returns the AST node that introduced this <code>Scope</code>.</p>
     * 
     * @return The AST node that introduced this <code>Scope</code>.
     */
    public ResolveConceptualElement getDefiningElement() {
        return myDefiningElement;
    }

    @Override
    public MathSymbolTableEntry getInnermostBinding(String name,
            MathSymbolTable.ImportStrategy importStrategy)
            throws NoSuchSymbolException,
                DuplicateSymbolException {

        return ScopeBuilder.getInnermostBinding(name, myBindings, myParent,
                importStrategy);
    }

    @Override
    public List<MathSymbolTableEntry> getAllBindings(String name,
            MathSymbolTable.ImportStrategy importStrategy) {

        return ScopeBuilder.getAllBindings(name, myBindings, myParent,
                importStrategy);
    }

    @Override
    public void buildAllBindingsList(String symbol,
            List<MathSymbolTableEntry> accumulator,
            MathSymbolTable.ImportStrategy importStrategy) {

        ScopeBuilder.buildAllBindingsList(symbol, myBindings, myParent,
                accumulator, importStrategy);
    }
}
