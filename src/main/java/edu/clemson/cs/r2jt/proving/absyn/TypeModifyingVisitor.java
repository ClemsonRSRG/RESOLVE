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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
package edu.clemson.cs.r2jt.proving.absyn;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.clemson.cs.r2jt.typeandpopulate.MTNamed;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;

public class TypeModifyingVisitor extends MutatingVisitor {

    private final Map<MTType, MTType> mySubstitutions =
            new HashMap<MTType, MTType>();

    public TypeModifyingVisitor(Map<MTType, MTType> substitutions) {
        mySubstitutions.putAll(substitutions);
    }

    public void mutateEndPExp(PExp e) {

        PExp finalValue = e;
        MTType eType = e.getType();
        MTType eTypeValue = e.getTypeValue();

        MTType typeReplacement = mySubstitutions.get(e.getType());
        if (eType instanceof MTNamed) {
            try {
                getInnermostBinding(((MTNamed) eType).name);
                //The type name is bound to some shadowing scope, so we don't
                //want to apply the replacement
                typeReplacement = null;
            }
            catch (NoSuchElementException nsee) {}
        }

        if (typeReplacement != null) {
            finalValue = finalValue.withTypeReplaced(typeReplacement);
        }

        MTType typeValueReplacement = mySubstitutions.get(e.getType());
        if (eTypeValue instanceof MTNamed) {
            try {
                getInnermostBinding(((MTNamed) eTypeValue).name);
                //The type name is bound to some shadowing scope, so we don't
                //want to apply the replacement
                typeValueReplacement = null;
            }
            catch (NoSuchElementException nsee) {}
        }

        if (typeValueReplacement != null) {
            finalValue = finalValue.withTypeValueReplaced(typeReplacement);
        }

        if (finalValue != e) {
            //We do this once at the end both for efficiency and fear that 
            //mutating visitor might break down if we do a double-replace
            replaceWith(e);
        }
    }
}
