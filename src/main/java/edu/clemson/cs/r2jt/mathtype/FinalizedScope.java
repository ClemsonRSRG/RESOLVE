package edu.clemson.cs.r2jt.mathtype;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;

/**
 * <p>A <code>FinalizedScope</code> is an immutable realization of 
 * <code>Scope</code>.</p>
 * 
 * <p>Note that <code>FinalizedScope</code> has no public constructor.  
 * <code>FinalizedScope</code>s are acquired through calls to some of the 
 * methods of {@link MathSymbolTable MathSymbolTable}.</p>
 */
public class FinalizedScope extends SyntacticScope {

    /* In answer to your question, despite not adding or modifying any behavior,
     * this class exists separately from ConcreteIdentifierResolver because it
     * adds SEMANTIC information--namely, the invariant of immutability, which
     * can not be tied to ConcreteIdentifierResolver since ScopeBuilder must
     * inherit from it and be mutable.
     */

    FinalizedScope(MathSymbolTable source,
            ResolveConceptualElement definingElement,
            ModuleIdentifier enclosingModule, Scope parent,
            BaseSymbolTable bindings) {

        super(source, definingElement, parent, enclosingModule,
                new BaseSymbolTable(bindings));
    }
}
