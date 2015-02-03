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

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

public class VariableReplacingVisitor extends MutatingVisitor {

    private final Map<String, MTType> mySubstitutions;

    public VariableReplacingVisitor(Map<String, String> substitutions,
            TypeGraph g) {

        mySubstitutions = convertToMTNamedMap(substitutions, g);
    }

    public VariableReplacingVisitor(Map<String, MTType> substitutions) {
        mySubstitutions = new HashMap<String, MTType>(substitutions);
    }

    private static Map<String, MTType> convertToMTNamedMap(
            Map<String, String> original, TypeGraph g) {

        Map<String, MTType> result = new HashMap<String, MTType>();

        for (Map.Entry<String, String> entry : original.entrySet()) {
            result.put(entry.getKey(), new MTNamed(g, entry.getValue()));
        }

        return result;
    }

    @Override
    public void endMTNamed(MTNamed t) {

        if (mySubstitutions.containsKey(t.name)) {
            try {
                getInnermostBinding(t.name);
                //This is bound to some inner scope
            }
            catch (NoSuchElementException e) {
                replaceWith(mySubstitutions.get(t.name));
            }
        }
    }

}
