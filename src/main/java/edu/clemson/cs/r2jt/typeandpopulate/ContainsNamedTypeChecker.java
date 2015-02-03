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

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class ContainsNamedTypeChecker extends BoundVariableVisitor {

    private final Set<String> myNames = new HashSet<String>();

    private boolean myResult = false;

    /**
     * <p>Result in <code>true</code> if one of the given names appears in the
     * checked type.  The set will not be changed, but it will be read from
     * so it must not change while checking runs.</p>
     * @param names 
     */
    public ContainsNamedTypeChecker(Set<String> names) {
        myNames.addAll(names);
    }

    /**
     * <p>Results in <code>true</cdoe> if the given name appears in the checked
     * type.</p>
     * @param name 
     */
    public ContainsNamedTypeChecker(String name) {
        myNames.add(name);
    }

    public boolean getResult() {
        return myResult;
    }

    @Override
    public void endMTNamed(MTNamed named) {
        try {
            getInnermostBinding(named.name);
        }
        catch (NoSuchElementException nsee) {
            myResult = myResult || myNames.contains(named.name);
        }
    }
}
