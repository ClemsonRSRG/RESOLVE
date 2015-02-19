/**
 * FinalizedScope.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;

/**
 * <p>A <code>FinalizedScope</code> is an immutable realization of 
 * <code>Scope</code>.</p>
 * 
 * <p>Note that <code>FinalizedScope</code> has no public constructor.  
 * <code>FinalizedScope</code>s are acquired through calls to some of the 
 * methods of {@link MathSymbolTable}.</p>
 */
public class FinalizedScope extends SyntacticScope {

    /* In answer to your question, despite not adding or modifying any behavior,
     * this class exists separately from ConcreteIdentifierResolver because it
     * adds SEMANTIC information--namely, the invariant of immutability, which
     * can not be tied to ConcreteIdentifierResolver since ScopeBuilder must
     * inherit from it and be mutable.
     */
    FinalizedScope(MathSymbolTable source, ResolveAST definingElement,
            ModuleIdentifier enclosingModule, Scope parent,
            BaseSymbolTable bindings) {

        super(source, definingElement, parent, enclosingModule,
                new BaseSymbolTable(bindings));
    }
}
