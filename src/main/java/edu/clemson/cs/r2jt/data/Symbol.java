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
package edu.clemson.cs.r2jt.data;

public class Symbol implements Comparable<Symbol> {

    // ===========================================================
    // Variables
    // ===========================================================

    private String name;

    private static java.util.Dictionary dict = new java.util.Hashtable();

    // ===========================================================
    // Constructors
    // ===========================================================

    private Symbol(String name) {
        this.name = name;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public String getName() {
        return name;
    }

    public boolean equals(String str) {
        return (this == Symbol.symbol(str));
    }

    public boolean equals(Symbol sym) {
        return (this == sym);
    }

    /** Returns the unique symbol associated with a string. */
    public static Symbol symbol(String str) {
        String inStr = str.intern();
        Symbol sym = (Symbol) dict.get(inStr);
        if (sym == null) {
            sym = new Symbol(inStr);
            dict.put(inStr, sym);
        }
        return sym;
    }

    public static Symbol creatOldSymbol(Symbol sym) {
        String str = "#" + sym.toString();
        return Symbol.symbol(str);
    }

    public String asString(int indent, int increment) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        sb.append(this.toString() + "\n");
        return sb.toString();
    }

    public String toString() {
        return name;
    }

    public int compareTo(Symbol o) {
        return name.compareTo(o.name);
    }
}
