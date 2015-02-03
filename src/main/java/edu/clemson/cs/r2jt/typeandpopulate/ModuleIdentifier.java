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
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.UsesItem;
import edu.clemson.cs.r2jt.data.ModuleID;

/**
 * <p>Identifies a particular module unambiguously.</p>
 * 
 * <p><strong>Note:</strong> Currently, we only permit one level of namespace.
 * But ultimately that will probably change (because, for example, at this
 * moment if there were two "Stack_Templates", we couldn't deal with that.  A
 * java class-path-like solution seems inevitable.  For the moment however, this
 * is just a wrapper around the string name of the module to facilitate changing
 * how we deal with modules later.</p>
 */
public class ModuleIdentifier implements Comparable<ModuleIdentifier> {

    public static final ModuleIdentifier GLOBAL = new ModuleIdentifier();

    private final String myName;
    private final boolean myGlobalFlag;

    private ModuleIdentifier() {
        myName = "GLOBAL";
        myGlobalFlag = true;
    }

    public ModuleIdentifier(ModuleID mid) {
        this(mid.getName().getName());
    }

    public ModuleIdentifier(ModuleDec m) {
        this(m.getName().getName());
    }

    public ModuleIdentifier(UsesItem i) {
        this(i.getName().getName());
    }

    public ModuleIdentifier(String s) {
        myName = s;
        myGlobalFlag = false;
    }

    public boolean equals(Object o) {
        boolean result = (o instanceof ModuleIdentifier);

        if (result) {
            result = ((ModuleIdentifier) o).myName.equals(myName);
        }

        return result;
    }

    public int hashCode() {
        return myName.hashCode();
    }

    @Override
    public int compareTo(ModuleIdentifier o) {
        return myName.compareTo(o.myName);
    }

    public String toString() {
        return myName;
    }

    public String fullyQualifiedRepresentation(String symbol) {
        return myName + "." + symbol;
    }
}
