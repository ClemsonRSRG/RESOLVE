/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

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
